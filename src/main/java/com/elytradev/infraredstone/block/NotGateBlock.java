package com.elytradev.infraredstone.block;

import com.elytradev.infraredstone.block.entity.NotGateBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class NotGateBlock extends ModuleBase {

	public static final EnumProperty<Direction> FACING = Properties.FACING_HORIZONTAL;
	public static final BooleanProperty BOOLEAN_MODE = BooleanProperty.create("boolean_mode");

	protected NotGateBlock() {
		super("not_gate", DEFAULT_SETTINGS);
		this.setDefaultState(this.getStateFactory().getDefaultState().with(FACING, Direction.NORTH).with(BOOLEAN_MODE, false));
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new NotGateBlockEntity();
	}

	public static final VoxelShape NOT_CLICK_BOOLEAN = Block.createCubeShape( 6, 2.9,  2, 10, 4.1,  6);

	@Override
	public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
		BlockEntity be = world.getBlockEntity(pos);
		if(!world.isClient() && !player.isSneaking() && be instanceof NotGateBlockEntity) {
			Vec3d blockCenteredHit = new Vec3d(hitX, hitY, hitZ);
			blockCenteredHit = blockCenteredHit.subtract(0.5, 0.5, 0.5);
			switch (state.get(NotGateBlock.FACING)) {
				case SOUTH:
					blockCenteredHit = blockCenteredHit.rotateX((float)Math.PI);
					break;
				case EAST:
					blockCenteredHit = blockCenteredHit.rotateX((float)Math.PI/2);
					break;
				case WEST:
					blockCenteredHit = blockCenteredHit.rotateX(3*(float)Math.PI/2);
					break;
				default:
					break;
			}
			blockCenteredHit = blockCenteredHit.add(0.5, 0.5, 0.5);
//			blockCenteredHit = blockCenteredHit.multiply(16);
			NotGateBlockEntity beNotGate = (NotGateBlockEntity)be;
			if (NOT_CLICK_BOOLEAN.getBoundingBox().contains(blockCenteredHit)) {
				beNotGate.toggleBooleanMode();
			}
		}
		return true;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(FACING, BOOLEAN_MODE);
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
		return this.getDefaultState().with(FACING, ctx.getPlayerHorizontalFacing());
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof NotGateBlockEntity) {
			world.setBlockState(pos, state
					.with(BOOLEAN_MODE, ((NotGateBlockEntity)be).booleanMode));
		}
	}

}
