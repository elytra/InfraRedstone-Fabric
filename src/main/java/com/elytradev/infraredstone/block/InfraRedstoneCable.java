package com.elytradev.infraredstone.block;

import com.elytradev.infraredstone.api.InfraRedstoneComponent;
import com.elytradev.infraredstone.api.InfraRedstoneWire;
import com.elytradev.infraredstone.logic.InRedLogic;
import com.elytradev.infraredstone.util.enums.CableConnection;
import net.fabricmc.fabric.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class InfraRedstoneCable extends BlockBase implements Waterloggable, InfraRedstoneWire, InfraRedstoneComponent {

	public static final EnumProperty<CableConnection> NORTH = EnumProperty.create("north", CableConnection.class);
	public static final EnumProperty<CableConnection> SOUTH = EnumProperty.create("south", CableConnection.class);
	public static final EnumProperty<CableConnection> EAST = EnumProperty.create("east", CableConnection.class);
	public static final EnumProperty<CableConnection> WEST = EnumProperty.create("west", CableConnection.class);

	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	public InfraRedstoneCable() {
		super("infra_redstone", FabricBlockSettings.create(Material.PART).setStrength(0f, 8f).build());
		this.setDefaultState(this.getStateFactory().getDefaultState()
				.with(NORTH, CableConnection.DISCONNECTED)
				.with(SOUTH, CableConnection.DISCONNECTED)
				.with(EAST, CableConnection.DISCONNECTED)
				.with(WEST, CableConnection.DISCONNECTED)
				.with(WATERLOGGED, false));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(NORTH, SOUTH, EAST, WEST, WATERLOGGED);
	}

	@Override
	public boolean isSimpleFullBlock(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return false;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return VoxelShapes.empty();
	}

	private VoxelShape NO_SIDE = Block.createCubeShape(6d, 0d, 6d, 10d, 3d, 10d);
	private VoxelShape NORTH_SIDE = Block.createCubeShape(6d, 0d, 0d, 10d, 3d, 6d);
	private VoxelShape NORTH_UP = Block.createCubeShape(6d, 3d, 0d, 10d, 19d, 3d);
	private VoxelShape NORTH_SIDE_UP = VoxelShapes.union(NORTH_SIDE, NORTH_UP);
	private VoxelShape SOUTH_SIDE = Block.createCubeShape(6d, 0d, 10d, 10d, 3d, 16d);
	private VoxelShape SOUTH_UP = Block.createCubeShape(6d, 3d, 13d, 10d, 19d, 16d);
	private VoxelShape SOUTH_SIDE_UP = VoxelShapes.union(SOUTH_SIDE, SOUTH_UP);
	private VoxelShape EAST_SIDE = Block.createCubeShape(10d, 0d, 6d, 16d, 3d, 10d);
	private VoxelShape EAST_UP = Block.createCubeShape(13d, 3d, 6d, 16d, 19d, 10d);
	private VoxelShape EAST_SIDE_UP = VoxelShapes.union(EAST_SIDE, EAST_UP);
	private VoxelShape WEST_SIDE = Block.createCubeShape(0d, 0d, 6d, 6d, 3d, 10d);
	private VoxelShape WEST_UP = Block.createCubeShape(0d, 3d, 6d, 3d, 19d, 10d);
	private VoxelShape WEST_SIDE_UP = VoxelShapes.union(WEST_SIDE, WEST_UP);

	@Override
	public VoxelShape getBoundingShape(BlockState state, BlockView view, BlockPos pos) {
		VoxelShape result = NO_SIDE;
		if (state.get(NORTH) == CableConnection.CONNECTED) result = VoxelShapes.union(result, NORTH_SIDE);
		if (state.get(NORTH) == CableConnection.CONNECTED_UP) result = VoxelShapes.union(result, NORTH_SIDE_UP);
		if (state.get(SOUTH) == CableConnection.CONNECTED) result = VoxelShapes.union(result, SOUTH_SIDE);
		if (state.get(SOUTH) == CableConnection.CONNECTED_UP) result = VoxelShapes.union(result, SOUTH_SIDE_UP);
		if (state.get(EAST) == CableConnection.CONNECTED) result = VoxelShapes.union(result, EAST_SIDE);
		if (state.get(EAST) == CableConnection.CONNECTED_UP) result = VoxelShapes.union(result, EAST_SIDE_UP);
		if (state.get(WEST) == CableConnection.CONNECTED) result = VoxelShapes.union(result, WEST_SIDE);
		if (state.get(WEST) == CableConnection.CONNECTED_UP) result = VoxelShapes.union(result, WEST_SIDE_UP);
		return result;
	}

	private CableConnection getCableConnections(BlockView world, BlockPos pos, Direction dir) {
		if (canConnect(world, pos.offset(dir), dir.getOpposite())) return CableConnection.CONNECTED;

		if (!InRedLogic.isSideSolid((World)world, pos.offset(Direction.UP), Direction.DOWN)) {
			if (canConnect(world, pos.offset(dir).up(), dir.getOpposite())) return CableConnection.CONNECTED_UP;
		}

		if (!InRedLogic.isSideSolid((World)world, pos.offset(dir), dir.getOpposite())) {
			if (world.getBlockState(pos.offset(Direction.DOWN).offset(dir)).getBlock() instanceof InfraRedstoneComponent) return CableConnection.DISCONNECTED;
			if (canConnect(world, pos.offset(dir).down(), dir.getOpposite())) return CableConnection.CONNECTED;
		}

		return CableConnection.DISCONNECTED;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		World world = ctx.getWorld();
		BlockPos pos = ctx.getPos();
		return this.getDefaultState()
				.with(NORTH, getCableConnections(world, pos, Direction.NORTH))
				.with(SOUTH, getCableConnections(world, pos, Direction.SOUTH))
				.with(EAST, getCableConnections(world, pos, Direction.EAST))
				.with(WEST, getCableConnections(world, pos, Direction.WEST))
				.with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getPos()).getFluid() == Fluids.WATER);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		if (state.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.method_15789(world));
		}
		if (!this.canBlockStay(world, pos)) {
			world.breakBlock(pos, true);

			for (Direction dir : Direction.values()) {
				world.updateNeighborsAlways(pos.offset(dir), this);
			}
		} else {
			world.setBlockState(pos, state
					.with(NORTH, getCableConnections(world, pos, Direction.NORTH))
					.with(SOUTH, getCableConnections(world, pos, Direction.SOUTH))
					.with(EAST, getCableConnections(world, pos, Direction.EAST))
					.with(WEST, getCableConnections(world, pos, Direction.WEST))
					.with(WATERLOGGED, world.getFluidState(pos).getFluid() == Fluids.WATER)
			);
		}
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState stateFrom) {
		for (Direction dir : Direction.values()) {
			world.updateNeighborsAlways(pos.offset(dir), this);
		}
	}

	@Override
	public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState stateFrom, boolean b) {
		for (Direction dir : Direction.values()) {
			world.updateNeighborsAlways(pos.offset(dir), this);
		}
	}

	public boolean canBlockStay(World world, BlockPos pos) {
		return world.getBlockState(pos.down()).hasSolidTopSurface(world, pos.down())
				|| world.getBlockState(pos.down()).getBlock() instanceof InfraRedstoneComponent;
	}

	@Override
	public boolean canPlaceAt(BlockState state, ViewableWorld world, BlockPos pos) {
		return canBlockStay((World)world, pos);
	}

	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getState(false) : super.getFluidState(state);
	}

	@Override
	public boolean canConnect(World world, BlockPos currentPos, BlockPos inspectingFrom) {
		// We're pretty lenient on what can connect. Let the other side determine if the connection gets made.
		return true;
	}
}
