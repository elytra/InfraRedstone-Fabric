package com.elytradev.infraredstone.block.entity;

import com.elytradev.infraredstone.api.InfraRedstoneCapable;
import com.elytradev.infraredstone.api.InfraRedstoneSignal;
import com.elytradev.infraredstone.api.MultimeterProbeProvider;
import com.elytradev.infraredstone.block.ModBlocks;
import com.elytradev.infraredstone.block.OscillatorBlock;
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
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.Chunk;

public class OscillatorBlockEntity extends IRComponentBlockEntity implements Tickable, MultimeterProbeProvider, InfraRedstoneCapable {

	private InfraRedstoneHandler signal = new InfraRedstoneHandler();
	private int refreshTicks;
	public int maxRefreshTicks = 4;
	private int sigToWrite;

	//Transient data to throttle sync down here
	boolean lastActive = false;

	@Environment(EnvType.CLIENT)
	boolean firstTick = true;

	public OscillatorBlockEntity() {
		super(ModBlocks.OSCILLATOR_BE);
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
			if (state.getBlock() instanceof OscillatorBlock) {
				Direction back = state.get(OscillatorBlock.FACING).getOpposite();
				int sig = InRedLogic.findIRValue(world, pos, back);
				if (sig != signal.getSignalValue()) {
					//in and out signals are different, check if it's in the middle of a refresh cycle
					if (refreshTicks <= 0) {
						//refresh cycle ended, set signal and grab next signal
						signal.setNextSignalValue(sigToWrite);
						sigToWrite = sig;
						refreshTicks = maxRefreshTicks;
					} else {
						//in the middle of a cycle, keep at what it currently is
						signal.setNextSignalValue(signal.getSignalValue());
					}
					refreshTicks -= 2;
					markDirty();
				}
			}
		} else {
			//Not an IR tick, so this is a "copy" tick. Adopt the previous tick's "next" value.
			signal.setSignalValue(signal.getNextSignalValue());
			markDirty();
			//setActive(state, signal.getSignalValue()!=0); //This is also when we light up
		}
	}

	public void setDelay() {
		if (maxRefreshTicks >= 100) maxRefreshTicks = 100;
		if (maxRefreshTicks < 1) maxRefreshTicks = 1;
		refreshTicks = maxRefreshTicks;
		markDirty();
	}

	@Override
	public CompoundTag toTag(CompoundTag compound) {
		CompoundTag tag = super.toTag(compound);
		tag.put("Signal", InfraRedstoneSerializer.serialize(signal, null));
		tag.putInt("NextSignal", sigToWrite);
		tag.putInt("CurrentRefresh", refreshTicks);
		tag.putInt("MaxRefresh", maxRefreshTicks);
		return tag;
	}

	@Override
	public void fromTag(CompoundTag compound) {
		super.fromTag(compound);
		if (compound.containsKey("Signal")) InfraRedstoneSerializer.deserialize(signal, null, compound.getTag("Signal"));
		sigToWrite = compound.getInt("NextSignal");
		refreshTicks = compound.getInt("CurrentRefresh");
		maxRefreshTicks = compound.getInt("MaxRefresh");
	}

	@Override
	public void markDirty() {
		super.markDirty();
		// again, I've copy-pasted this like 12 times, should probably go into Concrete
		if (!hasWorld() || getWorld().isClient) return;
		boolean active = isActive();
		if (active != lastActive || firstTick) { //Throttle updates - only send when something important changes

			ServerWorld ws = (ServerWorld) getWorld();
			Chunk c = getWorld().getChunk(getPos());
			for (ServerPlayerEntity player : getWorld().getPlayers(ServerPlayerEntity.class, Predicates.alwaysTrue())) {
				if (ws.getChunkManager().method_14154(player, c.getPos().x, c.getPos().z)) {
					InfraRedstoneNetworking.syncModule(this, player);
				}
			}

			if (lastActive != active || firstTick) {
				//BlockState isn't changing, but we need to notify the block in front of us so that vanilla redstone updates
				BlockState state = world.getBlockState(pos);
				world.updateNeighborsAlways(pos, ModBlocks.OSCILLATOR);
				world.updateListeners(pos, state, state, 1);
			}

			if (firstTick) firstTick = false;
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
		if (inspectingFrom==null) return signal;

		BlockState state = world.getBlockState(pos);
		if (state.getBlock()==ModBlocks.OSCILLATOR) {
			Direction oscillatorFront = state.get(OscillatorBlock.FACING);
			if (oscillatorFront==inspectingFrom) {
				return signal;
			} else if (oscillatorFront==inspectingFrom.getOpposite()) {
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
		if (state.getBlock()==ModBlocks.OSCILLATOR) {
			Direction oscillatorFront = state.get(OscillatorBlock.FACING);
			if (oscillatorFront==inspectingFrom) {
				return true;
			} else if (oscillatorFront==inspectingFrom.getOpposite()) {
				return true;
			} else {
				return false;
			}
		}

		return false;
	}
}
