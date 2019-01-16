package com.elytradev.infraredstone.block;

import com.elytradev.infraredstone.api.InfraRedstoneComponent;
import com.elytradev.infraredstone.api.InfraRedstoneWire;
import net.fabricmc.fabric.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class InfraRedstoneScaffold extends BlockBase implements Waterloggable, InfraRedstoneWire, InfraRedstoneComponent {

	public static final BooleanProperty NORTH = BooleanProperty.create("north");
	public static final BooleanProperty SOUTH = BooleanProperty.create("south");
	public static final BooleanProperty EAST = BooleanProperty.create("east");
	public static final BooleanProperty WEST = BooleanProperty.create("west");
	public static final BooleanProperty UP = BooleanProperty.create("up");
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	public InfraRedstoneScaffold() {
		super("infra_redstone_scaffold", FabricBlockSettings.create(Material.PART).setStrength(0f, 8f).build());
		this.setDefaultState(this.getStateFactory().getDefaultState()
				.with(NORTH, false)
				.with(SOUTH, false)
				.with(EAST, false)
				.with(WEST, false)
				.with(UP, false)
				.with(WATERLOGGED, false));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(NORTH, SOUTH, EAST, WEST, UP, WATERLOGGED);
	}

	@Override
	public boolean isSimpleFullBlock(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return false;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.MIPPED_CUTOUT;
	}

	@Override
	public VoxelShape getBoundingShape(BlockState state, BlockView view, BlockPos shape) {
		return VoxelShapes.fullCube();
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return Block.createCubeShape(0.05,0.0,0.05,15.95,16.0,15.95);
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (entity instanceof ItemEntity) return;
		if (entity.horizontalCollision) {
			entity.velocityY = 0.35;
		} else if (entity.isSneaking()) {
			entity.velocityY = 0.08; //Stop, but also counteract EntityLivingBase-applied microgravity
		} else if (entity.velocityY<-0.20) {
			entity.velocityY = -0.20;
		}
	}

	private boolean getCableConnections(BlockView world, BlockPos pos, Direction dir) {
		Block test = world.getBlockState(pos.offset(dir).offset(Direction.DOWN)).getBlock();
		if (test instanceof InfraRedstoneWire && !(test instanceof InfraRedstoneComponent)) return true;
		return canConnect(world, pos.offset(dir), dir.getOpposite());
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
				.with(UP, getCableConnections(world, pos, Direction.UP))
				.with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getPos()).getFluid() == Fluids.WATER);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		if (state.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.method_15789(world));
		}
		world.setBlockState(pos, state
				.with(NORTH, getCableConnections(world, pos, Direction.NORTH))
				.with(SOUTH, getCableConnections(world, pos, Direction.SOUTH))
				.with(EAST, getCableConnections(world, pos, Direction.EAST))
				.with(WEST, getCableConnections(world, pos, Direction.WEST))
				.with(UP, getCableConnections(world, pos, Direction.UP))
				.with(WATERLOGGED, world.getFluidState(pos).getFluid() == Fluids.WATER)
		);
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

	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getState(false) : super.getFluidState(state);
	}

	@Override
	public boolean canConnect(World world, BlockPos currentPos, BlockPos inspectingFrom) {
		// We only want to connect to things on our y-level, directly above or below us, or with a special case.
		if (currentPos.getX() == inspectingFrom.getX() && currentPos.getY() == inspectingFrom.getY()) return true;
		if (currentPos.getY() == inspectingFrom.getY()) return true;
		if (currentPos.getY() - inspectingFrom.getY() == -1) {
			// If it's an Infra-Redstone wire and it's ok with connecting up here, then we're fine with it.
			return (world.getBlockState(inspectingFrom).getBlock() instanceof InfraRedstoneWire && ((InfraRedstoneComponent)world.getBlockState(inspectingFrom).getBlock()).canConnect(world, inspectingFrom, currentPos));
		}
		return false;
	}
}
