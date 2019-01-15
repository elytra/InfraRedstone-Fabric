package com.elytradev.infraredstone.block;

import com.elytradev.infraredstone.block.entity.ColorLEDBlockEntity;
import com.elytradev.infraredstone.block.entity.LEDBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ColorLEDBlock extends ModuleBase {
	public static final BooleanProperty LIT = Properties.LIT;

	protected ColorLEDBlock() {
		super("rgb_led", DEFAULT_SETTINGS);
		this.setDefaultState(this.getStateFactory().getDefaultState().with(LIT, false));
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new ColorLEDBlockEntity();
	}

	@Override
	public int getLuminance(BlockState state) {
		return state.get(LIT)? 15 : 0;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(LIT);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos posFrom) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof ColorLEDBlockEntity) {
			world.setBlockState(pos, state.with(LIT, ((ColorLEDBlockEntity)be).getIsLit()));
		}
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState state) {
		return true;
	}

	@Override
	public VoxelShape getBoundingShape(BlockState state, BlockView view, BlockPos pos) {
		return VoxelShapes.fullCube();
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, VerticalEntityPosition collisionPos) {
		return VoxelShapes.fullCube();
	}

	@Override
	public boolean canBlockStay(World world, BlockPos pos) {
		return true;
	}
}
