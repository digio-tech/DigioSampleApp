pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()

    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven {
            url = uri("https://pkgs.dev.azure.com/eMudhraTrustService/b81ae285-cb60-4000-8c39-394db16f2a75/_packaging/eMudhraEsignAAR/maven/v1")
        }
    }
}

rootProject.name = "DigioSample"
include(":app")
