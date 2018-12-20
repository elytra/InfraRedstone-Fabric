package com.elytradev.infraredstone.block.entity;

import com.elytradev.infraredstone.api.*;
import com.elytradev.infraredstone.block.EncoderBlock;
import com.elytradev.infraredstone.block.ModBlocks;
import com.elytradev.infraredstone.logic.InRedLogic;
import com.elytradev.infraredstone.logic.impl.InfraRedstoneHandler;
import com.elytradev.infraredstone.logic.impl.InfraRedstoneSerializer;
import com.elytradev.infraredstone.util.InfraRedstoneNetworking;
import com.google.common.base.Predicates;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import prospector.silk.fluid.FluidContainer;
import prospector.silk.fluid.FluidInstance;

public class EncoderBlockEntity extends  IRComponentBlockEntity implements Tickable, MultimeterProbeProvider, InfraRedstoneCapable {

	private InfraRedstoneHandler signal = new InfraRedstoneHandler();

	//Transient data to throttle sync down here
	boolean lastActive = false;

	@Environment(EnvType.CLIENT)
	boolean firstTick = true;

	public EncoderBlockEntity() {
		super(ModBlocks.ENCODER_BE);
	}

	@Override
	public void tick() {
		if (world.isClient && firstTick) {
			InfraRedstoneNetworking.requestModule(this);
			firstTick = false;
			markDirty();
		}
		if (world.isClient || !hasWorld()) return;

		BlockState state = world.getBlockState(this.getPos());

		if (InRedLogic.isIRTick()) {
			//IR tick means we're searching for a next value
			if (state.getBlock() instanceof EncoderBlock) {
				Direction back = state.get(EncoderBlock.FACING).getOpposite();
				BlockPos backPos = this.getPos().offset(back);
				BlockState quantify = world.getBlockState(backPos);
				// check for the main encoder API
				if (quantify instanceof EncoderScannable) {
					signal.setNextSignalValue(((EncoderScannable) quantify).getEncoderValue(back.getOpposite()));
					// make sure we don't hit the if later down
					markDirty();
					return;
					// check for the non-TE encoder API
				} else if (quantify instanceof SimpleEncoderScannable) {
					signal.setNextSignalValue(((SimpleEncoderScannable) quantify).getEncoderValue(world, backPos, quantify, back.getOpposite()));
					markDirty();
					return;
					// no encoder API, so check for a tile entity
				} else if (world.getBlockEntity(backPos) != null) {
					BlockEntity be = world.getBlockEntity(backPos);
					// check for capabilities on the tile entity, make sure we only move on if we don't find any
					if (be instanceof Inventory) {
						Inventory inv = (Inventory)be;
						int stacksChecked = 0;
						float fillPercentage = 0f;
						for (int i = 0; i < (inv.getInvSize() ); i++) {
							ItemStack stack = inv.getInvStack(i);
							if (!stack.isEmpty()) {
								fillPercentage += (float)stack.getAmount() / (float)Math.min(inv.getInvMaxStackAmount(), stack.getMaxAmount());
								stacksChecked++;
							}
						}
						fillPercentage /= (float)inv.getInvSize();
						signal.setNextSignalValue(MathHelper.floor(fillPercentage * 62.0F) + (stacksChecked > 0 ? 1 : 0));
						markDirty();
						return;
					}
					// Fluid containers don't have any way to know capacity externally yet, so this has to be commented out for now
//					if (be instanceof FluidContainer) {
//						FluidContainer cont = (FluidContainer)be;
//						FluidInstance[] fluids = cont.getFluids(back.getOpposite());
//						int fluidsChecked = 0;
//						float fillPercentage = 0f;
//						for (FluidInstance fluid : fluids) {
//							fillPercentage += (float)fluid.getAmount();
//						}
//					}
					// check for a vanilla comparator interface
				} if (quantify.hasComparatorOutput()) {
					signal.setNextSignalValue(4*quantify.getComparatorOutput(world, backPos));
					// can't find anything else, so check for redstone/inred signal
				} else {
					// redstone first so inred's redstone-catching doesn't override it
					int sigBack = world.getEmittedRedstonePower(backPos, back);
					if (sigBack != 0) {
						signal.setNextSignalValue(sigBack);
					} else {
						signal.setNextSignalValue(InRedLogic.findIRValue(world, pos, back));
					}
				}

				markDirty();
			}
		} else {
			//Not an IR tick, so this is a "copy" tick. Adopt the previous tick's "next" value.
			signal.setSignalValue(signal.getNextSignalValue());
			markDirty();
			//setActive(state, signal.getSignalValue()!=0); //This is also when we light up
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag compound) {
		CompoundTag tag = super.toTag(compound);
		tag.put("Signal", InfraRedstoneSerializer.serialize(signal, null));
		return tag;
	}

	@Override
	public void fromTag(CompoundTag compound) {
		super.fromTag(compound);
		if (compound.containsKey("Signal")) InfraRedstoneSerializer.deserialize(signal, null, compound.getTag("Signal"));
	}

	@Override
	public void markDirty() {
		super.markDirty();
		// again, I've copy-pasted this like 12 times, should probably go into Concrete
		if (!hasWorld() || getWorld().isClient) return;
		boolean active = isActive();
		if (active != lastActive) { //Throttle updates - only send when something important changes

			ServerWorld ws = (ServerWorld) getWorld();
			Chunk c = getWorld().getChunk(getPos());
			for (ServerPlayerEntity player : getWorld().getPlayers(ServerPlayerEntity.class, Predicates.alwaysTrue())) {
				if (ws.getChunkManager().method_14154(player, c.getPos().x, c.getPos().z)) {
					InfraRedstoneNetworking.syncModule(this, player);
				}
			}

			if (lastActive != active) {
				//BlockState isn't changing, but we need to notify the block in front of us so that vanilla redstone updates
				BlockState state = world.getBlockState(pos);
				world.updateNeighborsAlways(pos, ModBlocks.ENCODER);
				world.updateListeners(pos, state, state, 1);
			}

			lastActive = active;
		}
	}
	
	public boolean isActive() {
		return signal.getSignalValue() != 0;
	}
	
	@Override
	public StringTextComponent getProbeMessage() {
		TranslatableTextComponent i18n = new TranslatableTextComponent("msg.inred.multimeter.out");
		return new StringTextComponent(i18n.getFormattedText()+getValue(signal));
	}

	@Override
	public InfraRedstoneSignal getInfraRedstoneHandler(Direction inspectingFrom) {
		if (world==null) return InfraRedstoneHandler.ALWAYS_OFF;
		if (inspectingFrom==null) return  signal;

		BlockState state = world.getBlockState(pos);
		if (state.getBlock()==ModBlocks.ENCODER) {
			Direction encoderFront = state.get(EncoderBlock.FACING);
			if (encoderFront==inspectingFrom) {
				return  signal;
			} else if (encoderFront==inspectingFrom.getOpposite()) {
				return InfraRedstoneHandler.ALWAYS_OFF;
			} else {
				return null;
			}
		}
		return InfraRedstoneHandler.ALWAYS_OFF; //We can't tell what our front face is, so supply a dummy that's always-off.
	}

	@Override
	public boolean canConnectToSide(Direction inspectingFrom) {
		if (world==null) return true;
		if (inspectingFrom==null) return true;
		BlockState state = world.getBlockState(pos);
		if (state.getBlock()==ModBlocks.ENCODER) {
			Direction encoderFront = state.get(EncoderBlock.FACING);
			if (encoderFront==inspectingFrom) {
				return true;
			} else return encoderFront == inspectingFrom.getOpposite();
		}

		return false;
	}
}
