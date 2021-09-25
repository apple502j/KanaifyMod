package com.github.ucchyocean.lc3.japanize.provider;

import java.util.concurrent.CompletableFuture;

import io.github.apple502j.kanaify.Kanaifier;

public interface Provider {
    CompletableFuture<String> fetch(Kanaifier kanaifier, String message);
    default String parse(String value) {
        return value;
    }
    default boolean isUsable() {
        return true;
    }
    default String getName() {
        return this.getClass().getName();
    }
}
