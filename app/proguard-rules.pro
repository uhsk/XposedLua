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
-obfuscationdictionary          proguard-dic-6.txt
-renamesourcefileattribute      proguard-dic-6.txt
-classobfuscationdictionary     proguard-dic-6.txt
-packageobfuscationdictionary   proguard-dic-6.txt
-repackageclasses               java.io
-renamesourcefileattribute      "lua"
-dontwarn **

-keepattributes Signature
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses

##########################################################################################################
-keepclassmembers,allowobfuscation class * { @com.google.gson.annotations.SerializedName <fields>; }

##########################################################################################################
-keep class androidx.fragment.app.FragmentTransaction{ *; }
-keep class androidx.fragment.app.FragmentTransaction$Op{ *; }

##########################################################################################################
-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep public class com.alibaba.android.arouter.facade.**{*;}

##########################################################################################################
-keep public class org.luaj.**{*;}
-keep public class org.luaj.vm2.lib.Bit32Lib$*{*;}

##########################################################################################################
-keep class * implements de.robv.android.xposed.IXposedHookLoadPackage
