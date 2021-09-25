package com.github.ucchyocean.lc3.japanize.provider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.util.Util;

import io.github.apple502j.kanaify.Kanaifier;

public class YahooKanaAPI implements Provider {
    public static Provider INSTANCE = new YahooKanaAPI();
    private static final String API_CLIENT_ID = "YAHOO_API_CLIENT_ID";
    private static final String URL = "https://jlp.yahooapis.jp/JIMService/V2/conversion";
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
        return kanaifier.request("POST", URL, (urlconn) -> {
            urlconn.setDoOutput(true);
            urlconn.setRequestProperty("Content-Type", "application/json");
            urlconn.setRequestProperty("User-Agent", "Yahoo AppID: " + System.getenv(API_CLIENT_ID));
        }, (urlconn) -> {
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

            try {
                urlconn.getOutputStream().write(gson.toString().getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                Kanaifier.LOGGER.warn("IOException while opening stream:", e);
            }
        });
    }

    public String parse(String value) {
        StringBuilder result = new StringBuilder();
        JsonObject gson = new Gson().fromJson(value, JsonObject.class);
        for ( JsonElement response : gson.getAsJsonObject("result").getAsJsonArray("segment") ) {
            result.append(response.getAsJsonObject().getAsJsonArray("candidate").get(0).getAsString());
        }
        return result.toString();
    }
}
