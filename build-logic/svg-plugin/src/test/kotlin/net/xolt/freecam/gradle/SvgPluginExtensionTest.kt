package net.xolt.freecam.gradle

import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.newInstance
import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.BeforeTest
import kotlin.test.Test

class SvgPluginExtensionTest {

    private lateinit var project: Project
    private lateinit var extension: SvgPluginExtension

    @BeforeTest
    fun setup() {
        project = ProjectBuilder.builder().build()
        extension = project.objects.newInstance<SvgPluginExtension>(project)
    }

    @Test
    fun `exportPng creates task and consumable configuration`() {
        extension.exportPng("icon") {
            width.set(32f)
        }

        // Verify Task
        project.tasks.findByName("generateIcon").shouldNotBeNull {
            shouldBeInstanceOf<SvgToPngTask>()
            width.orNull shouldBe 32f
            height.orNull.shouldBeNull()
        }

        // Verify Configuration
        project.configurations.findByName("icon").shouldNotBeNull {
            isCanBeConsumed shouldBe true
            isCanBeResolved shouldBe false

            artifacts.size shouldBe 1
        }
    }

    @Test
    fun `exportPngMatrix creates size tasks, aggregate task, and configurations`() {
        extension.exportPngMatrix("logo", 16, 32) {
            // no-op
        }

        val task16 = project.tasks.findByName("generateLogo16").shouldNotBeNull()
        val task32 = project.tasks.findByName("generateLogo32").shouldNotBeNull()

        project.tasks.findByName("generateLogos").shouldNotBeNull {
            withClue("should depend on each per-size task") {
                dependsOn
                    .flatMap { it as? Iterable<*> ?: listOf(it) }
                    .map { (it as? Provider<*>)?.orNull }
                    .shouldContainExactlyInAnyOrder(task16, task32)
            }
        }

        project.configurations.findByName("logos").shouldNotBeNull {
            withClue("an artifact for each size") { artifacts.size shouldBe 2 }
        }
        project.configurations.findByName("logo_16").shouldNotBeNull {
            artifacts.size shouldBe 1
        }
        project.configurations.findByName("logo_32").shouldNotBeNull {
            artifacts.size shouldBe 1
        }
    }
}
