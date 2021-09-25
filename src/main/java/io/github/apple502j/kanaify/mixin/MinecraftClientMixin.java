package io.github.apple502j.kanaify.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;

import io.github.apple502j.kanaify.Kanaifier;
import io.github.apple502j.kanaify.KanaifyMod;

@Mixin(MinecraftClient.class)
@Environment(EnvType.CLIENT)
public class MinecraftClientMixin {
	@Inject(at = @At("HEAD"), method = "close()V")
	private void close(CallbackInfo info) {
		if (Kanaifier.INSTANCE == null) return;
		try {
			Kanaifier.INSTANCE.close();
		} catch (Exception e) {
			KanaifyMod.LOGGER.warn("Error during Kanaifier shutdown: ", e);
		}
		Kanaifier.INSTANCE = null;
	}
}
