package com.elytradev.infraredstone.container;

import com.elytradev.infraredstone.block.entity.OscillatorBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public class OscillatorContainer extends Container {

	BlockPos pos;
	OscillatorBlockEntity be;

	public OscillatorContainer(BlockPos pos, PlayerEntity player) {
		this.pos = pos;
		this.be = (OscillatorBlockEntity)player.getEntityWorld().getBlockEntity(pos);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

}
