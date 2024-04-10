// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.3.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    kotlin("plugin.serialization") version "1.8.10"
    id("com.google.gms.google-services") version "4.4.1" apply false
}

buildscript {

    dependencies {
        // Otras dependencias...
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.51")

    }
}