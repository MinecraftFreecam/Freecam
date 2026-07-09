/**
 * Applying this plugin to a project will transitively pull in its classpath,
 * giving the project access to `:build-logic:shadow` classes.
 *
 * It also applies the GradleUp `shadow` plugin, for convenience.
 */

plugins {
    id("com.gradleup.shadow")
}
