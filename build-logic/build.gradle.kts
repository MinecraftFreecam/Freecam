plugins {
    `kotlin-dsl` apply false
}

tasks.register("check") {
    description = "Runs all checks in subprojects"
    aggregateByName()
}

tasks.register("test") {
    description = "Runs the test suite in subprojects"
    aggregateByName()
}

fun Task.aggregateByName() {
    dependsOn(provider {
        subprojects.mapNotNull { it.tasks.findByName(name) }
    })
}
