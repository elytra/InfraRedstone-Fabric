package com.elytradev.infraredstone.block.entity;

import com.elytradev.infraredstone.api.*;
import com.elytradev.infraredstone.block.NotGateBlock;
import com.elytradev.infraredstone.block.DiodeBlock;
import com.elytradev.infraredstone.block.ModBlocks;
import com.elytradev.infraredstone.logic.InRedLogic;
import com.elytradev.infraredstone.logic.impl.InfraRedstoneHandler;
import com.elytradev.infraredstone.logic.impl.InfraRedstoneSerializer;
import com.elytradev.infraredstone.util.InfraRedstoneNetworking;
import com.google.common.base.Predicates;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.Chunk;

public class NotGateBlockEntity extends IRComponentBlockEntity implements Tickable, MultimeterProbeProvider, InfraRedstoneCapable {

	private InfraRedstoneHandler signal = new InfraRedstoneHandler();
	public boolean booleanMode;
	public boolean backActive;

	//Transient data to throttle sync down here
	boolean lastActive = false;
	boolean lastBooleanMode = false;
	boolean lastBackActive = false;

	@Environment(EnvType.CLIENT)
	boolean firstTick = true;

	public NotGateBlockEntity() {
		super(ModBlocks.NOT_GATE_BE);
	}

	@Override
	public void tick() {
		if (world.isClient && firstTick) {
			InfraRedstoneNetworking.requestModule(this);
			markDirty();
		}
		if (world.isClient || !hasWorld()) return;

		BlockState state = world.getBlockState(this.getPos());

		if (InRedLogic.isIRTick()) {
			//IR tick means we're searching for a next value
			if (state.getBlock() instanceof NotGateBlock) {
				Direction back = state.get(NotGateBlock.FACING).getOpposite();
				int sig = InRedLogic.findIRValue(world, pos, back);
				backActive = sig != 0;
				if (!booleanMode) {
					signal.setNextSignalValue((~sig) & 0b11_1111);
				} else {
					if (sig == 0) {
						signal.setNextSignalValue(1);
					} else {
						signal.setNextSignalValue(0);
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

	public void toggleBooleanMode() {
		if (booleanMode) {
			booleanMode = false;
			world.playSound(null, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCK, 0.3f, 0.5f);
		} else {
			booleanMode = true;
			world.playSound(null, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCK, 0.3f, 0.55f);
		}
		markDirty();
	}

	@Override
	public CompoundTag toTag(CompoundTag compound) {
		CompoundTag tag = super.toTag(compound);
		tag.put("Signal", InfraRedstoneSerializer.serialize(signal, null));
		tag.putBoolean("BooleanMode", booleanMode);
		tag.putBoolean("BackActive", backActive);
		return tag;
	}

	@Override
	public void fromTag(CompoundTag compound) {
		super.fromTag(compound);
		if (compound.containsKey("Signal")) InfraRedstoneSerializer.deserialize(signal, null, compound.getTag("Signal"));
		booleanMode = compound.getBoolean("BooleanMode");
		backActive = compound.getBoolean("BackActive");
	}

	@Override
	public void markDirty() {
		super.markDirty();
		// again, I've copy-pasted this like 12 times, should probably go into Concrete
		if (!hasWorld() || getWorld().isClient) return;
		boolean active = isActive();
		if (active != lastActive
				|| lastBackActive != backActive
				|| lastBooleanMode != booleanMode
				|| firstTick) { //Throttle updates - only send when something important changes

			ServerWorld ws = (ServerWorld) getWorld();
			Chunk c = getWorld().getChunk(getPos());
			for (ServerPlayerEntity player : ((ServerWorld) getWorld()).method_18766(Predicates.alwaysTrue())) {
				if (ws.getChunkManager().isChunkLoaded(c.getPos().x, c.getPos().z)) {
					InfraRedstoneNetworking.syncModule(this, player);
				}
			}

			if (lastBooleanMode != booleanMode || firstTick) {
				world.updateNeighborsAlways(pos.offset(Direction.UP), ModBlocks.NOT_GATE);
			}
			if (lastActive != active || lastBackActive != backActive || firstTick) {
				//BlockState isn't changing, but we need to notify the block in front of us so that vanilla redstone updates
				BlockState state = world.getBlockState(pos);
				world.updateNeighborsAlways(pos, ModBlocks.NOT_GATE);
				world.updateListeners(pos, state, state, 1);
			}

			lastBooleanMode = booleanMode;
			lastActive = isActive();
			lastBackActive = backActive;
			if (firstTick) firstTick = false;
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
		if (state.getBlock()==ModBlocks.NOT_GATE) {
			Direction notGateFront = state.get(NotGateBlock.FACING);
			if (notGateFront==inspectingFrom) {
				return  signal;
			} else if (notGateFront==inspectingFrom.getOpposite()) {
				return InfraRedstoneHandler.ALWAYS_OFF;
			} else {
				return null;
			}
		}
		return InfraRedstoneHandler.ALWAYS_OFF; //We can't tell what our front face is, so supply a dummy that's always-off.
	}
	
	@Override
	public boolean canConnectIR(BlockPos dest, Direction dir) {
		//We can't connect vertically.
		if (dir == Direction.DOWN || dir == Direction.UP) return false;
		
		//Prevent connections to Y values above or below us
		if (dest.getY()!=pos.getY()) return false;
		
		if (world==null) return true;
		BlockState state = world.getBlockState(pos);
		if (state.contains(Properties.FACING_HORIZONTAL)) {
			Direction front = state.get(Properties.FACING_HORIZONTAL);
			
			return  //We can connect straight in front of or behind us
					dir==front ||
					dir==front.getOpposite();
		} else {
			//We got the wrong block
			return false;
		}
	}
}
