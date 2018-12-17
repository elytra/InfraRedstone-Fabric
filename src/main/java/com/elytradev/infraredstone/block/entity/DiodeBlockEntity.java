package com.elytradev.infraredstone.block.entity;

import com.elytradev.infraredstone.api.IInfraRedstone;
import com.elytradev.infraredstone.api.IMultimeterProbe;
import com.elytradev.infraredstone.api.InfraRedstoneCapable;
import com.elytradev.infraredstone.block.BlockBase;
import com.elytradev.infraredstone.block.DiodeBlock;
import com.elytradev.infraredstone.block.ModBlocks;
import com.elytradev.infraredstone.logic.InRedLogic;
import com.elytradev.infraredstone.logic.impl.InfraRedstoneHandler;
import com.elytradev.infraredstone.logic.impl.InfraRedstoneSerializer;
import com.google.common.base.Predicates;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.Chunk;

public class DiodeBlockEntity extends BlockEntity implements Tickable, IMultimeterProbe, InfraRedstoneCapable {
	private InfraRedstoneHandler signal = new InfraRedstoneHandler();
	private int mask = 0b11_1111;

	//Transient data to throttle sync down here
	boolean lastActive = false;
	int lastMask = 0b11_1111;

	public DiodeBlockEntity() {
		super(ModBlocks.DIODE_BE);
	}

	@Override
	public void tick() {
		if (world.isClient || !hasWorld()) return;

		BlockState state = world.getBlockState(this.getPos());

		if (InRedLogic.isIRTick()) {
			//IR tick means we're searching for a next value
			if (state.getBlock() instanceof DiodeBlock) {
				Direction back = state.get(DiodeBlock.FACING).getOpposite();
				int sig = InRedLogic.findIRValue(world, pos, back);
				System.out.println("IR tick: "+sig);
				signal.setNextSignalValue(sig & mask);
				markDirty();
			}
		} else {
			//Not an IR tick, so this is a "copy" tick. Adopt the previous tick's "next" value.
			System.out.println("non-IR tick: "+signal.getNextSignalValue());
			signal.setSignalValue(signal.getNextSignalValue());
			markDirty();
			//setActive(state, signal.getSignalValue()!=0); //This is also when we light up
		}
	}

	public void setMask(int bit) {
		mask ^= (1 << bit);
		world.playSound(null, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCK, 0.3f, 0.45f);
		markDirty();
	}

	@Override
	public CompoundTag toTag(CompoundTag compound) {
		CompoundTag tag = super.toTag(compound);
		tag.putInt("Mask", mask);
		tag.put("Signal", InfraRedstoneSerializer.serialize(signal, null));
		return tag;
	}

	@Override
	public void fromTag(CompoundTag compound) {
		super.fromTag(compound);
		mask = compound.getInt("Mask");
		if (compound.containsKey("Signal")) InfraRedstoneSerializer.deserialize(signal, null, compound.getTag("Signal"));
	}

	//TODO: find out if this is still needed
//	@Override
//	public void markDirty() {
//		super.markDirty();
//		// again, I've copy-pasted this like 12 times, should probably go into Concrete
//		if (!hasWorld() || getWorld().isClient) return;
//		boolean active = isActive();
//		if (mask!=lastMask || active!=lastActive) { //Throttle updates - only send when something important changes
//
//			ServerWorld ws = (ServerWorld) getWorld();
//			Chunk c = getWorld().getChunk(getPos());
//			SPacketUpdateBlockEntity packet = new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
//			for (ServerPlayerEntity player : getWorld().getPlayers(ServerPlayerEntity.class, Predicates.alwaysTrue())) {
//				if (ws.getChunkManager().method_14154(player, c.getPos().x, c.getPos().z)) {
//					player.networkHandler.sendPacket(packet);
//				}
//			}
//
//			if (lastMask!=mask) {
//				//BlockState state = world.getBlockState(pos);
//				//ws.markAndNotifyBlock(pos, c, state, state, 1 | 2 | 16);
//			} else if (lastActive!=active) {
//				//BlockState isn't changing, but we need to notify the block in front of us so that vanilla redstone updates
//				BlockState state = world.getBlockState(pos);
//				if (state.getBlock()==ModBlocks.DIODE) {
//					Direction facing = state.get(DiodeBlock.FACING);
//					BlockPos targetPos = pos.offset(facing);
//					BlockState targetState = world.getBlockState(targetPos);
//					if (!(targetState.getBlock() instanceof BlockBase)) {
//						//Not one of ours. Update its redstone, and let observers see the fact that we updated too
//						world.markAndNotifyBlock(pos, world.getChunk(pos), state, state, 1);
//						world.markAndNotifyBlock(targetPos, world.getChunk(targetPos), targetState, targetState, 3); // 1 : Just cuase a BUD and notify observers
//					}
//				}
//			}
//
//			lastMask = mask;
//			lastActive = active;
//		}
//	}

	public int getMask() {
		return mask;
	}

	public boolean isActive() {
		return signal.getSignalValue() != 0;
	}


	@Override
	public StringTextComponent getProbeMessage() {
		TranslatableTextComponent i18n = new TranslatableTextComponent("msg.inred.multimeter.out");
		return new StringTextComponent(i18n.getFormattedText()+getValue());
	}

	private String getValue() {
		int signal = this.signal.getSignalValue();
		int bit1 = ((signal & 0b00_0001) != 0) ? 1:0;
		int bit2 = ((signal & 0b00_0010) != 0) ? 1:0;
		int bit3 = ((signal & 0b00_0100) != 0) ? 1:0;
		int bit4 = ((signal & 0b00_1000) != 0) ? 1:0;
		int bit5 = ((signal & 0b01_0000) != 0) ? 1:0;
		int bit6 = ((signal & 0b10_0000) != 0) ? 1:0;
		return ": 0b"+bit6+bit5+"_"+bit4+bit3+bit2+bit1+" ("+signal+")";
	}

	@Override
	public IInfraRedstone getInfraRedstoneHandler(Direction inspectingFrom) {
		if (world==null) return InfraRedstoneHandler.ALWAYS_OFF;
		if (inspectingFrom==null) return  signal;

		BlockState state = world.getBlockState(pos);
		if (state.getBlock()==ModBlocks.DIODE) {
			Direction diodeFront = state.get(DiodeBlock.FACING);
			if (diodeFront==inspectingFrom) {
				return  signal;
			} else if (diodeFront==inspectingFrom.getOpposite()) {
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
		if (state.getBlock()==ModBlocks.DIODE) {
			Direction diodeFront = state.get(DiodeBlock.FACING);
			if (diodeFront==inspectingFrom) {
				return true;
			} else return diodeFront == inspectingFrom.getOpposite();
		}

		return false;
	}
}
