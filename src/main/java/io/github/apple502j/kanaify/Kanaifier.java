package io.github.apple502j.kanaify;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.github.ucchyocean.lc3.japanize.Japanizer;
import com.github.ucchyocean.lc3.japanize.provider.Provider;
import com.github.ucchyocean.lc3.japanize.provider.Providers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.util.Util;

public final class Kanaifier {
    public static final Logger LOGGER = LoggerFactory.getLogger("kanaifier");
    private final Provider kanaProvider;
    private final HttpClient client;
    public static Kanaifier INSTANCE = new Kanaifier();

    private Kanaifier() {
        this.kanaProvider = Providers.get();
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10L))
                .executor(Util.getMainWorkerExecutor())
                .build();
        LOGGER.info("Using {} kana provider", this.kanaProvider.getName());
    }

    public CompletableFuture<String> performGet(String url) {
        return this.request("GET", URI.create(url), HttpRequest.BodyPublishers.noBody(), Map.of());
    }

    public CompletableFuture<String> request(String method, URI uri, HttpRequest.BodyPublisher publisher, Map<String, String> headers) {
        HttpRequest.Builder request = HttpRequest
                .newBuilder(uri)
                .method(method, publisher)
                .timeout(Duration.ofSeconds(20L));

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            request.header(entry.getKey(), entry.getValue());
        }

        return client.sendAsync(request.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .thenApplyAsync((resp) -> {
                    if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
                        throw new UncheckedIOException(new IOException(String.format(Locale.ROOT, "Page returned code %d", resp.statusCode())));
                    }

                    return resp.body();
                });
    }

    public CompletableFuture<String> convert(String romaji) {
        String japanized = Japanizer.japanize(romaji);
        return this.kanaProvider.fetch(this, japanized).thenApply(this.kanaProvider::parse);
    }
}
