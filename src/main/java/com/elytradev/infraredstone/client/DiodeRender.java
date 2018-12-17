package com.elytradev.infraredstone.client;

import com.elytradev.infraredstone.block.DiodeBlock;
import com.elytradev.infraredstone.block.ModBlocks;
import com.elytradev.infraredstone.block.entity.DiodeBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

public class DiodeRender extends InRedRenderBase<DiodeBlockEntity> {

	public static final String LIT = "infraredstone:block/diode_glow";

	@Override
	public Direction getFacing(DiodeBlockEntity be) {
		BlockState state = be.getWorld().getBlockState(be.getPos());
		if (state.getBlock()== ModBlocks.DIODE) return state.get(DiodeBlock.FACING);

		return Direction.NORTH;
	}

	@Override
	public Sprite getLightupTexture(DiodeBlockEntity block) {
		return (block.isActive()) ? MinecraftClient.getInstance().getSpriteAtlas().getSprite(LIT) : null;
	}
}
