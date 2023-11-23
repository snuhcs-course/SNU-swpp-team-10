// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    extra.apply {
        set("room_version", "2.5.0")
    }

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.3.0")
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
    }
}

plugins {
    id("com.android.application") version "8.1.0" apply false
    id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false

    id("io.github.gmazzo.test.aggregation.results") version "2.2.0"
    id("io.github.gmazzo.test.aggregation.coverage") version "2.2.0"
}

testAggregation {
    modules {
//        include(projects.demoProject.app, projects.demoProject.domain, projects.demoProject.login)
//        exclude(rootProject)
    }
    coverage {
//        exclude("**/ContentMainBinding*")
    }
}

tasks.jacocoAggregatedReport {
    reports {
        html.required.set(true)
    }
}

tasks.jacocoAggregatedCoverageVerification {
    violationRules {
        rule {
            limit {// current 19%
                minimum = "0.19".toBigDecimal()
            }
            limit {// desired 80%
                minimum = "0.8".toBigDecimal()
                isFailOnViolation = false
            }
        }
    }
}


apply(from= rootProject.file("dependencies.gradle"))

//tasks.check {
//    dependsOn(tasks.jacocoAggregatedCoverageVerification)
//}
