package com.elytradev.infraredstone.Container;

import com.elytradev.infraredstone.block.entity.OscillatorBlockEntity;
import net.minecraft.class_3917;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public class OscillatorContainer extends Container {

	BlockPos pos;
	OscillatorBlockEntity be;

	public OscillatorContainer(int id, BlockPos pos, PlayerEntity player) {
		super(id);
		this.pos = pos;
		this.be = (OscillatorBlockEntity)player.getEntityWorld().getBlockEntity(pos);
	}

	@Override
	public class_3917<?> method_17358() {
		return null;
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

}
