package io.github.apple502j.kanaify;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.io.CharStreams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.github.ucchyocean.lc3.japanize.Japanizer;
import com.github.ucchyocean.lc3.japanize.provider.Provider;
import com.github.ucchyocean.lc3.japanize.provider.Providers;

import net.minecraft.util.thread.TaskExecutor;

public class Kanaifier {
    public static final Logger LOGGER = LogManager.getLogger("kanaifier");
    private static final AtomicInteger NEXT_WORKER_ID = new AtomicInteger(1);
    private static final int MAX_THREADS = 10;
    private static final ThreadFactory THREAD_FACTORY;

    private ExecutorService executorService;
    private Executor taskExecutor;
    private Provider kanaProvider;
    public static Kanaifier INSTANCE = null;

    static {
        THREAD_FACTORY = (runnable -> {
                Thread thread = new Thread(runnable);
                thread.setName("KanaifyThread" + NEXT_WORKER_ID.getAndIncrement());
                return thread;
        });
    }

    public Kanaifier() {
        this.kanaProvider = Providers.get();
        this.executorService = Executors.newFixedThreadPool(MAX_THREADS, THREAD_FACTORY);
        this.taskExecutor = TaskExecutor.create(this.executorService, "Kanaifier executor")::send;
        LOGGER.info("Using " + this.kanaProvider.getName() + " kana provider");
    }

    public void close() {
        this.executorService.shutdownNow();
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
        }, this.taskExecutor);
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
        }).thenApply((text) -> text.isEmpty() ? kana : KanaifiyUtil.format(kana, text));
    }
}
