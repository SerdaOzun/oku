-keepattributes Deprecated,*Annotation*,InnerClasses
-keepdirectories  #FUCK FLYWAY SO MUCH. THIS PIECE OF SHIT LINE

-keep class org.sqlite.** { *; }
-keep class kotlinx.coroutines.** { *; }
-keep class kotlin.Metadata { *; }
-keep class kotlin.text.RegexOption { *; }
-keep class org.slf4j.** { *; }
-keep class org.openkoreantext.** { *; }
-keep class ch.** { *; }
-keep class io.netty.** {*; }
-keep class kotlin.reflect.jvm.internal.** { *; }
-keep class org.flywaydb.** { *; }
-keep class com.fasterxml.jackson.** { *; }
-keep class org.jetbrains.exposed.** { *; }
-keep class com.google.gson.** { *; }
-keep class db.** { *; } # Preserve Flyway Migration file names
-keep class com.okuread.util.LogDirectoryPropertyDefiner{ *; } # Preserve name for logback config

# Keep Scala standard library
-keep class scala.** { *; }
-keep class org.scala.** { *; }
-keep class scala.collection.Seq { *; }

# A resource is loaded with a relative path so the package of this class must be preserved.
-keeppackagenames okhttp3.internal.publicsuffix.*
-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz

# OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

-dontwarn org.objectweb.asm.**
-dontwarn org.codehaus.**
-dontwarn kotlinx.coroutines.slf4j.**
-dontwarn org.flywaydb.**
-dontwarn org.jetbrains.exposed.**
-dontwarn jakarta.**
-dontwarn javax.annotation.** # JSR 305 annotations are for embedding nullability information.
-dontwarn androidx.compose.**
