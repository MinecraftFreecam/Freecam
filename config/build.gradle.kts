import io.github.z4kn4fein.semver.Version

plugins {
    `java-library`
}

// Minecraft 1.17 is compatible with Java 16, so target that version
val jvmVersion = 16

val gsonVersions = mapOf(
    "26.2" to "2.14.0",
    "1.21.11" to "2.13.2",
    "1.21.4" to "2.11.0",
    "1.20.2" to "2.10.1",
    "1.19.3" to "2.10",
    "1.18.2" to "2.8.9",
    "1.18" to "2.8.8",
    "1.12" to "2.8.0",
    "legacy" to "2.2.4",
)

val (oldestGson, newestGson) = let {
    val sorted = gsonVersions.values.sortedBy {
        Version.parse(it, strict = false)
    }
    sorted.first() to sorted.last()
}

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
    implementation("com.google.code.gson:gson") {
        version {
            prefer(newestGson)
            strictly("[$oldestGson,$newestGson]")
        }
    }
}
