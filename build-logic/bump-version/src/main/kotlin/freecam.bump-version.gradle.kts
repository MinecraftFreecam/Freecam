import net.xolt.freecam.gradle.BumpVersionTask

tasks.register<BumpVersionTask>("bumpVersion") {
    group = "version"
    description = "Bump the project version"
}