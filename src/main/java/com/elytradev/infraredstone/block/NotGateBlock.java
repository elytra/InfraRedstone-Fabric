package com.elytradev.infraredstone.block;

import com.elytradev.infraredstone.block.entity.NotGateBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class NotGateBlock extends ModuleBase implements Waterloggable {

	public static final EnumProperty<Direction> FACING = Properties.FACING_HORIZONTAL;
	public static final BooleanProperty BOOLEAN_MODE = BooleanProperty.create("boolean_mode");
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	protected NotGateBlock() {
		super("not_gate", DEFAULT_SETTINGS);
		this.setDefaultState(this.getStateFactory().getDefaultState().with(FACING, Direction.NORTH).with(BOOLEAN_MODE, false).with(WATERLOGGED, false));
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new NotGateBlockEntity();
	}

	public static final VoxelShape NOT_CLICK_BOOLEAN = Block.createCuboidShape( 6, 2.9,  2, 10, 4.1,  6);

	@Override
	public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
		BlockEntity be = world.getBlockEntity(pos);
		if(!world.isClient() && !player.isSneaking() && be instanceof NotGateBlockEntity) {
			Vec3d blockCenteredHit = blockHitResult.getPos(); blockCenteredHit = new Vec3d(blockCenteredHit.x % 1, blockCenteredHit.y % 1, blockCenteredHit.z % 1);
			blockCenteredHit = blockCenteredHit.subtract(0.5, 0.5, 0.5);
			switch (state.get(NotGateBlock.FACING)) {
				case SOUTH:
					blockCenteredHit = blockCenteredHit.rotateY((float)Math.PI);
					break;
				case EAST:
					blockCenteredHit = blockCenteredHit.rotateY((float)Math.PI/2);
					break;
				case WEST:
					blockCenteredHit = blockCenteredHit.rotateY(3*(float)Math.PI/2);
					break;
				default:
					break;
			}
			blockCenteredHit = blockCenteredHit.add(0.5, 0.5, 0.5);
			NotGateBlockEntity beNotGate = (NotGateBlockEntity)be;
			if (NOT_CLICK_BOOLEAN.getBoundingBox().contains(blockCenteredHit)) {
				beNotGate.toggleBooleanMode();
			}
		}
		return true;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(FACING, BOOLEAN_MODE, WATERLOGGED);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction side) {
		return getStrongRedstonePower(state, world, pos, side);
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction side) {
		if (side!=state.get(FACING).getOpposite()) return 0;
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof NotGateBlockEntity) {
			return ((NotGateBlockEntity)be).isActive()?16:0;
		}
		return 0;
	}

	@Override
	public boolean emitsRedstonePower(BlockState blockState) {
		return true;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerHorizontalFacing()).with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		if (state.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		if (!this.canBlockStay(world, pos)) {
			world.breakBlock(pos, true);

			for (Direction dir : Direction.values()) {
				world.updateNeighborsAlways(pos.offset(dir), this);
			}
		} else {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof NotGateBlockEntity) {
				world.setBlockState(pos, state
						.with(BOOLEAN_MODE, ((NotGateBlockEntity) be).booleanMode));
			}
		}
	}

	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getState(false) : super.getFluidState(state);
	}
}
