plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "TavallCouriers"

val tavallToolsVersion = "1.0.0"

gradle.beforeProject { project ->
    project.pluginManager.withPlugin("java") {
        project.dependencies.add("implementation", "org.tavall:tavall-di:$tavallToolsVersion")

        if (project.path == ":internal-courier-api" || project.path == ":spring-webview") {
            project.dependencies.add("implementation", "org.tavall:tavall-database-postgres:$tavallToolsVersion")
        }
        if (project.path == ":spring-webview") {
            project.dependencies.add("implementation", "org.tavall:tavall-logging:$tavallToolsVersion")
            project.dependencies.add("implementation", "org.tavall:tavall-concurrency:$tavallToolsVersion")
        }
    }
}

include("gemini-api", "internal-courier-api", "spring-webview")
