package com.elytradev.infraredstone.api;

import net.minecraft.text.StringTextComponent;

public interface IMultimeterProbe {

    /**
     * Adds information for the Multimeter to display on use.
     * @return the phrase the Multimeter should write.
     * Should contain the signal output of a Module, along with any other important information.
     * Signal output should be formated "0b00_0000 (0)"
     */
    StringTextComponent getProbeMessage();

}
