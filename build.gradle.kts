import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "com.satrap"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

val exposedVersion: String by project
val sqliteVersion: String by project
val precomposeVersion: String by project
val openNLPVersion: String by project

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)

    //Database
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-json:$exposedVersion")
    implementation("org.xerial:sqlite-jdbc:$sqliteVersion")

    //Precompose
    api(compose.foundation)
    api(compose.animation)
    api("moe.tlaster:precompose:$precomposeVersion")
    api("moe.tlaster:precompose-viewmodel:$precomposeVersion")

    //OpenNLP
    implementation("org.apache.opennlp:opennlp-tools:$openNLPVersion")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Oku"
            packageVersion = "1.0.0"
        }
    }
}
