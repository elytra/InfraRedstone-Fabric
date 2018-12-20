package com.elytradev.infraredstone.block;

import net.fabricmc.fabric.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import io.github.prospector.silk.block.SilkBlockWithEntity;

public class ModuleBase extends SilkBlockWithEntity implements NamedBlock {
	public String name;

	public static final Settings DEFAULT_SETTINGS = FabricBlockSettings.create(Material.PART).setStrength(0.5f, 8f).build();

	public static final VoxelShape CLICK_BOOLEAN = Block.createCubeShape( 6, 2.9,  3, 10, 4.1,  7);

	public static final VoxelShape BASE_SHAPE = Block.createCubeShape(0, 0, 0, 16, 3, 16);

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
