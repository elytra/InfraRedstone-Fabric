package com.elytradev.infraredstone.block.entity;

import com.elytradev.infraredstone.api.InfraRedstoneCapable;
import com.elytradev.infraredstone.api.InfraRedstoneSignal;
import com.elytradev.infraredstone.api.MultimeterProbeProvider;
import com.elytradev.infraredstone.block.ModBlocks;
import com.elytradev.infraredstone.logic.InRedLogic;
import com.elytradev.infraredstone.logic.impl.InfraRedstoneHandler;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.Chunk;

import java.awt.*;

public class ColorLEDBlockEntity extends IRComponentBlockEntity implements Tickable, MultimeterProbeProvider, InfraRedstoneCapable {
	private int colorValue;

	//Transient data to throttle sync down here
	int lastColorValue = 0;
	boolean lastActive = false;

	@Environment(EnvType.CLIENT)
	boolean firstTick = true;

	public ColorLEDBlockEntity() {
		super(ModBlocks.COLOR_LED_BE);
	}

	@Override
	public void tick() {
		if (world.isClient && firstTick) {
			InfraRedstoneNetworking.requestModule(this);
			markDirty();
		}
		if (world.isClient || !hasWorld()) return;
		colorValue = 0;
		if (InRedLogic.isIRTick()) {
			for (Direction dir : Direction.values()) {
				int caughtSignal = InRedLogic.findIRValue(world, pos, dir);
				colorValue |= caughtSignal;
			}
			markDirty();
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag compound) {
		CompoundTag tag = super.toTag(compound);
		tag.putInt("ColorValue", colorValue);
		return tag;
	}

	@Override
	public void fromTag(CompoundTag compound) {
		super.fromTag(compound);
		colorValue = compound.getInt("ColorValue");
	}

	@Override
	public void markDirty() {
		super.markDirty();
		// again, I've copy-pasted this like 12 times, should probably go into Concrete
		if (!hasWorld() || getWorld().isClient) return;
		boolean active = getIsLit();
		if (active != lastActive
				|| lastColorValue != colorValue
				|| firstTick) { //Throttle updates - only send when something important changes

			ServerWorld ws = (ServerWorld) getWorld();
			Chunk c = getWorld().getChunk(getPos());
			for (ServerPlayerEntity player : getWorld().getPlayers(ServerPlayerEntity.class, Predicates.alwaysTrue())) {
				if (ws.getChunkManager().method_14154(player, c.getPos().x, c.getPos().z)) {
					InfraRedstoneNetworking.syncModule(this, player);
				}
			}

			if (lastActive != active || firstTick) {
				world.updateNeighborsAlways(pos.offset(Direction.UP), ModBlocks.COLOR_LED);
			}
			if (lastColorValue != colorValue ||  firstTick) {
				//BlockState isn't changing, but we need to notify the block in front of us so that vanilla redstone updates
				BlockState state = world.getBlockState(pos);
				world.updateNeighborsAlways(pos, ModBlocks.COLOR_LED);
				world.updateListeners(pos, state, state, 1);
			}

			lastActive = getIsLit();
			lastColorValue = colorValue;
			if (firstTick) firstTick = false;
		}
	}

	public boolean getIsLit() {
		return colorValue > 0;
	}

	public int getRGBColor() {
		float hue = colorValue/63f;
		return Color.HSBtoRGB(hue, 0.5f, 1f);
	}

	@Override
	public StringTextComponent getProbeMessage() {
		TranslatableTextComponent message = new TranslatableTextComponent("msg.inred.multimeter.rgbled");
		return new StringTextComponent(message.getFormattedText()+String.format(" #%08X", getRGBColor()));
	}

	@Override
	public InfraRedstoneSignal getInfraRedstoneHandler(Direction inspectingFrom) {
		return InfraRedstoneHandler.ALWAYS_OFF;
	}

	@Override
	public boolean canConnectToSide(Direction inspectingFrom) {
		return true;
	}
}
