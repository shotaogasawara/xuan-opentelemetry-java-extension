## 概要

xuan向けのopentelemetry extension置き場です

## 対応ライブラリ・バージョン

spring boot、tomcat 10以上が対象です。

opentelemetry-agentのバージョンはv1.26.0 対応です。それ以外のバージョンでは動かない可能性があるので注意してください。

agentのバージョンにそろえて変更が必要です

## CI

リント: `./gradlew :spotlessApply` で解消する
