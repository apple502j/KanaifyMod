package io.github.apple502j.kanaify;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import com.github.ucchyocean.lc3.japanize.Japanizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.text.Text;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.message.v1.ServerMessageDecoratorEvent;

public class KanaifyMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("kanaify");

	@Override
	public void onInitialize() {
		// Load class
		Objects.requireNonNull(Kanaifier.INSTANCE);

		ServerMessageDecoratorEvent.EVENT.register(ServerMessageDecoratorEvent.CONTENT_PHASE, (sender, message) -> {
			String raw = message.getString();
			if (raw.isBlank() || !Japanizer.needsJapanize(raw)) return CompletableFuture.completedFuture(message);
			return Kanaifier.INSTANCE.convert(raw).thenApply(
					(converted) -> (Text)Text.literal(String.format(Locale.ROOT, "%s (%s)", raw, converted))
			).exceptionally((exc) -> {
				KanaifyMod.LOGGER.warn("Error while kanaifying message {}", raw);
				KanaifyMod.LOGGER.warn("Stack trace:", exc);
				return message;
			});
		});

		LOGGER.info("Kanaifier initialized.");
	}
}
