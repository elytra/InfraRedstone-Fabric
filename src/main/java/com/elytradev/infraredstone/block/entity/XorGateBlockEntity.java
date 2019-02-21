package com.elytradev.infraredstone.block.entity;

import com.elytradev.infraredstone.api.InfraRedstoneCapable;
import com.elytradev.infraredstone.api.InfraRedstoneSignal;
import com.elytradev.infraredstone.api.MultimeterProbeProvider;
import com.elytradev.infraredstone.block.AndGateBlock;
import com.elytradev.infraredstone.block.ModBlocks;
import com.elytradev.infraredstone.block.XorGateBlock;
import com.elytradev.infraredstone.logic.InRedLogic;
import com.elytradev.infraredstone.logic.impl.InfraRedstoneHandler;
import com.elytradev.infraredstone.logic.impl.InfraRedstoneSerializer;
import com.elytradev.infraredstone.util.InfraRedstoneNetworking;
import com.google.common.base.Predicates;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.server.PlayerStream;
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

import java.util.Iterator;

public class XorGateBlockEntity extends IRComponentBlockEntity implements Tickable, MultimeterProbeProvider, InfraRedstoneCapable {

	private InfraRedstoneHandler signal = new InfraRedstoneHandler();
	private int valLeft;
	private int valRight;
	public boolean booleanMode;

	//Transient data to throttle sync down here
	boolean lastActive = false;
	int lastValLeft = 0;
	int lastValRight = 0;
	boolean lastBooleanMode = false;

	@Environment(EnvType.CLIENT)
	boolean firstTick = true;

	public XorGateBlockEntity() {
		super(ModBlocks.XOR_GATE_BE);
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
			if (state.getBlock() instanceof XorGateBlock) {
				Direction left = state.get(AndGateBlock.FACING).rotateYCounterclockwise();
				Direction right = state.get(AndGateBlock.FACING).rotateYClockwise();
				int sigLeft = InRedLogic.findIRValue(world, pos, left);
				int sigRight = InRedLogic.findIRValue(world, pos, right);
				valLeft = sigLeft;
				valRight = sigRight;
				if (!booleanMode) {
					signal.setNextSignalValue(sigLeft ^ sigRight);
				} else {
					if (sigLeft > 0 && sigRight == 0) {
						signal.setNextSignalValue(1);
					} else if (sigLeft == 0 && sigRight > 0) {
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
		tag.putInt("Left", valLeft);
		tag.putInt("Right", valRight);
		return tag;
	}

	@Override
	public void fromTag(CompoundTag compound) {
		super.fromTag(compound);
		if (compound.containsKey("Signal")) InfraRedstoneSerializer.deserialize(signal, null, compound.getTag("Signal"));
		booleanMode = compound.getBoolean("BooleanMode");
		valLeft = compound.getInt("Left");
		valRight = compound.getInt("Right");
	}

	@Override
	public void markDirty() {
		super.markDirty();
		// again, I've copy-pasted this like 12 times, should probably go into Concrete
		if (!hasWorld() || getWorld().isClient) return;
		boolean active = isActive();
		if (active != lastActive
				|| lastValLeft != valLeft
				|| lastValRight != valRight
				|| lastBooleanMode != booleanMode
				|| firstTick) { //Throttle updates - only send when something important changes

			for (Iterator itr = PlayerStream.watching(getWorld(), getPos()).iterator(); itr.hasNext();) {
				InfraRedstoneNetworking.syncModule(this, (ServerPlayerEntity) itr.next());
			}

			if (lastBooleanMode != booleanMode || firstTick) {
				world.updateNeighborsAlways(pos.offset(Direction.UP), ModBlocks.NOT_GATE);
			}
			if (lastActive != active || lastValLeft != valLeft || lastValRight != valRight || firstTick) {
				//BlockState isn't changing, but we need to notify the block in front of us so that vanilla redstone updates
				BlockState state = world.getBlockState(pos);
				world.updateNeighborsAlways(pos, ModBlocks.NOT_GATE);
				world.updateListeners(pos, state, state, 1);
			}

			lastBooleanMode = booleanMode;
			lastActive = isActive();
			lastValLeft = valLeft;
			lastValRight = valRight;
			if (firstTick) firstTick = false;
		}
	}

	public boolean isActive() {
		return signal.getSignalValue() != 0;
	}

	public boolean isLeftActive() {
		return valLeft!=0;
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
		if (inspectingFrom==null) return  signal;

		BlockState state = world.getBlockState(pos);
		if (state.getBlock()==ModBlocks.XOR_GATE) {
			Direction xorGateFront = state.get(XorGateBlock.FACING);
			if (xorGateFront==inspectingFrom) {
				return  signal;
			} else if (xorGateFront==inspectingFrom.rotateYCounterclockwise()) {
				return InfraRedstoneHandler.ALWAYS_OFF;
			} else if (xorGateFront==inspectingFrom.rotateYClockwise()) {
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
