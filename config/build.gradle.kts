plugins {
    `java-library`
}

// Minecraft 1.17 is compatible with Java 16, so target that version
val jvmVersion = 16

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(jvmVersion))
    JavaVersion.toVersion(jvmVersion).let {
        sourceCompatibility = it
        targetCompatibility = it
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // TODO: version catalog
    compileOnly("org.jetbrains:annotations:26.1.0")
    implementation("org.slf4j:slf4j-api") {
        version {
            prefer("2.0.17")
            strictly("[2.0.0,2.1.0)")
        }
    }
}
