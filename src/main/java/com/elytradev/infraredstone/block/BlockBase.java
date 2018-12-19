package com.elytradev.infraredstone.block;

import com.elytradev.infraredstone.api.InfraRedstoneCapable;
import com.elytradev.infraredstone.api.SimpleInfraRedstoneSignal;
import com.elytradev.infraredstone.util.C28n;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.item.TooltipOptions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;

public class BlockBase extends Block implements NamedBlock {

	public String name;

	public BlockBase(String name, Settings settings) {
		super(settings);
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Block getBlock() {
		return this;
	}

	/** Ask the *destination block* if it can be connected to. {@code from} side has the same semantics as Capability sides */
	public static boolean canConnect(BlockView world, BlockPos pos, Direction from) {
		if (world.getBlockState(pos).isAir()) return false;

		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block==ModBlocks.INFRA_REDSTONE || block==ModBlocks.IN_RED_SCAFFOLD) return true;
		if (block instanceof SimpleInfraRedstoneSignal) {
			return ((SimpleInfraRedstoneSignal)block).canConnectIR(world, pos, state, from);
		}
		BlockEntity be = world.getBlockEntity(pos);
		if (!(be instanceof InfraRedstoneCapable)) return false;

		return ((InfraRedstoneCapable)be).canConnectToSide(from);
	}

//	@Override
//	public void addInformation(ItemStack itemStack, BlockView blockView, List<TextComponent> tooltip, TooltipOptions tooltipOptions) {
//		if (Gui.isShiftPressed()) {
//			C28n.formatList(tooltip, "tooltip.inred." + name);
//		} else C28n.formatList(tooltip,"preview.inred." + name);
//	}
}
