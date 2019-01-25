package com.elytradev.infraredstone.block;

import com.elytradev.infraredstone.api.InfraRedstoneComponent;
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

public class ModuleBase extends SilkBlockWithEntity implements NamedBlock, InfraRedstoneComponent {
	public String name;

	public static final Settings DEFAULT_SETTINGS = FabricBlockSettings.create(Material.PART).setStrength(0.5f, 8f).build();

	public static final VoxelShape CLICK_BOOLEAN = Block.createCuboidShape( 6, 2.9,  3, 10, 4.1,  7);

	public static final VoxelShape BASE_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 3, 16);

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
				|| world.getBlockState(pos.down()).getBlock() instanceof InfraRedstoneComponent;
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
	public VoxelShape getRayTraceShape(BlockState state, BlockView view, BlockPos pos) {
		return BASE_SHAPE;
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1, VerticalEntityPosition verticalEntityPosition_1) {
		return BASE_SHAPE;
	}

	@Override
	public boolean canConnect(World world, BlockPos currentPos, BlockPos inspectingFrom) {
		// We're pretty lenient on what can connect. Let the other side determine if the connection gets made.
		return true;
	}
}
