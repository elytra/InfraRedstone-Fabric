package com.elytradev.infraredstone.block.entity;

import com.elytradev.infraredstone.api.InfraRedstoneCapable;
import com.elytradev.infraredstone.api.InfraRedstoneSignal;
import com.elytradev.infraredstone.api.MultimeterProbeProvider;
import com.elytradev.infraredstone.block.ModBlocks;
import com.elytradev.infraredstone.block.ShifterBlock;
import com.elytradev.infraredstone.logic.InRedLogic;
import com.elytradev.infraredstone.logic.impl.InfraRedstoneHandler;
import com.elytradev.infraredstone.logic.impl.InfraRedstoneSerializer;
import com.elytradev.infraredstone.util.InfraRedstoneNetworking;
import com.elytradev.infraredstone.util.enums.ShifterSelection;
import com.google.common.base.Predicates;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.Chunk;

public class ShifterBlockEntity extends IRComponentBlockEntity implements Tickable, MultimeterProbeProvider, InfraRedstoneCapable {

	private InfraRedstoneHandler signal = new InfraRedstoneHandler();
	private InfraRedstoneHandler eject = new InfraRedstoneHandler();
	public ShifterSelection selection = ShifterSelection.LEFT;

	//Transient data to throttle sync down here
	boolean lastActive = false;
	boolean lastEject = false;
	ShifterSelection lastSelection = ShifterSelection.LEFT;

	@Environment(EnvType.CLIENT)
	boolean firstTick = true;

	public ShifterBlockEntity() {
		super(ModBlocks.SHIFTER_BE);
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
			if (state.getBlock() instanceof ShifterBlock) {
				Direction back = state.get(ShifterBlock.FACING).getOpposite();
				int sig = InRedLogic.findIRValue(world, pos, back);
				int ej = 0;

				if (selection == ShifterSelection.LEFT) {
					ej = (sig & 0b10_0000);
					ej = (ej != 0) ? 1 : 0;
					sig <<= 1;
					sig &= 0b011_1111;
				} else {
					ej = (sig & 0b00_0001);
					ej = (ej != 0) ? 1 : 0;
					sig >>>= 1;
					sig &= 0b011_1111;
				}

				signal.setNextSignalValue(sig);
				eject.setNextSignalValue(ej);
				markDirty();
			}
		} else {
			//Not an IR tick, so this is a "copy" tick. Adopt the previous tick's "next" value.
			signal.setSignalValue(signal.getNextSignalValue());
			eject.setSignalValue(eject.getNextSignalValue());
			markDirty();
		}
	}

	public void toggleSelection() {
		if (selection == ShifterSelection.LEFT) {
			selection = ShifterSelection.RIGHT;
			world.playSound(null, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCK, 0.3f, 0.55f);
		} else {
			selection = ShifterSelection.LEFT;
			world.playSound(null, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCK, 0.3f, 0.55f);
		}
		eject.setNextSignalValue(0);
		eject.setSignalValue(0);
		markDirty();
	}

	@Override
	public CompoundTag toTag(CompoundTag compound) {
		CompoundTag tag = super.toTag(compound);
		tag.put("Signal", InfraRedstoneSerializer.serialize(signal, null));
		tag.put("Eject", InfraRedstoneSerializer.serialize(eject, null));
		tag.putString("Selection", selection.toString());
		return tag;
	}

	@Override
	public void fromTag(CompoundTag compound) {
		super.fromTag(compound);
		if (compound.containsKey("Signal")) InfraRedstoneSerializer.deserialize(signal, null, compound.getTag("Signal"));
		if (compound.containsKey("Eject")) InfraRedstoneSerializer.deserialize(eject, null, compound.getTag("Eject"));
		selection = ShifterSelection.forName(compound.getString("Selection"));
	}

	@Override
	public void markDirty() {
		super.markDirty();
		// again, I've copy-pasted this like 12 times, should probably go into Concrete
		if (!hasWorld() || getWorld().isClient) return;
		boolean active = isActive();
		boolean eject = isEject();
		if (active != lastActive
				|| lastEject != eject
				|| lastSelection != selection
				|| firstTick) { //Throttle updates - only send when something important changes

			ServerWorld ws = (ServerWorld) getWorld();
			Chunk c = getWorld().getChunk(getPos());
			for (ServerPlayerEntity player : getWorld().getPlayers(ServerPlayerEntity.class, Predicates.alwaysTrue())) {
				if (ws.getChunkManager().method_14154(player, c.getPos().x, c.getPos().z)) {
					InfraRedstoneNetworking.syncModule(this, player);
				}
			}

			if (lastSelection != selection || firstTick) {
				world.updateNeighborsAlways(pos.offset(Direction.UP), ModBlocks.SHIFTER);
			}
			if (lastActive != active || lastEject != eject || firstTick) {
				//BlockState isn't changing, but we need to notify the block in front of us so that vanilla redstone updates
				BlockState state = world.getBlockState(pos);
				world.updateNeighborsAlways(pos, ModBlocks.SHIFTER);
				world.updateListeners(pos, state, state, 1);
			}

			lastSelection = selection;
			lastActive = isActive();
			lastEject = isEject();
			if (firstTick) firstTick = false;
		}
	}

	public boolean isActive() {
		return signal.getSignalValue() != 0;
	}

	public boolean isEject() {
		return eject.getSignalValue() != 0;
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
		if (state.getBlock()==ModBlocks.SHIFTER) {
			Direction shifterFront = state.get(ShifterBlock.FACING);
			if (shifterFront==inspectingFrom) {
				return signal;
			} else if (shifterFront.rotateYCounterclockwise()==inspectingFrom) {
				if (selection == ShifterSelection.LEFT) return eject;
				else return InfraRedstoneHandler.ALWAYS_OFF;
			} else if (shifterFront.rotateYClockwise()==inspectingFrom) {
				if (selection == ShifterSelection.RIGHT) return eject;
				else return InfraRedstoneHandler.ALWAYS_OFF;
			} else if (shifterFront.getOpposite()==inspectingFrom) {
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
		if (state.getBlock()==ModBlocks.SHIFTER) {
			Direction shifterFront = state.get(ShifterBlock.FACING);
			if (shifterFront==inspectingFrom) {
				return true;
			} else if (shifterFront.rotateYCounterclockwise()==inspectingFrom) {
				return selection == ShifterSelection.LEFT;
			} else if (shifterFront.rotateYClockwise()==inspectingFrom) {
				return selection == ShifterSelection.RIGHT;
			} else if (shifterFront.getOpposite()==inspectingFrom) {
				return true;
			} else {
				return false;
			}
		}

		return false;
	}
}
