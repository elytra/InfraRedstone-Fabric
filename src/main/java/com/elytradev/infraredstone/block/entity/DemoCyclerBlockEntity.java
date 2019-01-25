package com.elytradev.infraredstone.block.entity;

import com.elytradev.infraredstone.api.InfraRedstoneCapable;
import com.elytradev.infraredstone.api.InfraRedstoneSignal;
import com.elytradev.infraredstone.block.DemoCyclerBlock;
import com.elytradev.infraredstone.block.ModBlocks;
import com.elytradev.infraredstone.logic.InRedLogic;
import com.elytradev.infraredstone.logic.impl.InfraRedstoneHandler;
import com.elytradev.infraredstone.logic.impl.InfraRedstoneSerializer;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class DemoCyclerBlockEntity extends IRComponentBlockEntity implements Tickable, InfraRedstoneCapable {

	private InfraRedstoneHandler signal = new InfraRedstoneHandler();

	private boolean active;
	private boolean increasing;
	private int pauseTicks;

	public DemoCyclerBlockEntity() {
		super(ModBlocks.DEMO_CYCLE_BE);
	}

	@Override
	public void tick() {
		if (world.isClient || !hasWorld()) return;

		BlockState state = world.getBlockState(this.getPos());

		if (InRedLogic.isIRTick()) {
			//IR tick means we're searching for a next value
			if (state.getBlock() instanceof DemoCyclerBlock) {
				if (active) {
					if (increasing) {
						if (signal.getSignalValue() < 63) {
							signal.setNextSignalValue(signal.getSignalValue() + 1);
						} else if (pauseTicks < 20) {
							signal.setNextSignalValue(63);
							pauseTicks++;
						} else {
							increasing = false;
							pauseTicks = 0;
						}
					} else {
						if (signal.getSignalValue() > 0) {
							signal.setNextSignalValue(signal.getSignalValue() - 1);
						} else {
							signal.setNextSignalValue(0);
							increasing = true;
							active = false;
						}
					}
				}
				markDirty();
			}
		} else {
			//Not an IR tick, so this is a "copy" tick. Adopt the previous tick's "next" value.
			signal.setSignalValue(signal.getNextSignalValue());
			markDirty();
		}
	}

	public void activate() {
		active = true;
		pauseTicks = 0;
		increasing = true;
		markDirty();
	}

	@Override
	public CompoundTag toTag(CompoundTag compound) {
		CompoundTag tag = super.toTag(compound);
		tag.put("Signal", InfraRedstoneSerializer.serialize(signal, null));
		tag.putBoolean("Active", active);
		tag.putBoolean("Increasing", increasing);
		tag.putInt("Pause", pauseTicks);
		return tag;
	}

	@Override
	public void fromTag(CompoundTag compound) {
		super.fromTag(compound);
		if (compound.containsKey("Signal")) InfraRedstoneSerializer.deserialize(signal, null, compound.getTag("Signal"));
		active = compound.getBoolean("Active");
		increasing = compound.getBoolean("Increasing");
		pauseTicks = compound.getInt("Pause");
	}

	@Override
	public InfraRedstoneSignal getInfraRedstoneHandler(Direction inspectingFrom) {
		return signal;
	}

	@Override
	public boolean canConnectIR(BlockPos dest, Direction inspectingFrom) {
		return true;
	}
}
