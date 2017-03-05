-keep class karov.** { *; }
-dontwarn karov.internal.**
-keep class **$$ViewInjector { *; }
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
-keepclasseswithmembernames class * {
    @karov.* <fields>;
}

-keepclasseswithmembernames class * {
    @karov.* <methods>;
}