# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-keep,allowobfuscation,allowshrinking interface com.ls.**.api.*ApiService

# Gson 数据类
-keep class com.ls.librarybase.bean.** { *; }
-keep class com.ls.network.bean.** { *; }
-keep class com.ls.home.bean.** { *; }
-keep class com.ls.news.bean.** { *; }
-keep class com.ls.products.bean.** { *; }
-keep class com.ls.user.bean.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
# PendingSyncManager 的待同步状态被 Gson 序列化到 SharedPreferences，
# 若被混淆会在 debug/release 之间或升级后导致字段名对不上、离线操作数据丢失
-keep class com.ls.librarybase.utils.PendingSyncManager$PendingState { *; }

# Enum
-keepclassmembers enum * { *; }

# R 文件
-keep class **.R$* { *; }

# 原生 WebView JS 接口（如需使用）
# -keepclassmembers class fqcn.of.javascript.interface.for.webview { *; }
