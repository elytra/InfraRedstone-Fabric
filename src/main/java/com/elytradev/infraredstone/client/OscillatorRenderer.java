package com.elytradev.infraredstone.client;

import com.elytradev.infraredstone.block.ModBlocks;
import com.elytradev.infraredstone.block.TransistorBlock;
import com.elytradev.infraredstone.block.entity.OscillatorBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

public class OscillatorRenderer extends InRedBaseRenderer<OscillatorBlockEntity> {

	public static final String LIT = "infraredstone:block/oscillator_glow";

	@Override
	public Direction getFacing(OscillatorBlockEntity be) {
		BlockState state = be.getWorld().getBlockState(be.getPos());
		if (state.getBlock()== ModBlocks.OSCILLATOR) return state.get(TransistorBlock.FACING);

		return Direction.NORTH;
	}

	@Override
	public Sprite getLightupTexture(OscillatorBlockEntity block) {
		return (block.isActive()) ? MinecraftClient.getInstance().getSpriteAtlas().getSprite(LIT) : null;
	}

}
