import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    id("com.vanniktech.maven.publish") version "0.30.0"
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "17.0"
        podfile = project.file("../sampleIos/Podfile")
        framework {
            baseName = "sharedUi"
            isStatic = true
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            //put your multiplatform dependencies here
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)

            implementation(projects.capturablecompose)

            //kotlinx datetime
            implementation(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "io.github.suwasto.samplesharedui"
    compileSdk = 35
    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

mavenPublishing {

    coordinates(
        groupId = "io.github.suwasto",
        artifactId = "kmp-capturable-compose",
        version = "0.1.0"
    )

    pom {
        name.set("Kotlin Multiplatform Screenshot Library for Jetpack Compose")
        description.set("A lightweight Kotlin Multiplatform library for capturing Jetpack Compose composables as image bitmaps. Supports Android and iOS. Easily take screenshots of your composables for testing, previews, or sharing.")
        url.set("https://github.com/suwasto/KMP-Capturable-Compose")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("suwasto")
                name.set("Anang Suwasto")
                email.set("suwasto.anang@gmail.com")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/suwasto/KMP-Capturable-Compose.git")
            developerConnection.set("scm:git:ssh://github.com:suwasto/KMP-Capturable-Compose.git")
            url.set("https://github.com/suwasto/KMP-Capturable-Compose")
        }
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()
}