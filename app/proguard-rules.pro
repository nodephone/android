# ProGuard / R8 Optimization & Keep Rules for NodePhone Android v1.0.0

# Preserve Room Database Entities and DAOs
-keep class com.nodephone.android.data.** { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase { *; }
-dontwarn androidx.room.paging.**

# Preserve Hilt / Dagger Generated Code
-keep class com.nodephone.android.di.** { *; }
-keepclassmembers class * {
    @javax.inject.Inject <init>(...);
}

# Preserve Ktor Client Models & Serialization
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# Preserve ZXing QR Code Library
-keep class com.google.zxing.** { *; }

# Preserve Coroutines StateFlow
-keep class kotlinx.coroutines.** { *; }
