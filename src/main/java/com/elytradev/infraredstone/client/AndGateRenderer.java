package com.elytradev.infraredstone.client;

import com.elytradev.infraredstone.block.AndGateBlock;
import com.elytradev.infraredstone.block.ModBlocks;
import com.elytradev.infraredstone.block.entity.AndGateBlockEntity;
import com.elytradev.infraredstone.util.Torch;
import com.elytradev.infraredstone.util.enums.InactiveSelection;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

import static io.github.prospector.silk.util.MathUtil.frac;

public class AndGateRenderer extends InRedBaseRenderer<AndGateBlockEntity> {

	public static final String LIT = "infraredstone:block/and_gate_glow";

	public AndGateRenderer() {
		torches = new Torch[4];
		torches[0] = new Torch(frac(7), frac(4), true, true); // output torch
		torches[1] = new Torch(frac(0.05), frac(7), true, true); // left torch
		torches[2] = new Torch(frac(7), frac(13.95), true, true); // back torch
		torches[3] = new Torch(frac(13.95), frac(7), true, true); // right torch
	}

	@Override
	public Direction getFacing(AndGateBlockEntity be) {
		BlockState state = be.getWorld().getBlockState(be.getPos());
		if (state.getBlock()== ModBlocks.AND_GATE) return state.get(AndGateBlock.FACING);

		return Direction.NORTH;
	}

	@Override
	public Sprite getLightupTexture(AndGateBlockEntity be) {
		getTorches(be);
		return (be.isActive()) ? MinecraftClient.getInstance().getSpriteAtlas().getSprite(LIT) : null;
	}

	public void getTorches(AndGateBlockEntity be) {
		torches[0].isLit = be.isActive();
		torches[0].isFullHeight = !be.booleanMode;
		torches[1].isLit = be.isLeftActive();
		torches[1].isFullHeight = (be.inactive != InactiveSelection.LEFT);
		torches[2].isLit = be.isBackActive();
		torches[2].isFullHeight = (be.inactive != InactiveSelection.BACK);
		torches[3].isLit = be.isRightActive();
		torches[3].isFullHeight = (be.inactive != InactiveSelection.RIGHT);
	}
}
