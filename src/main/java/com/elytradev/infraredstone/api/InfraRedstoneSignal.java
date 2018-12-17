package com.elytradev.infraredstone.api;

import net.minecraft.util.math.Direction;

/**
 * This interface describes an IR signal value to other InfraRedstone logic tiles.
 */
public interface InfraRedstoneSignal {

	/**
	 * @return the signal vlaue output by the module.
	 * It may be helpful to format the value in binary: `0b00_0000`
	 */
	int getSignalValue();
}
