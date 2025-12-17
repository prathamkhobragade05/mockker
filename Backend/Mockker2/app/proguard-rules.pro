# Retrofit / OkHttp / Gson
-keep class com.pratham.mockker2.** { *; }
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Keep model classes for Gson
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
