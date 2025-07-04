import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.conveyor)
}

repositories {
    google {
        mavenContent {
            includeGroupAndSubgroups("androidx")
            includeGroupAndSubgroups("com.android")
            includeGroupAndSubgroups("com.google")
        }
    }
    mavenCentral()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material)
    implementation(compose.ui)
    implementation(compose.components.resources)
    implementation(compose.components.uiToolingPreview)
    api(compose.foundation)
    api(compose.animation)
    implementation(libs.bundles.exposed)
    implementation(libs.bundles.precompose)
    implementation(libs.sqlite.jdbc)
    implementation(libs.flyway.core)
//    implementation(libs.kotori)
    implementation(libs.opeanKoreanText)
    implementation(libs.jiebaAnalysis)
    implementation(libs.kotlinx.serialization.json)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.logback)
    implementation(libs.okhttp)
    implementation(libs.gson)
    implementation(libs.carbon.compose)
    implementation(libs.config4k)
    implementation(libs.koalaplot)

    testImplementation(libs.testng)
    testImplementation(libs.kotest)

    //for hydraulic conveyor
    linuxAmd64(compose.desktop.linux_x64)
    macAarch64(compose.desktop.macos_arm64)
    macAmd64(compose.desktop.macos_x64)
    windowsAmd64(compose.desktop.windows_x64)
}

group = "com.oku"
version = "1.4.1"

kotlin {
    jvmToolchain(18)
}

tasks.test {
    useTestNG {
        threadCount = 1
        preserveOrder = true
    }
}

compose.desktop {
    application {
        mainClass = "com.okuread.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)

            packageName = "oku"
            packageVersion = "1.4.1"
            copyright = "© 2024 Serda Özün. All rights reserved."
            description = "Oku"
            windows {
                shortcut = true
                iconFile.set(project.file("src/main/resources/images/appIcon/oku_logo.ico"))
                upgradeUuid = "776a3dba-b99f-4710-b791-79579afb6249"
            }
            macOS {
                iconFile.set(project.file("src/main/resources/images/appIcon/oku_logo.icns"))
            }

            //proguard
            buildTypes.release {
                proguard {
                    modules("java.instrument", "java.sql", "jdk.unsupported", "java.naming")
                    configurationFiles.from("proguard-rules.pro")
                    obfuscate.set(false)
                    optimize.set(true)
                }
            }
        }
    }
}

//hydraulic conveyor workaround
configurations.all {
    attributes {
        attribute(Attribute.of("ui", String::class.java), "awt")
    }
}