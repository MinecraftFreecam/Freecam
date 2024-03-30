plugins {
    `kotlin-dsl`
}

dependencies {
    testImplementation(kotlin("test"))
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}
