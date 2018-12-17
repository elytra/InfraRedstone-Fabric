package com.elytradev.infraredstone.api;

import net.minecraft.util.math.Direction;

/**
 * This interface describes an "inspection" signal value to InfraComparators.
 */
public interface IEncoderScannable {
	/**
	 * @param inspectingFrom the direction the encoder is looking from.
	 * @return a value from 0-63 depending on the state of your object.
	 * See the Encoder Guidelines page on the InfraRedstone wiki for usage examples.
	 * It may be helpful to format the value in binary: `0b00_0000`
	 */
	int getComparatorValue(Direction inspectingFrom);
}
