package com.github.ucchyocean.lc3.japanize.provider;

import java.util.concurrent.CompletableFuture;

import io.github.apple502j.kanaify.Kanaifier;

public class NoopProvider implements Provider {
    public static final Provider INSTANCE = new NoopProvider();

    public CompletableFuture<String> fetch(Kanaifier kanaifier, String message) {
        return CompletableFuture.completedFuture(message);
    }
}
