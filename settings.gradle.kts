plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "TavallCouriers"
include("gemini-api", "internal-courier-api", "spring-webview")
