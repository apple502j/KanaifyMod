package io.github.apple502j.kanaify;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KanaifyMod implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("kanaify");

	@Override
	public void onInitialize() {
		if (Kanaifier.INSTANCE != null) Kanaifier.INSTANCE.close();
		Kanaifier.INSTANCE = new Kanaifier();
		LOGGER.info("Kanaifier initialized.");
	}
}
