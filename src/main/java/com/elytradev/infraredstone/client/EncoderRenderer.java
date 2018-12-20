package com.elytradev.infraredstone.client;

import com.elytradev.infraredstone.block.EncoderBlock;
import com.elytradev.infraredstone.block.ModBlocks;
import com.elytradev.infraredstone.block.entity.EncoderBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

public class EncoderRenderer extends InRedBaseRenderer<EncoderBlockEntity> {
	public static final String LIT = "infraredstone:block/encoder_glow";

	@Override
	public Direction getFacing(EncoderBlockEntity be) {
		BlockState state = be.getWorld().getBlockState(be.getPos());
		if (state.getBlock()== ModBlocks.ENCODER) return state.get(EncoderBlock.FACING);

		return Direction.NORTH;
	}

	@Override
	public Sprite getLightupTexture(EncoderBlockEntity block) {
		return (block.isActive()) ? MinecraftClient.getInstance().getSpriteAtlas().getSprite(LIT) : null;
	}
}
