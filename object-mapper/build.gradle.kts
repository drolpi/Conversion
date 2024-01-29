import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.shadow)
}

tasks.withType<ShadowJar> {
    archiveFileName.set("conversion-objectmapper.jar")
    archiveVersion.set(null as String?)

    // drop unused classes which are making the jar bigger
    minimize()
}

dependencies {
    "implementation"(projects.core)
}