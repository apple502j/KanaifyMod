package com.github.ucchyocean.lc3.japanize.provider;

import io.github.apple502j.kanaify.Kanaifier;

public enum Providers {
    YAHOO_KANA_API {
        public Provider getInstance() {
            return YahooKanaAPI.INSTANCE;
        }
    },
    GOOGLE_IME {
        public Provider getInstance() {
            return GoogleIME.INSTANCE;
        }
    };

    public Provider getInstance() {
        throw new AbstractMethodError();
    }
    public boolean isUsable() {
        return this.getInstance().isUsable();
    }
    public static Provider get() {
        for (Providers provider : Providers.values()) {
            if (provider.isUsable()) return provider.getInstance();
        }
        Kanaifier.LOGGER.warn("No provider available, using fallback");
        return NoopProvider.INSTANCE;
    }
}
