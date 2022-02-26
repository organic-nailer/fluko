import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    application
}

group = "dev.fastriver"
version = "1.0-SNAPSHOT"
val skArtifact = "skija-windows"
val skVersion = "0.93.6"
val lwjglVersion = "3.3.1"
val lwjglNatives = "natives-windows"

repositories {
    mavenCentral()
    maven (url = "https://packages.jetbrains.team/maven/p/skija/maven")
}

dependencies {
    testImplementation(kotlin("test"))
    api("org.jetbrains.skija:${skArtifact}:${skVersion}")

    implementation("org.lwjgl:lwjgl:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-glfw:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-opengl:$lwjglVersion")
    runtimeOnly("org.lwjgl:lwjgl:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-glfw:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-opengl:$lwjglVersion:$lwjglNatives")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "9"
}

application {
    mainClass.set("MainKt")
}