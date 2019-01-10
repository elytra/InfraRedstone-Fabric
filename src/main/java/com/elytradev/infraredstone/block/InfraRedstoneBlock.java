package com.elytradev.infraredstone.block;

import com.elytradev.infraredstone.block.entity.InfraRedstoneBlockEntity;
import net.fabricmc.fabric.block.FabricBlockSettings;
import net.fabricmc.fabric.tags.FabricItemTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;

public class InfraRedstoneBlock extends ModuleBase {

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
}
