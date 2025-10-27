import net.minecrell.pluginyml.paper.PaperPluginDescription
plugins {
	id("java")
	id("de.eldoria.plugin-yml.paper") version "0.7.1"
    id("com.gradleup.shadow") version "9.0.0-rc1"
}

group = "xyz.emirdev.echogen"
version = "1.2.4"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
    maven { url = uri("https://repo.skriptlang.org/releases") }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.SkriptLang:Skript:2.12.2")
    compileOnly("org.projectlombok:lombok:1.18.40")
    annotationProcessor("org.projectlombok:lombok:1.18.40")
    paperLibrary("fr.mrmicky:fastboard:2.1.5")
    paperLibrary("org.spongepowered:configurate-yaml:4.1.2")
    paperLibrary("com.zaxxer:HikariCP:7.0.0")
    paperLibrary("com.h2database:h2:2.3.232")
    paperLibrary("io.github.classgraph:classgraph:4.8.179")
    implementation("com.github.stefvanschie.inventoryframework:IF:0.11.3")
}

tasks {
    build {
        dependsOn("shadowJar")
    }

    shadowJar {
        relocate("com.github.stefvanschie.inventoryframework", "xyz.emirdev.echogen.inventoryframework")
        archiveClassifier = ""
    }
}

// Configuring paper-plugin.yml
paper {
    authors = listOf("EmirhanTr3")
    description = "A plugin for general features of a server."
    website = "https://github.com/EmirhanTr3/Echogen"
    main = "xyz.emirdev.echogen.Echogen"
    loader = "xyz.emirdev.echogen.load.LibraryLoader"
    apiVersion = "1.21.10"

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
        register("Skript") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}
