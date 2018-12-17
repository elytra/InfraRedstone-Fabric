package com.elytradev.infraredstone.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public abstract class IRComponentBlockEntity extends BlockEntity {
	public IRComponentBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}
}
