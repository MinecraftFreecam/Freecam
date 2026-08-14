package net.xolt.freecam.gradle

import org.gradle.api.Project
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.support.uppercaseFirstChar
import javax.inject.Inject

abstract class SvgPluginExtension @Inject constructor(
    private val project: Project,
) {
    /**
     * Register a task and consumable configuration for converting an SVG file to PNG.
     */
    fun exportPng(name: String, action: SvgToPngTask.() -> Unit) = with(project) {
        val task = tasks.register<SvgToPngTask>("generate${name.uppercaseFirstChar()}") {
            group = "graphics"
            description = "Generates PNG for $name"
            action()
        }

        val cfg = configurations.create(name.replace('-', '_')) {
            description = "PNG for $name"
            isCanBeConsumed = true
            isCanBeResolved = false
        }

        artifacts.add(cfg.name, task.flatMap { it.destination }) {
            builtBy(task)
        }
    }

    /**
     * Register tasks and consumable configurations for converting an SVG file to a matrix of PNG sizes.
     */
    fun exportPngMatrix(name: String, vararg sizes: Int, action: SvgToPngTask.() -> Unit) = exportPngMatrix(name, sizes.toList(), action)

    /**
     * Register tasks and consumable configurations for converting an SVG file to a matrix of PNG sizes.
     */
    fun exportPngMatrix(name: String, sizes: Iterable<Int>, action: SvgToPngTask.() -> Unit) = with(project) {
        val taskName = "generate${name.uppercaseFirstChar()}"
        val cfgName = name.replace('-', '_')

        val masterCfg = configurations.create("${cfgName}s") {
            description = "All PNG sizes for $name"
            isCanBeConsumed = true
            isCanBeResolved = false
        }

        val sizeTasks = sizes.map { size ->
            val sizeTask = tasks.register<SvgToPngTask>("$taskName$size") {
                group = "graphics"
                description = "Generates ${size}px PNG for $name"
                destination.set(layout.buildDirectory.file("graphics/$name/$name-$size.png"))
                width.set(size.toFloat())
                height.set(size.toFloat())
                action()
            }

            val sizeCfg = configurations.create("${cfgName}_$size") {
                description = "${size}px PNG for $name"
                isCanBeConsumed = true
                isCanBeResolved = false
            }

            sequenceOf(masterCfg, sizeCfg).forEach { cfg ->
                artifacts.add(cfg.name, sizeTask.flatMap { it.destination }) {
                    builtBy(sizeTask)
                }
            }

            sizeTask
        }

        tasks.register("${taskName}s") {
            group = "graphics"
            description = "Generates all PNG sizes for $name"
            dependsOn(sizeTasks)
        }
    }
}
