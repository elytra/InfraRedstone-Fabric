package com.elytradev.infraredstone.client;

import com.elytradev.infraredstone.block.ModBlocks;
import com.elytradev.infraredstone.block.ShifterBlock;
import com.elytradev.infraredstone.block.entity.ShifterBlockEntity;
import com.elytradev.infraredstone.util.enums.ShifterSelection;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

public class ShifterRenderer extends InRedBaseRenderer<ShifterBlockEntity> {

	public static final String LIT = "infraredstone:block/shifter_glow";
	public static final String CENTER = "_center";
	public static final String LEFT = "_left";
	public static final String RIGHT = "_right";

	@Override
	public Direction getFacing(ShifterBlockEntity be) {
		BlockState state = be.getWorld().getBlockState(be.getPos());
		if (state.getBlock()== ModBlocks.SHIFTER) return state.get(ShifterBlock.FACING);

		return Direction.NORTH;
	}

	@Override
	public Sprite getLightupTexture(ShifterBlockEntity be) {
		BlockState state = be.getWorld().getBlockState(be.getPos());
		String phrase = LIT;
		if (be.isActive()) phrase += CENTER;
		if (be.isEject()) {
			if (state.get(ShifterBlock.SELECTION) == ShifterSelection.LEFT) {
				phrase += LEFT;
			} else {
				phrase += RIGHT;
			}
		}
		return (!phrase.equals(LIT)) ? MinecraftClient.getInstance().getSpriteAtlas().getSprite(phrase) : null;
	}
}
