buildscript {
    dependencies {
        classpath (libs.kotlin.plugin)
    }
}

plugins {
    alias (libs.plugins.android.application) apply false
    alias (libs.plugins.devtools.ksp) apply false
}