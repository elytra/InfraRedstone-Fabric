package com.elytradev.infraredstone.client;

import com.elytradev.infraredstone.block.ModBlocks;
import com.elytradev.infraredstone.block.XorGateBlock;
import com.elytradev.infraredstone.block.entity.XorGateBlockEntity;
import com.elytradev.infraredstone.util.Torch;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

import static io.github.prospector.silk.util.RenderUtil.frac;

public class XorGateRenderer extends InRedBaseRenderer<XorGateBlockEntity> {

	public static final String LIT = "infraredstone:block/xor_gate_glow";

	public XorGateRenderer() {
		torches = new Torch[3];
		torches[0] = new Torch(frac(7), frac(4), true, true); // output torch
		torches[1] = new Torch(frac(0.05), frac(7), true, true); // left torch
		torches[2] = new Torch(frac(13.95), frac(7), true, true); // right torch
	}

	@Override
	public Direction getFacing(XorGateBlockEntity be) {
		BlockState state = be.getWorld().getBlockState(be.getPos());
		if (state.getBlock()== ModBlocks.XOR_GATE) return state.get(XorGateBlock.FACING);

		return Direction.NORTH;
	}

	@Override
	public Sprite getLightupTexture(XorGateBlockEntity be) {
		getTorches(be);
		return (be.isActive()) ? MinecraftClient.getInstance().getSpriteAtlas().getSprite(LIT) : null;
	}

	public void getTorches(XorGateBlockEntity be) {
		torches[0].isLit = be.isActive();
		torches[0].isFullHeight = !be.booleanMode;
		torches[1].isLit = be.isLeftActive();
		torches[2].isLit = be.isRightActive();
	}
}
