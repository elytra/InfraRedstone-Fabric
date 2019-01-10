package com.elytradev.infraredstone.block;

import com.elytradev.infraredstone.block.entity.DiodeBlockEntity;
import net.minecraft.block.Block;
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
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DiodeBlock extends ModuleBase {

	public static final BooleanProperty BIT_0 = BooleanProperty.create("bit_0");
	public static final BooleanProperty BIT_1 = BooleanProperty.create("bit_1");
	public static final BooleanProperty BIT_2 = BooleanProperty.create("bit_2");
	public static final BooleanProperty BIT_3 = BooleanProperty.create("bit_3");
	public static final BooleanProperty BIT_4 = BooleanProperty.create("bit_4");
	public static final BooleanProperty BIT_5 = BooleanProperty.create("bit_5");

	public static final EnumProperty<Direction> FACING = Properties.FACING_HORIZONTAL;

	public static final VoxelShape CLICK_BIT_0 = Block.createCubeShape(10, 2.9, 10, 11, 4.1, 14);
	public static final VoxelShape CLICK_BIT_1 = Block.createCubeShape(9, 2.9, 8, 10, 4.1, 12);
	public static final VoxelShape CLICK_BIT_2 = Block.createCubeShape(8, 2.9, 10, 9, 4.1, 14);
	public static final VoxelShape CLICK_BIT_3 = Block.createCubeShape(7, 2.9, 8, 8, 4.1, 12);
	public static final VoxelShape CLICK_BIT_4 = Block.createCubeShape(6, 2.9, 10, 7, 4.1, 14);
	public static final VoxelShape CLICK_BIT_5= Block.createCubeShape(5, 2.9, 8, 6, 4.1, 12);

	protected DiodeBlock() {
		super("diode", DEFAULT_SETTINGS);
		this.setDefaultState(this.getStateFactory().getDefaultState()
				.with(BIT_0, true)
				.with(BIT_1, true)
				.with(BIT_2, true)
				.with(BIT_3, true)
				.with(BIT_4, true)
				.with(BIT_5, true)
				.with(FACING, Direction.NORTH));
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new DiodeBlockEntity();
	}

	@Override
	public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
		BlockEntity be = world.getBlockEntity(pos);
		if(!world.isClient() && !player.isSneaking() && be instanceof DiodeBlockEntity) {
			Vec3d blockCenteredHit = new Vec3d(hitX, hitY, hitZ);
			blockCenteredHit = blockCenteredHit.subtract(0.5, 0.5, 0.5);
			switch (state.get(DiodeBlock.FACING)) {
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
			DiodeBlockEntity beDiode = (DiodeBlockEntity)be;
			if (CLICK_BIT_0.getBoundingBox().contains(blockCenteredHit)) {
				beDiode.setMask(0);
			}
			if (CLICK_BIT_1.getBoundingBox().contains(blockCenteredHit)) {
				beDiode.setMask(1);
			}
			if (CLICK_BIT_2.getBoundingBox().contains(blockCenteredHit)) {
				beDiode.setMask(2);
			}
			if (CLICK_BIT_3.getBoundingBox().contains(blockCenteredHit)) {
				beDiode.setMask(3);
			}
			if (CLICK_BIT_4.getBoundingBox().contains(blockCenteredHit)) {
				beDiode.setMask(4);
			}
			if (CLICK_BIT_5.getBoundingBox().contains(blockCenteredHit)) {
				beDiode.setMask(5);
			}
		}
		return true;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(FACING, BIT_0, BIT_1, BIT_2, BIT_3, BIT_4, BIT_5);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction side) {
		return getStrongRedstonePower(state, world, pos, side);
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction side) {
		if (side!=state.get(FACING).getOpposite()) return 0;
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof DiodeBlockEntity) {
			return ((DiodeBlockEntity)be).isActive()?16:0;
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
			if (be instanceof DiodeBlockEntity) {
				world.setBlockState(pos, state
						.with(BIT_0, bitToBool(0, world, pos))
						.with(BIT_1, bitToBool(1, world, pos))
						.with(BIT_2, bitToBool(2, world, pos))
						.with(BIT_3, bitToBool(3, world, pos))
						.with(BIT_4, bitToBool(4, world, pos))
						.with(BIT_5, bitToBool(5, world, pos)));
			}
		}
	}

	public boolean bitToBool(int bit, BlockView world, BlockPos pos) {
		BlockEntity be = world.getBlockEntity(pos);
		if(be instanceof DiodeBlockEntity) {
			DiodeBlockEntity beDiode = (DiodeBlockEntity) be;
			return (1<<bit & beDiode.getMask()) > 0;
		}
		return false;
	}
}
