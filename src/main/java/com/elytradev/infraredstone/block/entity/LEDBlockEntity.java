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
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;

public class LEDBlockEntity extends IRComponentBlockEntity implements Tickable, MultimeterProbeProvider, InfraRedstoneCapable {

	private int lightLevel;
	private float brightness;

	//Transient data to throttle sync down here
	int lastLightLevel = 0;
	float lastBrightness = 0;

	@Environment(EnvType.CLIENT)
	boolean firstTick = true;

	public LEDBlockEntity() {
		super(ModBlocks.LED_BE);
	}

	@Override
	public void tick() {
		if (world.isClient && firstTick) {
			InfraRedstoneNetworking.requestModule(this);
			markDirty();
		}
		if (world.isClient || !hasWorld() || !InRedLogic.isIRTick()) return;
		lightLevel = 0;
		brightness = 0;
		for (Direction dir : Direction.values()) {
			BlockPos checkPos = pos.offset(dir);
			if (!InRedLogic.checkCandidacy(world, checkPos, dir)) {
				lightLevel = Math.max(lightLevel, world.getEmittedRedstonePower(checkPos, dir.getOpposite()));
				brightness = Math.max(brightness, world.getEmittedRedstonePower(checkPos, dir.getOpposite())/15f);
			} else {
				int caughtSignal = InRedLogic.findIRValue(world, pos, dir);
				float levelPercent = caughtSignal / 63f;
				 lightLevel = Math.max(lightLevel, MathHelper.floor(levelPercent * 14.0F) + (caughtSignal > 0 ? 1 : 0));
				 brightness = Math.max(brightness, levelPercent);
			}
		}
		markDirty();
	}

	@Override
	public CompoundTag toTag(CompoundTag compound) {
		CompoundTag tag = super.toTag(compound);
		tag.putInt("LightLevel", lightLevel);
		tag.putFloat("Brightness", brightness);
		return tag;
	}

	@Override
	public void fromTag(CompoundTag compound) {
		super.fromTag(compound);
		lightLevel = compound.getInt("LightLevel");
		brightness = compound.getFloat("Brightness");
	}

	@Override
	public void markDirty() {
		super.markDirty();
		// again, I've copy-pasted this like 12 times, should probably go into Concrete
		if (!hasWorld() || getWorld().isClient) return;
		if (lastLightLevel != lightLevel
				|| lastBrightness != brightness
				|| firstTick) { //Throttle updates - only send when something important changes

			ServerWorld ws = (ServerWorld) getWorld();
			Chunk c = getWorld().getChunk(getPos());
			for (ServerPlayerEntity player : getWorld().getPlayers(ServerPlayerEntity.class, Predicates.alwaysTrue())) {
				if (ws.getChunkManager().method_14154(player, c.getPos().x, c.getPos().z)) {
					InfraRedstoneNetworking.syncModule(this, player);
				}
			}

			if (lastLightLevel != lightLevel || firstTick) {
				world.updateNeighborsAlways(pos.offset(Direction.UP), ModBlocks.LED);
			}
			if (lastBrightness != brightness ||  firstTick) {
				//BlockState isn't changing, but we need to update for the new rendered brightness value
				BlockState state = world.getBlockState(pos);
				world.updateNeighborsAlways(pos, ModBlocks.LED);
				world.updateListeners(pos, state, state, 1);
			}

			lastLightLevel = lightLevel;
			lastBrightness = brightness;
			if (firstTick) firstTick = false;
		}
	}

	public int getLightLevel() {
		return lightLevel;
	}

	public float getBrightness() { return brightness; }

	@Override
	public StringTextComponent getProbeMessage() {
		TranslatableTextComponent message = new TranslatableTextComponent("msg.inred.multimeter.led");
		return new StringTextComponent(message.getFormattedText()+lightLevel);
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
