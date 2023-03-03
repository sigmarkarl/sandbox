import java.nio.file.Files
import java.nio.file.Paths
import java.util.spi.ToolProvider

//import com.google.protobuf.gradle.*
import org.gradle.kotlin.dsl.provider.gradleKotlinDslOf

/*buildscript {
    repositories {
        maven {
            url "https://plugins.gradle.org/m2/"
        }
    }
    dependencies {
        classpath 'org.openjfx:javafx-plugin:0.0.8'
    }
}*/

buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("gradle.plugin.com.google.protobuf:protobuf-gradle-plugin:0.8.18")
    }
}

plugins {
    id("java")
    id("application")
    id("com.palantir.graal") version "0.9.0"
    id("org.openjfx.javafxplugin") version "0.0.13"
    //id("com.github.johnrengelman.shadow") version "7.1.2"
}

/*protobuf {
    protoc { artifact = "com.google.protobuf:protoc:3.21.1" }
    plugins { grpc { artifact = "io.grpc:protoc-gen-grpc-java:1.46.0" } }
    generateProtoTasks { ofSourceSet("main")*.plugins { grpc {} } }
}*/

/*ext {
    javaMainClass = "org.simmi.distann.DistAnn"
}*/

application {
    mainClass.set("org.simmi.distann.DistAnn")
    //mainClassName = "org.simmi.distann.DistAnn"
}

/*tasks.register("fat_build") {
    dependsOn.addAll(listOf("compileJava", "processResources")) // We need this for Gradle optimization to work
    archiveClassifier.set("genset") // Naming the jar
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest { attributes(mapOf("Main-Class" to application.mainClass)) } // Provided we set it up in the application plugin configuration
    val sourcesMain = sourceSets.main.get()
    val contents = configurations.runtimeClasspath.get()
        .map { if (it.isDirectory) it else zipTree(it) } +
            sourcesMain.output
    from(contents)
}*/

/*tasks {
    val fatJar = register<Jar>("fatJar") {
        dependsOn.addAll(listOf("compileJava", "processResources")) // We need this for Gradle optimization to work
        archiveClassifier.set("genset") // Naming the jar
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes(mapOf("Main-Class" to application.mainClass)) } // Provided we set it up in the application plugin configuration
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) } +
                sourcesMain.output
        from(contents)
    }
    build {
        dependsOn(fatJar) // Trigger fat jar creation during build
    }
}*/

/*allprojects {
    apply plugin: 'idea'
    apply plugin: 'eclipse'
    apply plugin: 'java'
    apply plugin: 'org.openjfx.javafxplugin'

    ext.getProjectFullName = {
        return 'distann' + project.path.replace(':', '-')
    }
}*/

//mainClassName='org.simmi.distann.DistannFX'

/*tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("jarFileNmae")
    }
}*/

/*tasks {
    jar {
        manifest {
            attributes(mapOf(
                    "Main-Class" to "org.simmi.distann.DistannFX"
            ))
        }
    }
    shadowJar {
        manifest.inheritFrom(jar.get().manifest) //will make your shadowJar (produced by jar task) runnable
    }
}*/

/*jar {
    manifest {
        attributes["Main-Class"] = "org.simmi.distann.DistannFX"
    }

    //exclude("META-INF/.RSA", "META-INF/.SF','META-INF/.DSA")
}*/

fun resolveJpackage(): ToolProvider {
    return ToolProvider.findFirst("jpackage").orElseThrow {
        val javaVersion = System.getProperty("java.version")
        IllegalStateException("jpackage not found (expected JDK version: 14 or above, detected: $javaVersion)")
    }
}

fun runJpackage(ENDING: String, VERSION: String): String {
    println("Creating GeneSet package installer")
    val jpackage = resolveJpackage()
    val exitval: Int = jpackage.run(System.out, System.err, "--type", ENDING, "-i", "$rootDir/distann/build/install/distann", "-n", "genset", "--app-version", VERSION, "--dest", "$rootDir",
        "--main-jar",
        "lib/distann.jar",
        "--main-class",
        "org.simmi.distann.DistAnn",
        "--java-options",
        "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
        "--java-options",
        "--add-opens=java.base/java.nio=ALL-UNNAMED",
        "--verbose",
        "--file-associations",
        "file-association.txt",
        "--file-associations",
        "file-association2.txt")
    //"--mac-package-name", "Genset",
    System.err.println(exitval)
    return ""
}

tasks.register("genset_build") {
    val VERSION = Files.readString(Paths.get("$rootDir").resolve("VERSION")).trim()
    val ENDING: String = project.findProperty("ENDING").toString() ?: "deb"
    runJpackage(ENDING, VERSION)
}

subprojects {
    repositories {
        //jcenter()
        mavenCentral()
        gradlePluginPortal()
        /*maven {
            url("https://plugins.gradle.org/m2/")
        }*/
    }

		/*task fatJar(type: Jar) {
        baseName = getProjectFullName() + '-all'
        destinationDir = file("${buildDir}/distributions")
				manifest {
		        attributes 'Main-Class': 'org.simmi.distann.DistannFX'
		    }
        from { configurations.runtime.collect { it.isDirectory() ? it : zipTree(it) } } {

        }
        with jar
    }*/
}