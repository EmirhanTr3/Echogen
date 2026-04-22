import net.minecrell.pluginyml.paper.PaperPluginDescription
plugins {
    kotlin("jvm") version "2.3.20"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("de.eldoria.plugin-yml.paper") version "0.7.1"
    id("com.gradleup.shadow") version "9.0.0-rc1"
}

group = "cat.emir"
version = "2.0.2"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.skriptlang.org/releases")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")
    paperLibrary("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.SkriptLang:Skript:2.12.2")
    paperLibrary("fr.mrmicky:fastboard:2.1.5")
    paperLibrary("org.spongepowered:configurate-yaml:4.1.2")
    paperLibrary("org.spongepowered:configurate-extra-kotlin:4.1.2")
    paperLibrary("com.zaxxer:HikariCP:7.0.0")
    paperLibrary("com.h2database:h2:2.3.232")
    paperLibrary("io.github.classgraph:classgraph:4.8.179")
    implementation("com.github.stefvanschie.inventoryframework:IF:0.11.3")
}

tasks {
    runServer {
        minecraftVersion("1.21.10")
        downloadPlugins {
            modrinth("viaversion", "5.9.0")
            modrinth("viabackwards", "5.9.0")
            modrinth("luckperms", "v5.5.17-bukkit")
            modrinth("placeholderapi", "2.12.2")
            modrinth("skript", "2.13.2")
            url("https://github.com/SkriptLang/skript-reflect/releases/download/v2.6.1/skript-reflect-2.6.1.jar")
            modrinth("openinv", "5.3.0")
        }
    }

    jar.get().enabled = false

    build {
        dependsOn("shadowJar")
    }

    shadowJar {
        relocate("com.github.stefvanschie.inventoryframework", "cat.emir.echogen.inventoryframework")
        archiveClassifier = ""
    }
}

// Configuring paper-plugin.yml
paper {
    authors = listOf("EmirhanTr3")
    description = "A plugin for general features of a server."
    website = "https://github.com/EmirhanTr3/Echogen"
    main = "cat.emir.echogen.Echogen"
    loader = "cat.emir.echogen.load.LibraryLoader"
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
        register("OpenInv") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}

kotlin {
    jvmToolchain(21)
}
