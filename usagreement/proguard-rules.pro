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
#基本不用动区域-----------------------------------------
#基本指令区---------------------------------------------
-optimizationpasses 5 # 代码混淆的压缩比例，值在0-7之间
-dontusemixedcaseclassnames # 混淆后类名都为小写
-dontskipnonpubliclibraryclasses # 指定不去忽略非公共的库的类
-dontskipnonpubliclibraryclassmembers # 指定不去忽略非公共的库的类的成员
-dontshrink
# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontoptimize
-dontpreverify #    不做预校验的操作, 表示不进行校验,这个校验作用 在java平台上的
-verbose    # 打印混淆的详细信息
-printmapping proguardMapping.txt # 生成原类名和混淆后的类名的映射文件
-optimizations !code/simplification/cast,!field/*,!class/merging/* # 指定混淆是采用的算法
-keepattributes *Annotation*,InnerClasses # #使用注解需要添加, 不混淆Annotation
-keepattributes Signature # 不混淆泛型
-keepattributes SourceFile,LineNumberTable # 抛出异常时保留代码行号
-keepattributes EnclosingMethod
-ignorewarnings


###########################以下是AndroidStudio自带的混淆配置协议###############################


# Note that if you want to enable optimization, you cannot just
# include optimization flags in your own project configuration file;
# instead you will need to point to the
# "proguard-android-optimize.txt" file instead of this one from your
# project.properties file.
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
#指定不混淆所有的JNI方法
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
#所有View的子类及其子类的get、set方法都不进行混淆
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
# 不混淆Activity中参数类型为View的所有方法
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
# 不混淆Enum类型的指定方法
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 不混淆Parcelable和它的子类，还有Creator成员变量
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

# 不混淆R类里及其所有内部static类中的所有static变量字段
-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
# 不提示兼容库的错误警告
-dontwarn android.support.**

# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep

-keep @android.support.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}


###########################以下是需要手动的混淆配置协议###############################


#-libraryjars "C:\Program Files\Java\jre1.8.0_151\lib\rt.jar"
#-libraryjars "F:\Java\jdk1.8.0_91\jre\lib\rt.jar"
#-libraryjars "G:\Downloads\360 Output\360 jiagu\protocol-1.3.jar"
#-libraryjars "G:\AndoridSDK\platforms\android-26\android.jar"
#-libraryjars "C:\Users\admin\AppData\Local\Android\sdk\platforms\android-26\android.jar"
# 注意：以上两个路径需要将以上路径是自己jar包的位置，需要根据自己情况进行修改，如果报重复配置的错误，注释掉即可

#代码迭代优化的次数，默认5
#-optimizationpasses 5
##混淆时不会产生形形色色的类名
#-dontusemixedcaseclassnames
#-optimizations !code/simplification/cast,!field/*,!class/merging/* #指定混淆是采用的算法
#-keepattributes Signature #不混淆泛型
#
##忽略警告
#-ignorewarnings
# 以下是不需要混淆的文件

# 保持项目中的第三方jar不混淆
#-libraryjars libs/protocol-1.3.jar
## 不混淆第三方jar包中的类
#-dontwarn com.speedtalk.protocol.*
#-keep class com.speedtalk.protocol.** {*;}
#-keep class com.speedtalk.logutils.*{*;}
#-keep class com.speedtalk.protocol.tscobjs.*{*;}
#-keep class com.speedtalk.protocol.TSCObject{*;}
#-keep class com.speedtalk.protocol.constants.*{*;}
#-keep class com.speedtalk.protocol.utils.*{*;}

#-dontobfuscate 不混淆输入的类文件
#-keep class com.speedtalk.audio.AudioCodingEnvironment$*{*;}
-keep class cn.com.erayton.usagreement.socket.client.SocketClient$*{*;}
-keep class cn.com.erayton.usagreement.socket.client.SocketClient{
public <fields>;
public <methods>;
}
-keep class cn.com.erayton.usagreement.socket.client.SocketClientSender{*;}
-keep class cn.com.erayton.usagreement.utils.USGate$*{*;}
-keep class cn.com.erayton.usagreement.utils.USGate{
public <fields>;
public <methods>;
}
# 2020.02.11    关闭 model
-keep class cn.com.erayton.usagreement.model.model.*{*;}
-keep class com.library.USVideo{*;}
-keep class cn.com.erayton.usagreement.data.Constants{*;}
-keep class cn.com.erayton.usagreement.service.VideoPushService{*;}
#-keep class cn.com.erayton.usagreement.model.*{*;}


#-keep class com.speedtalk.audio.AudioCodingEnvironment{
#public <fields>;
#public <methods>;
#}
#-keep class com.speedtalk.common.CreateGroupResp{*;}
#-keep class com.speedtalk.socket.tscclient.TscSocketClient$*{*;}
#-keep class com.speedtalk.socket.tscclient.TscSocketClient{
#public <fields>;
#public <methods>;
#}
#-keep class com.speedtalk.socket.tscclient.TscSocketClientSender{*;}
#-keep class com.speedtalk.audio.AudioHelper$*{*;}
#-keep class com.speedtalk.audio.AudioHelper{
#public <fields>;
#public <methods>;
#}
#-keep class com.speedtalk.common.MessReturn{*;}