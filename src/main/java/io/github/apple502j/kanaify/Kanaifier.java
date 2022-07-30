package io.github.apple502j.kanaify;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.github.ucchyocean.lc3.japanize.Japanizer;
import com.github.ucchyocean.lc3.japanize.provider.Provider;
import com.github.ucchyocean.lc3.japanize.provider.Providers;
import com.google.common.io.CharStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.util.Util;

public final class Kanaifier {
    public static final Logger LOGGER = LoggerFactory.getLogger("kanaifier");
    private final Provider kanaProvider;
    public static Kanaifier INSTANCE = new Kanaifier();

    private Kanaifier() {
        this.kanaProvider = Providers.get();
        LOGGER.info("Using {} kana provider", this.kanaProvider.getName());
    }

    public CompletableFuture<String> performGet(String url) {
        return this.request("GET", url, (handler) -> {}, (handler) -> {});
    }

    public CompletableFuture<String> request(String method, String url, Consumer<HttpURLConnection> handler, Consumer<HttpURLConnection> poster) {
        Objects.requireNonNull(url);
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {

            HttpURLConnection urlconn = null;
            BufferedReader reader = null;
            String value = "";
            try {
                URL dest = new URL(url);
                urlconn = (HttpURLConnection) dest.openConnection();
                urlconn.setRequestMethod(method);
                urlconn.setInstanceFollowRedirects(false);
                handler.accept(urlconn);
                urlconn.connect();
                poster.accept(urlconn);
                reader = new BufferedReader(
                        new InputStreamReader(urlconn.getInputStream(), StandardCharsets.UTF_8));
                value = CharStreams.toString(reader);
            } catch (Exception e) {
                LOGGER.warn("Connection to \"" + url + "\" failed:", e);
                return "";
            } finally {
                if (urlconn != null) urlconn.disconnect();
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ioexc) {}
                }
            }
            return value;
        }, Util.getIoWorkerExecutor());
        return future.exceptionally((e) -> {
            LOGGER.warn("Connection to \"" + url + "\" failed:", e);
            return "";
        });
    }

    public CompletableFuture<String> convert(String kana) {
        String japanized = Japanizer.japanize(kana);
        if (japanized.isEmpty()) return CompletableFuture.completedFuture(kana);
        return this.kanaProvider.fetch(this, japanized).thenApply((value) -> this.kanaProvider.parse(value)).exceptionally((e) -> {
            LOGGER.warn("API returned unexpected result:", e);
            return "";
        });
    }
}
