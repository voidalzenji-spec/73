# 73 — بيئة تطوير أصلية لأندرويد (Rust + Kotlin)

نواة Rust (`core/`) تُصدَّر إلى Kotlin عبر UniFFI، وتُبنى بـ cargo-ndk لمعمارية aarch64 مع محاذاة 16KB.

## الهيكل
- `core/` — نواة المحرر (rope) والطرفية (portable-pty)
- `uniffi-bindgen/` — مولّد ارتباطات Kotlin
- `android/` — تطبيق Kotlin + خدمة دائمة (SPECIAL_USE, START_STICKY)

## البناء
```
rustup target add aarch64-linux-android
cargo install cargo-ndk
cd android && gradle wrapper && ./gradlew :app:assembleDebug
```
(يتطلب JDK 17 وAndroid SDK 35 وNDK؛ متغير ANDROID_NDK_HOME.)

## خارطة الطريق
- [x] 1) هيكل النواة + جسر UniFFI + خدمة دائمة + طرفية أولية
- [ ] 1) tree-sitter، مدير الملفات داخل filesDir، واجهة SAF للاستيراد/التصدير
- [ ] 2) مدير حزم اللغات (Bionic aarch64) + gitoxide + git/OpenSSH
- [ ] 3) aapt2 أصلي + Gradle + Shizuku، خادم ويب محلي، WebView/Custom Tabs، ألعاب 2D
- [ ] 4) التصدير المتقاطع عبر zig cc، rcodesign/xtool، مولّد GitHub Actions

## ملاحظات توزيع
نسخة Google Play: الأدوات داخل jniLibs فقط. النسخة الكاملة (تنزيل اللغات): F-Droid / GitHub Releases (قيد W^X).
