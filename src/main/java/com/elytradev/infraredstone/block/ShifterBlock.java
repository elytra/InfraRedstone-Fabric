package com.elytradev.infraredstone.block;

import com.elytradev.infraredstone.block.entity.ShifterBlockEntity;
import com.elytradev.infraredstone.util.enums.ShifterSelection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.particle.WaterBubbleParticle;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ShifterBlock extends ModuleBase implements Waterloggable {

	public static final EnumProperty<Direction> FACING = Properties.FACING_HORIZONTAL;
	public static final EnumProperty<ShifterSelection> SELECTION = EnumProperty.create("selection", ShifterSelection.class);
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	protected ShifterBlock() {
		super("shifter", DEFAULT_SETTINGS);
		this.setDefaultState(this.getStateFactory().getDefaultState().with(FACING, Direction.NORTH).with(SELECTION, ShifterSelection.LEFT).with(WATERLOGGED, false));
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new ShifterBlockEntity();
	}

	@Override
	public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
		BlockEntity be = world.getBlockEntity(pos);
		if(!world.isClient() && !player.isSneaking() && be instanceof ShifterBlockEntity) {
			((ShifterBlockEntity)be).toggleSelection();
		}
		return true;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(FACING, SELECTION, WATERLOGGED);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction side) {
		return getStrongRedstonePower(state, world, pos, side);
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction side) {
//		if (side==state.get(FACING) || side==Direction.UP || side==Direction.DOWN) return 0;
		if (side!=state.get(FACING).getOpposite()) return 0;
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof ShifterBlockEntity) {
			if (side==state.get(FACING).getOpposite()) {
				return ((ShifterBlockEntity)be).isActive() ? 16 : 0;
//			} else if ((side==state.get(FACING).rotateYCounterclockwise() && state.get(SELECTION) == ShifterSelection.RIGHT)
//					|| side==state.get(FACING).rotateYClockwise() && state.get(SELECTION)==ShifterSelection.LEFT) {
//				return ((ShifterBlockEntity)be).isEject() ? 16 : 0;
			}
		}
		return 0;
	}

	@Override
	public boolean emitsRedstonePower(BlockState blockState) {
		return true;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerHorizontalFacing()).with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getPos()).getFluid() == Fluids.WATER);
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
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof ShifterBlockEntity) {
				world.setBlockState(pos, state
						.with(SELECTION, ((ShifterBlockEntity)be).selection));
			}
		}
	}

	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getState(false) : super.getFluidState(state);
	}
}
