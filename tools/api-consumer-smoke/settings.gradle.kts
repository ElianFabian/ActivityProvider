pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ActivityProviderApiConsumer"
includeBuild("../..") {
    dependencySubstitution {
        substitute(module("com.github.ElianFabian:ActivityProvider")).using(project(":activity-provider"))
    }
}
