plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

dependencies {
    implementation(libs.plugins.stonecutter.coords)
    implementation(libs.plugins.fabric.loom.coords)
    implementation(libs.plugins.fabric.loom.remap.coords)
}

gradlePlugin {
    plugins {
        create("loom-adapter") {
            id = "freecam.loom-adapter"
            implementationClass = "net.xolt.freecam.gradle.LoomAdapterPlugin"
        }
    }
}
