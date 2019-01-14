package com.elytradev.infraredstone.block;

import com.elytradev.infraredstone.block.entity.AndGateBlockEntity;
import com.elytradev.infraredstone.util.enums.InactiveSelection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.VerticalEntityPosition;
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

public class AndGateBlock extends ModuleBase {

	public static final EnumProperty<Direction> FACING = Properties.FACING_HORIZONTAL;
	public static final BooleanProperty BOOLEAN_MODE = BooleanProperty.create("boolean_mode");
	public static final EnumProperty<InactiveSelection> INACTIVE = EnumProperty.create("inactive", InactiveSelection.class);

	protected AndGateBlock() {
		super("and_gate", DEFAULT_SETTINGS);
		this.setDefaultState(this.getStateFactory().getDefaultState()
				.with(FACING, Direction.NORTH)
				.with(BOOLEAN_MODE, false)
				.with(INACTIVE, InactiveSelection.NONE));
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new AndGateBlockEntity();
	}

	private static final VoxelShape CLICK_LEFT   = Block.createCubeShape( 0, 2.9,  6,  3, 4.1, 10);
	private static final VoxelShape CLICK_BACK   = Block.createCubeShape( 6, 2.9, 13, 10, 4.1, 16);
	private static final VoxelShape CLICK_RIGHT  = Block.createCubeShape(13, 2.9,  6, 16, 4.1, 10);

	@Override
	public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
		BlockEntity be = world.getBlockEntity(pos);
		if(!world.isClient() && !player.isSneaking() && be instanceof AndGateBlockEntity) {
			Vec3d blockCenteredHit = new Vec3d(hitX, hitY, hitZ);
			blockCenteredHit = blockCenteredHit.subtract(0.5, 0.5, 0.5);
			switch (state.get(AndGateBlock.FACING)) {
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
			AndGateBlockEntity beAndGate = (AndGateBlockEntity)be;
			if (CLICK_BOOLEAN.getBoundingBox().contains(blockCenteredHit)) {
				beAndGate.toggleBooleanMode();
			}
			if (CLICK_LEFT.getBoundingBox().contains(blockCenteredHit)) {
				beAndGate.toggleInactive(InactiveSelection.LEFT);
			}
			if (CLICK_BACK.getBoundingBox().contains(blockCenteredHit)) {
				beAndGate.toggleInactive(InactiveSelection.BACK);
			}
			if (CLICK_RIGHT.getBoundingBox().contains(blockCenteredHit)) {
				beAndGate.toggleInactive(InactiveSelection.RIGHT);
			}
		}
		return true;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(FACING, BOOLEAN_MODE, INACTIVE);
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
		return this.getDefaultState().with(FACING, ctx.getPlayerHorizontalFacing());
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		if (!this.canBlockStay(world, pos)) {
			world.breakBlock(pos, true);

			for (Direction dir : Direction.values()) {
				world.updateNeighborsAlways(pos.offset(dir), this);
			}
		} else {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof AndGateBlockEntity) {
				world.setBlockState(pos, state
						.with(BOOLEAN_MODE, ((AndGateBlockEntity)be).booleanMode)
						.with(INACTIVE, ((AndGateBlockEntity)be).inactive));
			}
		}
	}
}
