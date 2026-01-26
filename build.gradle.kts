plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
    id("fabric-loom") version "1.8-SNAPSHOT"
    id("maven-publish")
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

repositories {
    mavenCentral()
}

dependencies {
    // Minecraft and Fabric
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    
    // Fabric API
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")
    
    // Fabric Language Kotlin
    modImplementation("net.fabricmc:fabric-language-kotlin:${project.property("fabric_kotlin_version")}")
    
    // Kotlinx Serialization for JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Test dependencies
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
} 

tasks {
    val modVersion = project.version.toString()
    val minecraftVersion = project.property("minecraft_version").toString()
    val loaderVersion = project.property("loader_version").toString()
    val fabricKotlinVersion = project.property("fabric_kotlin_version").toString()
    val archivesBaseName = project.base.archivesName.get()
    
    processResources {
        inputs.property("version", modVersion)
        inputs.property("minecraft_version", minecraftVersion)
        inputs.property("loader_version", loaderVersion)
        inputs.property("fabric_kotlin_version", fabricKotlinVersion)
        
        filesMatching("fabric.mod.json") {
            expand(
                mapOf(
                    "version" to modVersion,
                    "minecraft_version" to minecraftVersion,
                    "loader_version" to loaderVersion,
                    "fabric_kotlin_version" to fabricKotlinVersion
                )
            )
        }
    }
    
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(21)
    }
    
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }
    
    jar {
        from("LICENSE") {
            rename { "${it}_${archivesBaseName}" }
        }
    }

    // Configure test task to use JUnit Platform (JUnit 5)
    test {
        useJUnitPlatform()
    }
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    jvmToolchain(21)
}
