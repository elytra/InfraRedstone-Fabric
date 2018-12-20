package com.elytradev.infraredstone.client;

import com.elytradev.infraredstone.block.DiodeBlock;
import com.elytradev.infraredstone.block.ModBlocks;
import com.elytradev.infraredstone.block.TransistorBlock;
import com.elytradev.infraredstone.block.entity.TransistorBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

public class TransistorRenderer extends InRedBaseRenderer<TransistorBlockEntity> {

	public static final String LIT = "infraredstone:block/transistor_glow";

	@Override
	public Direction getFacing(TransistorBlockEntity be) {
		BlockState state = be.getWorld().getBlockState(be.getPos());
		if (state.getBlock()== ModBlocks.TRANSISTOR) return state.get(TransistorBlock.FACING);

		return Direction.NORTH;
	}

	@Override
	public Sprite getLightupTexture(TransistorBlockEntity block) {
		return (block.isActive()) ? MinecraftClient.getInstance().getSpriteAtlas().getSprite(LIT) : null;
	}
}
