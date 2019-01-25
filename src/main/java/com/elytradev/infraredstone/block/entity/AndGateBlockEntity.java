package com.elytradev.infraredstone.block.entity;

import com.elytradev.infraredstone.api.InfraRedstoneCapable;
import com.elytradev.infraredstone.api.InfraRedstoneSignal;
import com.elytradev.infraredstone.api.MultimeterProbeProvider;
import com.elytradev.infraredstone.block.AndGateBlock;
import com.elytradev.infraredstone.block.ModBlocks;
import com.elytradev.infraredstone.logic.InRedLogic;
import com.elytradev.infraredstone.logic.impl.InfraRedstoneHandler;
import com.elytradev.infraredstone.logic.impl.InfraRedstoneSerializer;
import com.elytradev.infraredstone.util.InfraRedstoneNetworking;
import com.elytradev.infraredstone.util.enums.InactiveSelection;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.List;

public class AndGateBlockEntity extends IRComponentBlockEntity implements Tickable, MultimeterProbeProvider, InfraRedstoneCapable {

	private InfraRedstoneHandler signal = new InfraRedstoneHandler();
	public boolean booleanMode;
	private int valLeft;
	private int valBack;
	private int valRight;
	public InactiveSelection inactive = InactiveSelection.NONE;

	//Transient data to throttle sync down here
	boolean lastActive = false;
	boolean lastBooleanMode = false;
	int lastValLeft = 0;
	int lastValBack = 0;
	int lastValRight = 0;
	InactiveSelection lastInactive = InactiveSelection.NONE;

	@Environment(EnvType.CLIENT)
	boolean firstTick = true;

	public AndGateBlockEntity() {
		super(ModBlocks.AND_GATE_BE);
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
			if (state.getBlock() instanceof AndGateBlock) {
				Direction left = state.get(AndGateBlock.FACING).rotateYCounterclockwise();
				Direction right = state.get(AndGateBlock.FACING).rotateYClockwise();
				Direction back = state.get(AndGateBlock.FACING).getOpposite();
				int sigLeft = InRedLogic.findIRValue(world, pos, left);
				int sigRight = InRedLogic.findIRValue(world, pos, right);
				int sigBack = InRedLogic.findIRValue(world, pos, back);
				List<Integer> signals = new ArrayList<>();

				valLeft = sigLeft;
				valRight = sigRight;
				valBack = sigBack;
				int result = 0b11_1111; //63

				if (!booleanMode) {
					switch (inactive) {
						case LEFT:
							signals.add(sigBack);
							signals.add(sigRight);
							break;
						case BACK:
							signals.add(sigLeft);
							signals.add(sigRight);
							break;
						case RIGHT:
							signals.add(sigLeft);
							signals.add(sigBack);
							break;
						case NONE:
							signals.add(sigLeft);
							signals.add(sigBack);
							signals.add(sigRight);
					}

					for (int signal : signals) {
						// if any input added to signal is 0b00_0000, will result in no output
						result &= signal;
					}
				} else {
					switch (inactive) {
						case LEFT:
							result = (sigBack > 0 && sigRight > 0)? 1 : 0;
							break;
						case BACK:
							result = (sigLeft > 0 && sigRight > 0)? 1 : 0;
							break;
						case RIGHT:
							result = (sigLeft > 0 && sigBack > 0)? 1 : 0;
							break;
						case NONE:
							result = (sigLeft > 0 && sigBack > 0 && sigRight > 0)? 1 : 0;
					}
				}

				signal.setNextSignalValue(result);
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

	public void toggleInactive(InactiveSelection newInactive) {
		if (inactive == newInactive) {
			inactive = InactiveSelection.NONE;
		} else {
			inactive = newInactive;
		}
		world.playSound(null, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCK, 0.3f, 0.45f);
		markDirty();
	}

	@Override
	public CompoundTag toTag(CompoundTag compound) {
		CompoundTag tag = super.toTag(compound);
		tag.put("Signal", InfraRedstoneSerializer.serialize(signal, null));
		tag.putBoolean("BooleanMode", booleanMode);
		tag.putInt("Left", valLeft);
		tag.putInt("Back", valBack);
		tag.putInt("Right", valRight);
		tag.putString("Inactive", inactive.asString());
		return tag;
	}

	@Override
	public void fromTag(CompoundTag compound) {
		super.fromTag(compound);
		if (compound.containsKey("Signal")) InfraRedstoneSerializer.deserialize(signal, null, compound.getTag("Signal"));
		booleanMode = compound.getBoolean("BooleanMode");
		valLeft = compound.getInt("Left");
		valBack = compound.getInt("Back");
		valRight = compound.getInt("Right");
		inactive = InactiveSelection.forName(compound.getString("Inactive"));
	}

	@Override
	public void markDirty() {
		super.markDirty();
		// again, I've copy-pasted this like 12 times, should probably go into Concrete
		if (!hasWorld() || getWorld().isClient) return;
		boolean active = isActive();
		if (active != lastActive
				|| inactive!=lastInactive
				|| valLeft!=lastValLeft
				|| valRight!=lastValRight
				|| valBack!=lastValBack
				|| lastBooleanMode != booleanMode
				|| firstTick) { //Throttle updates - only send when something important changes

			ServerWorld ws = (ServerWorld) getWorld();
			Chunk c = getWorld().getChunk(getPos());
			for (ServerPlayerEntity player : getWorld().getPlayers(ServerPlayerEntity.class, Predicates.alwaysTrue())) {
				if (ws.getChunkManager().method_14154(player, c.getPos().x, c.getPos().z)) {
					InfraRedstoneNetworking.syncModule(this, player);
				}
			}

			if (lastBooleanMode != booleanMode || inactive != lastInactive || firstTick) {
				world.updateNeighborsAlways(pos.offset(Direction.UP), ModBlocks.NOT_GATE);
			}
			if (lastActive != active
					|| valLeft!=lastValLeft
					|| valRight!=lastValRight
					|| valBack!=lastValBack
					|| firstTick) {
				//BlockState isn't changing, but we need to notify the block in front of us so that vanilla redstone updates
				BlockState state = world.getBlockState(pos);
				world.updateNeighborsAlways(pos, ModBlocks.NOT_GATE);
				world.updateListeners(pos, state, state, 1);
			}

			lastBooleanMode = booleanMode;
			lastActive = isActive();
			lastValLeft = valLeft;
			lastValRight = valRight;
			lastValBack = valBack;
			lastInactive = inactive;
			if (firstTick) firstTick = false;
		}
	}

	public boolean isActive() {
		return signal.getSignalValue() != 0;
	}

	public boolean isLeftActive() {
		return valLeft!=0;
	}
	public boolean isBackActive() {
		return valBack!=0;
	}
	public boolean isRightActive() {
		return valRight!=0;
	}

	@Override
	public StringTextComponent getProbeMessage() {
		TranslatableTextComponent i18n = new TranslatableTextComponent("msg.inred.multimeter.out");
		return new StringTextComponent(i18n.getFormattedText()+getValue(signal));
	}

	@Override
	public InfraRedstoneSignal getInfraRedstoneHandler(Direction inspectingFrom) {
		if (inspectingFrom == Direction.DOWN || inspectingFrom == Direction.UP) return null;
		if (world==null) return InfraRedstoneHandler.ALWAYS_OFF;
		if (inspectingFrom==null) return signal;

		BlockState state = world.getBlockState(pos);
		if (state.getBlock()==ModBlocks.AND_GATE) {
			Direction andGateFront = state.get(AndGateBlock.FACING);
			if (andGateFront==inspectingFrom) {
				return signal;
			} else if (andGateFront==inspectingFrom.getOpposite()) {
				return InfraRedstoneHandler.ALWAYS_OFF;
			} else if (andGateFront==inspectingFrom.rotateYCounterclockwise()) {
				return InfraRedstoneHandler.ALWAYS_OFF;
			} else if (andGateFront==inspectingFrom.rotateYClockwise()) {
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
		
		//Any purely horizontal direction is fine for us.
		return true;
	}
}
