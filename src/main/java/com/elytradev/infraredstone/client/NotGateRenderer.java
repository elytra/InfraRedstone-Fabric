package com.elytradev.infraredstone.client;

import com.elytradev.infraredstone.block.ModBlocks;
import com.elytradev.infraredstone.block.NotGateBlock;
import com.elytradev.infraredstone.block.entity.NotGateBlockEntity;
import com.elytradev.infraredstone.util.Torch;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

import static io.github.prospector.silk.util.RenderUtil.frac;

public class NotGateRenderer extends InRedBaseRenderer<NotGateBlockEntity> {

	public static final String BOTH = "infraredstone:block/not_gate_glow";
	public static final String IN = "infraredstone:block/not_gate_glow_in";
	public static final String OUT = "infraredstone:block/not_gate_glow_out";

	public NotGateRenderer() {
		torches = new Torch[1];
		torches[0] = new Torch(frac(7), frac(2), true, true);
	}

	@Override
	public Direction getFacing(NotGateBlockEntity be) {
		BlockState state = be.getWorld().getBlockState(be.getPos());
		if (state.getBlock()== ModBlocks.NOT_GATE) return state.get(NotGateBlock.FACING);

		return Direction.NORTH;
	}

	@Override
	public Sprite getLightupTexture(NotGateBlockEntity block) {
		boolean front;
		torches[0].isFullHeight = !block.booleanMode;
		if (block.isActive()) {
			torches[0].isLit=true;
			front = true;
		} else {
			torches[0].isLit = false;
			front = false;
		}
		if (front) {
			if (block.backActive) return MinecraftClient.getInstance().getSpriteAtlas().getSprite(BOTH); else return MinecraftClient.getInstance().getSpriteAtlas().getSprite(OUT);
		} else return (block.backActive)? MinecraftClient.getInstance().getSpriteAtlas().getSprite(IN) : null;
	}
}
