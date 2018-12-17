package com.elytradev.infraredstone.util;

import net.minecraft.client.resource.language.I18n;

public class CommonProxy {
	public String i18nFormat(String key, Object[] format) {
		return I18n.translate(key, format);
	}

	public boolean i18nContains(String key) {
		return I18n.hasTranslation(key);
	}
}
