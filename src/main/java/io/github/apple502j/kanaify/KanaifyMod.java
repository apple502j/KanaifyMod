package io.github.apple502j.kanaify;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;

public class KanaifyMod implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("kanaify");

	@Override
	public void onInitialize() {
		Kanaifier.INSTANCE = new Kanaifier();
		LOGGER.info("Kanaifier initialized.");
	}
}
