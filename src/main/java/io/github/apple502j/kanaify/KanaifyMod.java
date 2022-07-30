package io.github.apple502j.kanaify;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;

public class KanaifyMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("kanaify");

	@Override
	public void onInitialize() {
		// Load class
		Objects.requireNonNull(Kanaifier.INSTANCE);
		LOGGER.info("Kanaifier initialized.");
	}
}
