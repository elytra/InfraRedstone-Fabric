package com.elytradev.infraredstone.block;

import net.fabricmc.fabric.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import io.github.prospector.silk.block.SilkBlockWithEntity;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

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

	public boolean canBlockStay(World world, BlockPos pos) {
		return world.getBlockState(pos.down()).hasSolidTopSurface(world, pos.down())
				|| world.getBlockState(pos.down()).getBlock() == ModBlocks.IN_RED_SCAFFOLD
				|| world.getBlockState(pos.down()).getBlock() == ModBlocks.IN_RED_BLOCK;
	}

	@Override
	public boolean canPlaceAt(BlockState state, ViewableWorld world, BlockPos pos) {
		return canBlockStay((World)world, pos);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.SOLID;
	}

	@Override
	public boolean isSimpleFullBlock(BlockState state, BlockView view, BlockPos pos) {
		return false;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, VerticalEntityPosition collisionPos) {
		return VoxelShapes.empty();
	}

	@Override
	public VoxelShape getBoundingShape(BlockState state, BlockView view, BlockPos pos) {
		return BASE_SHAPE;
	}
}
