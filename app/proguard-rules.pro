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
-keep class com.google.mediapipe.** { *; }
-keep class com.google.mediapipe.tasks.vision.** { *; }
# Keep CalculatorProfileProto
-keep class com.google.mediapipe.proto.CalculatorProfileProto$CalculatorProfile.* { *; }

# Keep GraphTemplateProto
-keep public class com.google.mediapipe.framework.Graph { *; }
-keep public class com.google.common.** { *; }
-keep public interface com.google.common.** { *; }
-keep class com.google.mediapipe.proto.GraphTemplateProto$CalculatorGraphTemplate.*{ *; }
-keep class com.google.mediapipe.proto.** { *; }
-keep public class com.google.mediapipe.** { *; }
-dontwarn com.google.mediapipe.proto.CalculatorProfileProto$CalculatorProfile
-dontwarn com.google.mediapipe.proto.GraphTemplateProto$CalculatorGraphTemplate

