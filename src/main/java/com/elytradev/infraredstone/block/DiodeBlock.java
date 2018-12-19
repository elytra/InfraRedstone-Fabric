package com.elytradev.infraredstone.block;

import com.elytradev.infraredstone.block.entity.DiodeBlockEntity;
import net.fabricmc.fabric.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.BlockRenderLayer;
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

	public static final VoxelShape CLICK_BIT_0 = Block.createCubeShape(10, 3, 10, 11, 5.1, 14);
	public static final VoxelShape CLICK_BIT_1 = Block.createCubeShape(9, 3, 8, 10, 5.1, 12);
	public static final VoxelShape CLICK_BIT_2 = Block.createCubeShape(8, 3, 10, 9, 5.1, 14);
	public static final VoxelShape CLICK_BIT_3 = Block.createCubeShape(7, 3, 8, 8, 5.1, 12);
	public static final VoxelShape CLICK_BIT_4 = Block.createCubeShape(6, 3, 10, 7, 5.1, 14);
	public static final VoxelShape CLICK_BIT_5= Block.createCubeShape(5, 3, 8, 6, 5.1, 12);

	public static final VoxelShape SHAPE_BIT_0 = Block.createCubeShape(10, 3, 11, 11, 5, 13);
	public static final VoxelShape SHAPE_BIT_1 = Block.createCubeShape(9, 3, 9, 10, 5, 11);
	public static final VoxelShape SHAPE_BIT_2 = Block.createCubeShape(8, 3, 11, 9, 5, 13);
	public static final VoxelShape SHAPE_BIT_3 = Block.createCubeShape(7, 3, 9, 8, 5, 11);
	public static final VoxelShape SHAPE_BIT_4 = Block.createCubeShape(6, 3, 11, 7, 5, 13);
	public static final VoxelShape SHAPE_BIT_5= Block.createCubeShape(5, 3, 9, 6, 5, 11);
	public static final VoxelShape LOCKED_BIT_0 = Block.createCubeShape(10, 3, 11, 11, 4, 13);
	public static final VoxelShape LOCKED_BIT_1 = Block.createCubeShape(9, 3, 9, 10, 4, 11);
	public static final VoxelShape LOCKED_BIT_2 = Block.createCubeShape(8, 3, 11, 9, 4, 13);
	public static final VoxelShape LOCKED_BIT_3 = Block.createCubeShape(7, 3, 9, 8, 4, 11);
	public static final VoxelShape LOCKED_BIT_4 = Block.createCubeShape(6, 3, 11, 7, 4, 13);
	public static final VoxelShape LOCKED_BIT_5= Block.createCubeShape(5, 3, 9, 6, 4, 11);

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
			System.out.println(blockCenteredHit);
			System.out.println(CLICK_BIT_0.getBoundingBox());
			System.out.println(CLICK_BIT_0.getBoundingBox().contains(blockCenteredHit));
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
	public boolean isSimpleFullBlock(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return false;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.SOLID;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return VoxelShapes.empty();
	}

	@Override
	public VoxelShape getBoundingShape(BlockState state, BlockView view, BlockPos pos) {

//		VoxelShape aabb = state.get(BIT_0)? VoxelShapes.union(BASE_SHAPE, SHAPE_BIT_0) : VoxelShapes.union(BASE_SHAPE, LOCKED_BIT_0);
//		aabb = state.get(BIT_1)? VoxelShapes.union(aabb, SHAPE_BIT_1) : VoxelShapes.union(aabb, LOCKED_BIT_1);
//		aabb = state.get(BIT_2)? VoxelShapes.union(aabb, SHAPE_BIT_2) : VoxelShapes.union(aabb, LOCKED_BIT_2);
//		aabb = state.get(BIT_3)? VoxelShapes.union(aabb, SHAPE_BIT_3) : VoxelShapes.union(aabb, LOCKED_BIT_3);
//		aabb = state.get(BIT_4)? VoxelShapes.union(aabb, SHAPE_BIT_4) : VoxelShapes.union(aabb, LOCKED_BIT_4);
//		aabb = state.get(BIT_5)? VoxelShapes.union(aabb, SHAPE_BIT_5) : VoxelShapes.union(aabb, LOCKED_BIT_5);
		return BASE_SHAPE;
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

	public boolean bitToBool(int bit, BlockView world, BlockPos pos) {
		BlockEntity be = world.getBlockEntity(pos);
		if(be instanceof DiodeBlockEntity) {
			DiodeBlockEntity beDiode = (DiodeBlockEntity) be;
			return (1<<bit & beDiode.getMask()) > 0;
		}
		return false;
	}

	public boolean canBlockStay(World world, BlockPos pos) {
		return world.getBlockState(pos.down()).hasSolidTopSurface(world, pos.down());
	}
}
