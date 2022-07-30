package com.github.ucchyocean.lc3.japanize.provider;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.github.apple502j.kanaify.Kanaifier;

public class GoogleIME implements Provider {
    public static Provider INSTANCE = new GoogleIME();
    private static final String URL =
            "https://www.google.com/transliterate?langpair=ja-Hira|ja&text=";

    public CompletableFuture<String> fetch(Kanaifier kanaifier, String message) {
        return kanaifier.performGet(URL + URLEncoder.encode(message , StandardCharsets.UTF_8));
    }

    public String parse(String value) {
        StringBuilder result = new StringBuilder();
        for ( JsonElement response : new Gson().fromJson(value, JsonArray.class) ) {
            result.append(response.getAsJsonArray().get(1).getAsJsonArray().get(0).getAsString());
        }
        return result.toString();
    }
}
