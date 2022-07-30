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

public final class Kanaifier {
    public static final Logger LOGGER = LoggerFactory.getLogger("kanaifier");
    private final Provider kanaProvider;
    private final HttpClient client;
    public static Kanaifier INSTANCE = new Kanaifier();

    private Kanaifier() {
        this.kanaProvider = Providers.get();
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10L))
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
                })
                .handle((res, exc) -> {
                    if (exc instanceof UncheckedIOException) {
                        exc = exc.getCause();
                    }

                    if (exc instanceof IOException ioExc) {
                        LOGGER.warn("Request failed to {}", uri);
                        LOGGER.warn("Stack trace: ", exc);
                        throw new UncheckedIOException("Request failed", ioExc);
                    } else if (exc != null) {
                        LOGGER.error("Uncaught exception", exc);
                        throw new RuntimeException("Uncaught exception while processing", exc);
                    }

                    return res;
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
