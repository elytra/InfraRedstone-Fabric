package com.elytradev.infraredstone.block;

import net.fabricmc.fabric.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import prospector.silk.block.SilkBlockWithEntity;

public class ModuleBase extends SilkBlockWithEntity implements NamedBlock {
	public String name;

	public static Settings DEFAULT_SETTINGS = FabricBlockSettings.create(Material.PART).setStrength(0.5f, 8f).build();

	public static VoxelShape BASE_SHAPE = Block.createCubeShape(0, 0, 0, 16, 3, 16);

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
