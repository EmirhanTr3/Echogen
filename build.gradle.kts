import net.minecrell.pluginyml.paper.PaperPluginDescription
plugins {
    kotlin("jvm") version "2.4.0"
    id("xyz.jpenilla.run-paper") version "3.0.1"
    id("de.eldoria.plugin-yml.paper") version "0.7.1"
    id("com.gradleup.shadow") version "9.0.0-rc1"
}

group = "cat.emir"
version = "2.2.1"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.skriptlang.org/releases")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    paperLibrary("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compileOnly("net.luckperms:api:5.5")
    compileOnly("me.clip:placeholderapi:2.12.2")
    compileOnly("com.github.SkriptLang:Skript:2.12.2")
    paperLibrary("fr.mrmicky:fastboard:2.2.0")
    implementation("com.github.stefvanschie.inventoryframework:IF:0.12.0")

    // for echolib
    implementation("cat.emir:EchoLib:1.0.8")
    paperLibrary("org.spongepowered:configurate-yaml:4.1.2")
    paperLibrary("io.github.classgraph:classgraph:4.8.179")
    paperLibrary("com.h2database:h2:2.3.232")
    paperLibrary("com.zaxxer:HikariCP:7.0.2")
}

tasks {
    runServer {
        minecraftVersion("1.21.11")
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

        // remove all versions until 1.21.10 since we only support 1.21.10-26.1.2
        setOf(
            "1_16_5",
            "1_17_1",
            "1_18_2",
            "1_19_4",
            "1_20_0", "1_20_1", "1_20_2", "1_20_3", "1_20_3-4", "1_20_5", "1_20_6",
            "1_21_0", "1_21_1", "1_21_2_3", "1_21_2-3", "1_21_4", "1_21_5", "1_21_6_8", "1_21_6-8"
        ).forEach {
            exclude("com/github/stefvanschie/inventoryframework/nms/v$it/**")
            exclude("META-INF/maven/com.github.stefvanschie.inventoryframework/$it/**")
        }

        // removing all IF classes that we don't need
        exclude {
            (it.path.startsWith("com/github/stefvanschie/inventoryframework/")
                    && (it.name.endsWith("Gui.class") || it.name.endsWith("Pane.class"))
                    && it.name != "ChestGui.class"
                    && it.name != "Gui.class"
                    && it.name != "NamedGui.class"
                    && it.name != "MergedGui.class"
                    && it.name != "Pane.class"
                    && it.name != "PaginatedPane.class"
                    && it.name != "StaticPane.class"
                    && it.name != "OutlinePane.class"
                    && it.name != "PositionedPane.class"
            ) ||
            (it.path.startsWith("com/github/stefvanschie/inventoryframework/pane/component")
                    && it.name != "PagingButtons.class"
            )
        }

        exclude("com/github/stefvanschie/inventoryframework/nms/v*/*InventoryImpl*.class")
        exclude("fonts/**")
        exclude("com/github/stefvanschie/inventoryframework/font/**")
        exclude("com/github/stefvanschie/inventoryframework/abstraction/**")
        exclude("META-INF/**")

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}

// Configuring paper-plugin.yml
paper {
    authors = listOf("EmirhanTr3")
    description = "A plugin for general features of a server."
    website = "https://github.com/EmirhanTr3/Echogen"
    main = "cat.emir.echogen.Echogen"
    loader = "cat.emir.echogen.load.LibraryLoader"
    apiVersion = "1.21.11"

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
