plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

val pluginName = "PlayReview"
val pluginPackageName = "com.neonexalabs.playreview"

android {
    namespace = pluginPackageName
    compileSdk = 35

    defaultConfig {
        minSdk = 24  // Godot 4.7's
        consumerProguardFiles("consumer-rules.pro")
        manifestPlaceholders["godotPluginName"] = pluginName
        manifestPlaceholders["godotPluginPackageName"] = pluginPackageName
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

base {
    archivesName.set(pluginName)
}

dependencies {
    compileOnly("org.godotengine:godot:4.7.2.stable")  // the game's build brings the engine
    implementation("com.google.android.play:review:2.0.2")  // not bundled in the AAR: export_plugin.gd hands it to the game's build
}

// `assemble` leaves the addon in ../addons/PlayReview: the export script, plugin.cfg, the licence and both AARs (the release asset is that folder).
val copyDebugAar by tasks.registering(Copy::class) {
    from("build/outputs/aar")
    include("$pluginName-debug.aar")
    into("../addons/$pluginName/bin/debug")
}

val copyReleaseAar by tasks.registering(Copy::class) {
    from("build/outputs/aar")
    include("$pluginName-release.aar")
    into("../addons/$pluginName/bin/release")
}

val cleanAddon by tasks.registering(Delete::class) {
    delete("../addons/$pluginName")
}

val copyAddon by tasks.registering(Copy::class) {
    dependsOn(cleanAddon)
    finalizedBy(copyDebugAar, copyReleaseAar)
    from("export_scripts_template")
    from("../LICENSE")
    into("../addons/$pluginName")
}

tasks.named("assemble").configure {
    finalizedBy(copyAddon)
}
