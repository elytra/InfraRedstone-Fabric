package com.elytradev.infraredstone.api;

/**
 * This interface indicates that a block should not be connected to diagonally, outside of special cases.
 * The only current special case is for a non-axis-restricted wire reaching diagonally up to an axis-locked element.
 *
 */
public interface AxisRestricted {
}
