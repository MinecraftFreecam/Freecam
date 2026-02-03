plugins {
    `kotlin-dsl` apply false
}

val check by tasks.registering {
    aggregateByName()
}

val test by tasks.registering {
    aggregateByName()
}

fun Task.aggregateByName() {
    dependsOn(provider {
        subprojects.mapNotNull { it.tasks.findByName(name) }
    })
}
