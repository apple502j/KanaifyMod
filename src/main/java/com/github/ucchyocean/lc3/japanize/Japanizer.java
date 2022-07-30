/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2014
 */
package com.github.ucchyocean.lc3.japanize;

/**
 * ローマ字表記を漢字変換して返すユーティリティ
 * @author ucchy
 */
public class Japanizer {
    /**
     * メッセージの日本語化をする
     * @param keywordLocked
     * @return
     */
    public static String japanize(String keywordLocked) {

        // カナ変換
        String japanized = YukiKanaConverter.fixBrackets(YukiKanaConverter.conv(keywordLocked));

        // 返す
        return japanized.trim();
    }

    /**
     * 日本語化が必要かどうかを判定する
     * @param org
     * @return
     */
    public static boolean needsJapanize(String org) {
        return ( org.getBytes().length == org.length()
                && !org.matches("[ \\uFF61-\\uFF9F]+")
                && !org.matches("^[^a-zA-Z]$"));
    }
}
