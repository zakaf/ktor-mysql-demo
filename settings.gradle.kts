rootProject.name = "ktor-mysql-demo"

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    rulesMode.set(RulesMode.FAIL_ON_PROJECT_RULES)
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        mavenCentral()
    }

    versionCatalogs {
        create("cores") {
            version("ktor", "1.5.4")
            version("kotlin", "1.5.0")
            version("logback", "1.2.1")

            alias("ktor-netty").to("io.ktor", "ktor-server-netty").versionRef("ktor")
            alias("ktor-core").to("io.ktor", "ktor-server-core").versionRef("ktor")
            alias("ktor-host").to("io.ktor", "ktor-server-host-common").versionRef("ktor")
            alias("ktor-jackson").to("io.ktor", "ktor-jackson").versionRef("ktor")
            alias("ktor-metrics").to("io.ktor", "ktor-metrics").versionRef("ktor")
            alias("ktor-micrometer").to("io.ktor", "ktor-metrics-micrometer").versionRef("ktor")

            alias("logback-classic").to("ch.qos.logback", "logback-classic").version("1.2.1")

            alias("micrometer-prometheus").to("io.micrometer", "micrometer-registry-prometheus").version("1.6.5")

            alias("coroutine-core").to("org.jetbrains.kotlinx", "kotlinx-coroutines-core").versionRef("kotlin")
            alias("coroutine-jdk8").to("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8").versionRef("kotlin")

            bundle(
                "all",
                listOf(
                    "ktor-netty",
                    "ktor-core",
                    "ktor-host",
                    "ktor-jackson",
                    "ktor-metrics",
                    "ktor-micrometer",
                    "logback-classic",
                    "micrometer-prometheus",
                    "coroutine-core",
                    "coroutine-jdk8"
                )
            )
        }
        create("datastores") {
            version("exposed", "0.31.1")

            alias("exposed-core").to("org.jetbrains.exposed", "exposed-core").versionRef("exposed")
            alias("exposed-dao").to("org.jetbrains.exposed", "exposed-dao").versionRef("exposed")
            alias("exposed-jdbc").to("org.jetbrains.exposed", "exposed-jdbc").versionRef("exposed")

            alias("mysql-java").to("mysql", "mysql-connector-java").version("8.0.19")

            alias("hikari-cp").to("com.zaxxer", "HikariCP").version("3.4.2")

            bundle("exposed-all", listOf("exposed-core", "exposed-dao", "exposed-jdbc", "mysql-java", "hikari-cp"))

            alias("jasync-sql").to("com.github.jasync-sql","jasync-mysql").version("1.1.7")
        }
    }
}

include("jasync-sql")
include("domains")
include("exposed")
