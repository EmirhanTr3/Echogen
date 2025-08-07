import net.minecrell.pluginyml.paper.PaperPluginDescription
plugins {
	id("java")
	id("de.eldoria.plugin-yml.paper") version "0.7.1"
    id("com.gradleup.shadow") version "9.0.0-rc1"
}

// TODO: Change the information in here to the information you need.
// The name is located in settings.gradle.kts.
group = "xyz.emirdev.echogen"
version = "1.1.0"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
}

// When specifying dependencies, make sure to follow these rules:
// - If you want to shade the dependency you want to add, use implementation().
// - If you want to load the dependency during runtime, use paperLibrary().
// - If you want to include a plugin API, use compileOnly().
dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.6-R0.1-SNAPSHOT")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("me.clip:placeholderapi:2.11.6")
    paperLibrary("fr.mrmicky:fastboard:2.1.5")
    paperLibrary("org.spongepowered:configurate-yaml:4.1.2")
}

tasks.build {
    dependsOn("shadowJar")
}

// Configuring paper-plugin.yml
paper {
    authors = listOf("EmirhanTr3")
    description = "A plugin for general features of a server."
    website = "https://github.com/EmirhanTr3/Echogen"
    main = "xyz.emirdev.echogen.Echogen"
    loader = "xyz.emirdev.echogen.load.LibraryLoader"
    apiVersion = "1.21.6"

    // Keep this on!
    generateLibrariesJson = true
    
    serverDependencies {
        register("LuckPerms") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("PlaceholderAPI") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        // Specify other dependencies here...
    }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}
