plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.domains)
    implementation(cores.bundles.all)
    implementation(datastores.bundles.exposed.all)
}

tasks.test {
    useJUnitPlatform()
}
