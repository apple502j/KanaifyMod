package io.github.apple502j.kanaify.mixin;

import com.github.ucchyocean.lc3.japanize.Japanizer;
import io.github.apple502j.kanaify.Kanaifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin extends ServerCommonNetworkHandler {
    @Unique
    private static final Logger LOGGER = LoggerFactory.getLogger("kanaify");

    public ServerPlayNetworkHandlerMixin(MinecraftServer server, ClientConnection connection, ConnectedClientData clientData) {
        super(server, connection, clientData);
    }

    @Inject(method = "handleDecoratedMessage", at = @At("RETURN"))
    private void onMessageSent(SignedMessage message, CallbackInfo ci) {
        String original = message.getContent().getString();
        if (original.isBlank() || !Japanizer.needsJapanize(original)) return;
        Kanaifier.INSTANCE.convert(original).thenAcceptAsync(converted -> {
            PlayerManager manager = this.server.getPlayerManager();
            manager.broadcast(Text.literal("%s (%s)".formatted(original, converted)), false);
        }, this.server).exceptionally(e -> {
            LOGGER.error("Failed to kanaify: {}", original);
            LOGGER.error("Caused by:", e.getCause());
            return null;
        });
    }
}
