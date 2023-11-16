// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    extra.apply {
        set("room_version", "2.5.0")
    }
}

plugins {
    id("com.android.application") version "8.1.0" apply false
    id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false

    java // If the Java plugin is also applied to your project, a new task named jacocoTestReport is created.
    jacoco
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}


jacoco {
    // JaCoCo 버전
    toolVersion = "0.8.11"
//    applyTo(junitPlatformTest)

//  테스트결과 리포트를 저장할 경로 변경
//  default는 "${project.reporting.baseDir}/jacoco"
    // reportsDirectory
}

tasks.jacocoTestReport {
    reports {
        // 원하는 리포트를 켜고 끌 수 있습니다.
        html.required.set(true)
        html.setDestination(file("$buildDir/jacocoHtml"))
        xml.required.set(false)
        csv.required.set(false)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    extensions.configure(JacocoTaskExtension::class) {
        setDestinationFile(file("$buildDir/jacoco/jacoco.exec"))
        println(file("$buildDir/jacoco/jacoco.exec").absolutePath)
    }
}

tasks.test {
    useJUnitPlatform()
    shouldRunAfter(tasks.jacocoTestCoverageVerification)
    extensions.configure(JacocoTaskExtension::class) {
        setDestinationFile(file("$buildDir/jacoco/jacoco.exec"))
        println(file("$buildDir/jacoco/jacoco.exec").absolutePath)
    }
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

