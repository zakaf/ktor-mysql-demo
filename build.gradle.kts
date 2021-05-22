plugins {
    kotlin("jvm") version "1.5.0" apply false
}

allprojects {
    group = "io.zakaf"
    version = "1.0-SNAPSHOT"
}

subprojects {
    tasks.withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}
