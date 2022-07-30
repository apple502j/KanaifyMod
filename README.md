# Kanaify Mod
日本語での説明は以下を参照。

Kanaify Mod is a Romaji-to-Kana/Kanji conversion mod for Minecraft 1.17 with Fabric. This mod automatically converts romaji-only messages in chat and sends the converted text.

## Requirements
This mod requires Fabric mod loader, which can be downloaded from [their website](https://fabricmc.net/use/). **You'll also need Fabric API.**

This mod currently supports Minecraft 1.19.1. This mod has to be installed on the server to work in multiplayer.

## Usage
Put the mod JAR file inside the mods folder.

By default, this mod uses Google Translate API for Kana-Kanji conversion. This is the recommended mode.

To use the Yahoo! Japan Kana-Kanji conversion API, `YAHOO_API_CLIENT_ID` environment variable must be set to the client ID of the registered Yahoo! Japan application. (Client secret is not needed.) To register the app, please check the [Yahoo! Developer Network](https://e.developer.yahoo.co.jp/register) website. **Note that the API is ratelimited (50k messages per day).** 

## Credits and Licensing
This source code is licensed under LGPL version 3.0.

This is a ported version of [LunaChat](https://github.com/ucchyocean/LunaChat) Spigot plugin by [ucchyocean](https://github.com/ucchyocean). The source contains part of the LunaChat code licensed under LGPL version 3.0.

Kanaify Mod uses [Web Services by Yahoo! JAPAN](https://developer.yahoo.co.jp/sitemap/).

# Kanaify Mod
See above for English description.

Kanaify Modは、Minecraft Fabric 1.19用のローマ字かな漢字変換modです。チャットに送信されたローマ字のメッセージを自動的に変換し、変換されたメッセージを送信します。

## 要件
Fabric modローダーが必要です。[ウェブサイト](https://fabricmc.net/use/)からダウンロードできます。**Fabric APIも必要です。**

このmodはMinecraft 1.19.1をサポートしています。マルチプレイヤーで使用する場合は、サーバー側にインストールしてください。

## 使用方法
JARファイルをmodsフォルダー内に入れれば完了です。

既定では、かな漢字変換にはGoogle翻訳APIが利用されます。このモードをおすすめします。

Yahoo! Japanのかな漢字変換APIを利用する場合は、`YAHOO_API_CLIENT_ID`環境変数にYahoo! Japanにて登録されたアプリケーションのクライアントIDを設定してください。(クライアントシークレットは不要です。) 登録に関しての詳細は、[Yahoo! デベロッパーネットワーク](https://e.developer.yahoo.co.jp/register)ウェブサイトを確認してください。**このAPIには速度制限(5万件/日)があります。**

## クレジットとライセンス
ソースコードはLGPL バージョン3.0のもとでライセンスされています。

このmodは、[ucchyocean](https://github.com/ucchyocean)さんの[LunaChat](https://github.com/ucchyocean/LunaChat) Spigotプラグインの移植版です。ソースコードには、LGPL バージョン3.0のもとでライセンスされているLunaChatのソースコードを含みます。

[Webサービス by Yahoo! JAPAN](https://developer.yahoo.co.jp/sitemap/)