package com.github.ucchyocean.lc3.japanize.provider;

import java.net.URI;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.apple502j.kanaify.Kanaifier;

import net.minecraft.util.Util;

public class YahooKanaAPI implements Provider {
    public static Provider INSTANCE = new YahooKanaAPI();
    private static final String API_CLIENT_ID = "YAHOO_API_CLIENT_ID";
    private static final URI URL = URI.create("https://jlp.yahooapis.jp/JIMService/V2/conversion");
    private static final Gson GSON = new Gson();
    private static final JsonArray OPTIONS = Util.make(new JsonArray(), (opts) -> {
        opts.add("hiragana");
        opts.add("katakana");
        opts.add("alphanumeric");
    });
    private static final JsonArray DICTIONARY = Util.make(new JsonArray(), (opts) -> {
        opts.add("base");
        opts.add("name");
        opts.add("place");
    });

    public boolean isUsable() {
        Map<String, String> env = System.getenv();
        return env.containsKey(API_CLIENT_ID);
    }

    public CompletableFuture<String> fetch(Kanaifier kanaifier, String message) {
        JsonObject gson = new JsonObject();
        gson.addProperty("jsonrpc", "2.0");
        gson.addProperty("id", "1234-1");
        gson.addProperty("method", "jlp.jimservice.conversion");

        JsonObject params = new JsonObject();
        params.addProperty("q", message);
        params.addProperty("format", "hiragana");
        params.addProperty("mode", "kanakanji");
        params.add("option", OPTIONS);
        params.add("dictionary", DICTIONARY);
        params.addProperty("results", 1);

        gson.add("params", params);

        return kanaifier.request(
            "POST",
            URL,
            HttpRequest.BodyPublishers.ofString(GSON.toJson(gson), StandardCharsets.UTF_8),
            Map.of(
                "Content-Type", "application/json",
                "User-Agent", "Yahoo AppID: " + System.getenv(API_CLIENT_ID)
            )
        );
    }

    public String parse(String value) {
        StringBuilder result = new StringBuilder();
        JsonObject gson = GSON.fromJson(value, JsonObject.class);
        for ( JsonElement response : gson.getAsJsonObject("result").getAsJsonArray("segment") ) {
            result.append(response.getAsJsonObject().getAsJsonArray("candidate").get(0).getAsString());
        }
        return result.toString();
    }
}
