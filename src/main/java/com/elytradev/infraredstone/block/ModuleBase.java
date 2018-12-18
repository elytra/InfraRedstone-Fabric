package com.elytradev.infraredstone.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.RenderTypeBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import prospector.silk.block.SilkBlockWithEntity;

public class ModuleBase extends SilkBlockWithEntity implements NamedBlock {
	public String name;

	protected ModuleBase(String name, Settings settings) {
		super(settings);
		this.name = name;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Block getBlock() {
		return this;
	}
}
