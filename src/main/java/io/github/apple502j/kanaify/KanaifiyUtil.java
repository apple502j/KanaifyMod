package io.github.apple502j.kanaify;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.text.*;

public class KanaifiyUtil {
    private static final String CHAT_TEXT_KEY = "chat.type.text";
    private static final String CHAT_FORMAT = "%s (%s)";
    private static final Logger LOGGER = LogManager.getLogger("kanaifier");

    public static String getChatMessage(Text text) {
        if (text == null) return "";
        if (text instanceof TranslatableText chatText) {
            if (CHAT_TEXT_KEY.equals(chatText.getKey())) {
                try {
                    StringVisitable message = chatText.getArg(1);
                    return message.getString();
                } catch (TranslationException exc) {
                }
            }
        }
        LOGGER.warn("getChatMessage received unexpected input " + text);
        return text.toString();
    }

    public static Text createChatMessage(Text source, String original, String message) {
        if (source == null) return new LiteralText("");
        if (source instanceof TranslatableText chatText) {
            if (CHAT_TEXT_KEY.equals(chatText.getKey())) {
                try {
                    StringVisitable username = chatText.getArg(0);
                    return new TranslatableText(CHAT_TEXT_KEY, username.getString(), String.format(CHAT_FORMAT, message, original));
                } catch (TranslationException exc) {
                }
            }
        }
        LOGGER.warn("createChatMessage received unexpected input " + source);
        return source;
    }
}
