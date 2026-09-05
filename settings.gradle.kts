pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://api.xposed.info") }
    }
}

plugins {
    id ("org.gradle.toolchains.foojay-resolver-convention").version("1.0.0")
}

rootProject.name = "NoVPNDetect Enhanced"
include(":app")
