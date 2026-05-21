# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# 1. Protect Room Database & Serialization
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>(...);
}
-keep class * implements java.io.Serializable { *; }
-keep @androidx.room.Entity class * { *; }
-keep class * extends androidx.room.RoomDatabase

# 2. Protect your data models from being renamed
# This ensures Room can cleanly map database columns to your Kotlin objects
-keep class com.asdevs.expensetracker.data.model.** { *; }

# 3. Protect Hilt ViewModels safely using valid syntax
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }

# 4. Protect DataStore / Preferences fields
-keep class androidx.datastore.preferences.core.Preferences$Key { *; }