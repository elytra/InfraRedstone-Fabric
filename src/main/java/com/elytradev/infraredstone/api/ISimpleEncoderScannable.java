package com.elytradev.infraredstone.api;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * Version of IEncoderScannable that can be implemented by non-TE Blocks.
 */
public interface ISimpleEncoderScannable {

	/**
	 * Has extra information from the standard IEncoderScannable to compensate for not having a TE
	 * @param world the current world.
	 * @param pos the position of your object.
	 * @param state the current blockstate of your object.
	 * @param inspectingFrom the direction the encoder is looking from.
	 * @return a value from 0-63 depending on the state of your object and the given parameters.
	 * See the Encoder Guidelines page on the InfraRedstone wiki for usage examples.
	 * It may be helpful to format the value in binary: `0b00_0000`
	 */
	int getComparatorValue(World world, BlockPos pos, BlockState state, Direction inspectingFrom);
}
