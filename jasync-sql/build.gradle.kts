plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.domains)
    implementation(cores.bundles.all)
    implementation(datastores.jasync.sql)
}

tasks.test {
    useJUnitPlatform()
}
