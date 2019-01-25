package com.elytradev.infraredstone.block.entity;

import com.elytradev.infraredstone.api.InfraRedstoneCapable;
import com.elytradev.infraredstone.api.InfraRedstoneSignal;
import com.elytradev.infraredstone.api.MultimeterProbeProvider;
import com.elytradev.infraredstone.block.ModBlocks;
import com.elytradev.infraredstone.logic.impl.InfraRedstoneHandler;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class InfraRedstoneBlockEntity extends IRComponentBlockEntity implements MultimeterProbeProvider, InfraRedstoneCapable {

	public InfraRedstoneBlockEntity() {
		super(ModBlocks.IN_RED_BLOCK_BE);
	}

	@Override
	public InfraRedstoneSignal getInfraRedstoneHandler(Direction inspectingFrom) {
		return InfraRedstoneHandler.ALWAYS_MAX;
	}

	@Override
	public StringTextComponent getProbeMessage() {
		TranslatableTextComponent i18n = new TranslatableTextComponent("msg.inred.multimeter.block");
		return new StringTextComponent(i18n.getFormattedText());
	}

	@Override
	public boolean canConnectIR(BlockPos dest, Direction dir) {
		return true;
	}

}
