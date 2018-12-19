package com.elytradev.infraredstone.item;

import com.elytradev.infraredstone.api.MultimeterProbeProvider;
import com.elytradev.infraredstone.block.ModBlocks;
import com.elytradev.infraredstone.logic.InRedLogic;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MultimeterItem extends ItemBase {

	public MultimeterItem() {
		super("multimeter");
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext ctx) {
		World world = ctx.getWorld();
		BlockPos pos = ctx.getPos();
		Direction facing = ctx.getFacing();
		Block block = world.getBlockState(pos).getBlock();
		BlockEntity be = world.getBlockEntity(pos);
		PlayerEntity player = ctx.getPlayer();
		String value;
		TranslatableTextComponent i18n;
		StringTextComponent message;
		if (world.isClient) return ActionResult.PASS.PASS;
		if (be instanceof MultimeterProbeProvider) {
			message = ((MultimeterProbeProvider)be).getProbeMessage();
		} else if (block == ModBlocks.INFRA_REDSTONE || block == ModBlocks.IN_RED_SCAFFOLD) {
			value = getValue(world, pos, facing);
			i18n = new TranslatableTextComponent("msg.inred.multimeter.cable");
			message = new StringTextComponent(i18n.getFormattedText()+value);
//		} else if (block == ModBlocks.IN_RED_BLOCK) {
//			i18n = new TranslatableTextComponent("msg.inred.multimeter.block");
//			message = new StringTextComponent(i18n.getFormattedText());
//        } else if (block == ModBlocks.DEVICE_LIQUID_CRYSTAL) {
//            value = getValue(world, pos, facing);
//            i18n = new TextComponentTranslation("msg.inred.multimeter.direction");
//            message = new TextComponentString(i18n.getFormattedText()+" "+facing.getName()+value);
		} else if (InRedLogic.checkCandidacy(world, pos, player.getHorizontalFacing())) {
			value = getValue(world, pos, player.getHorizontalFacing());
			i18n = new TranslatableTextComponent("msg.inred.multimeter.direction");
			message = new StringTextComponent(i18n.getFormattedText() + " " + player.getHorizontalFacing().getName() + value);
		} else {
			return ActionResult.PASS;
		}
		player.addChatMessage(message, true);
		return ActionResult.SUCCESS.SUCCESS;
	}

	private String getValue(World world, BlockPos pos, Direction face) {
		int signal = InRedLogic.findIRValue(world, pos, face.getOpposite());
		int bit1 = ((signal & 0b00_0001) != 0) ? 1:0;
		int bit2 = ((signal & 0b00_0010) != 0) ? 1:0;
		int bit3 = ((signal & 0b00_0100) != 0) ? 1:0;
		int bit4 = ((signal & 0b00_1000) != 0) ? 1:0;
		int bit5 = ((signal & 0b01_0000) != 0) ? 1:0;
		int bit6 = ((signal & 0b10_0000) != 0) ? 1:0;
		return ": 0b"+bit6+bit5+"_"+bit4+bit3+bit2+bit1+" ("+signal+")";
	}
}
