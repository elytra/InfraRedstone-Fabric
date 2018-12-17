package com.elytradev.infraredstone.block;

import com.elytradev.infraredstone.util.C28n;
import net.minecraft.block.Block;
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

//	@Override
//	public void addInformation(ItemStack itemStack, BlockView blockView, List<TextComponent> tooltip, TooltipOptions tooltipOptions) {
//		if (Gui.isShiftPressed()) {
//			C28n.formatList(tooltip, "tooltip.inred." + name);
//		} else C28n.formatList(tooltip,"preview.inred." + name);
//	}
}
