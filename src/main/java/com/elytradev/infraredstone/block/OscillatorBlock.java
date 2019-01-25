package com.elytradev.infraredstone.block;

import com.elytradev.infraredstone.InfraRedstone;
import com.elytradev.infraredstone.block.entity.AndGateBlockEntity;
import com.elytradev.infraredstone.block.entity.OscillatorBlockEntity;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
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
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class OscillatorBlock extends ModuleBase implements Waterloggable {

	public static final EnumProperty<Direction> FACING = Properties.FACING_HORIZONTAL;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	public OscillatorBlock() {
		super("oscillator", DEFAULT_SETTINGS);
		this.setDefaultState(this.getStateFactory().getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, false));
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new OscillatorBlockEntity();
	}

	@Override
	public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
		BlockEntity be = world.getBlockEntity(pos);
		if (!world.isClient && !player.isSneaking() && be instanceof OscillatorBlockEntity) {
			ContainerProviderRegistry.INSTANCE.openContainer(InfraRedstone.OSCILLATOR_CONTAINER, player, buf -> buf.writeBlockPos(pos));
		}
		return true;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(FACING, WATERLOGGED);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction side) {
		return getStrongRedstonePower(state, world, pos, side);
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction side) {
		if (side!=state.get(FACING).getOpposite()) return 0;
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof AndGateBlockEntity) {
			return ((AndGateBlockEntity)be).isActive()?16:0;
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
		if (!this.canBlockStay(world, pos)) {
			world.breakBlock(pos, true);

			for (Direction dir : Direction.values()) {
				world.updateNeighborsAlways(pos.offset(dir), this);
			}
		} else {
			if (state.get(WATERLOGGED)) {
				world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
			}
		}
	}

	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getState(false) : super.getFluidState(state);
	}
}
