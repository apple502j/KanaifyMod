package io.github.apple502j.kanaify.mixin;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.network.MessageType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import io.github.apple502j.kanaify.Kanaifier;
import io.github.apple502j.kanaify.KanaifiyUtil;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
	@Redirect(method = "handleMessage(Lnet/minecraft/server/filter/TextStream$Message;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Ljava/util/function/Function;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
	private void broadcastKanaified(PlayerManager playerManager, Text serverMessage, Function<ServerPlayerEntity, Text> playerMessageFactory, MessageType playerMessageType, UUID sender) {
		String m = KanaifiyUtil.getChatMessage(serverMessage);
		CompletableFuture<String> future = Kanaifier.INSTANCE == null ? CompletableFuture.completedFuture(m) : Kanaifier.INSTANCE.convert(m);
		future.thenAcceptAsync((kanaified) -> {
			Text kanaText = KanaifiyUtil.createChatMessage(serverMessage, kanaified);
			playerManager.broadcast(kanaText, (_player) -> kanaText, MessageType.CHAT, sender);
		});
	}
}
