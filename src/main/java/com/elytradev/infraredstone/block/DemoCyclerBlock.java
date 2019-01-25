package com.elytradev.infraredstone.block;

import com.elytradev.infraredstone.block.entity.DemoCyclerBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DemoCyclerBlock extends ModuleBase {
	protected DemoCyclerBlock() {
		super("demo_cycler", DEFAULT_SETTINGS);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new DemoCyclerBlockEntity();
	}

	@Override
	public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof DemoCyclerBlockEntity) {
			if (!world.isClient) {
				((DemoCyclerBlockEntity) be).activate();
			} else {
				world.playSound(null, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCK, 1f, 1f);
			}
		}
		return true;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState state) {
		return true;
	}

	@Override
	public VoxelShape getRayTraceShape(BlockState state, BlockView view, BlockPos pos) {
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
