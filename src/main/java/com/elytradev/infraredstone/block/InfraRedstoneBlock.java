package com.elytradev.infraredstone.block;

import com.elytradev.infraredstone.api.InfraRedstoneComponent;
import com.elytradev.infraredstone.api.InfraRedstoneWire;
import com.elytradev.infraredstone.block.entity.InfraRedstoneBlockEntity;
import net.fabricmc.fabric.block.FabricBlockSettings;
import net.fabricmc.fabric.tags.FabricItemTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class InfraRedstoneBlock extends ModuleBase implements InfraRedstoneComponent {

	protected InfraRedstoneBlock() {
		super("infra_redstone_block", FabricBlockSettings.create(Material.PART).setStrength(1f, 1f).breakByTool(FabricItemTags.PICKAXES).build());
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new InfraRedstoneBlockEntity();
	}

	@Override
	public boolean canPlaceAt(BlockState state, ViewableWorld world, BlockPos pos) {
		return true;
	}

	@Override
	public boolean isSimpleFullBlock(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return true;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return VoxelShapes.fullCube();
	}

	@Override
	public VoxelShape getBoundingShape(BlockState state, BlockView view, BlockPos pos) {
		return VoxelShapes.fullCube();
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
