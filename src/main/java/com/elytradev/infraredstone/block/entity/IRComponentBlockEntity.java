package com.elytradev.infraredstone.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;

public abstract class IRComponentBlockEntity extends BlockEntity {
	public IRComponentBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}
}
