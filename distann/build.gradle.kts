import com.google.protobuf.gradle.*

plugins {
    id("java")
    id("application")
    id("com.google.protobuf")
    id("org.openjfx.javafxplugin")
}

tasks {
    val fatJar = register<Jar>("fatJar") {
        setProperty("zip64",true)
        dependsOn.addAll(listOf("compileJava", "processResources")) // We need this for Gradle optimization to work
        archiveClassifier.set("genset") // Naming the jar
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes(mapOf("Main-Class" to application.mainClass, "")) } // Provided we set it up in the application plugin configuration
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) } +
                sourcesMain.output
        from(contents)
    }
    build {
        dependsOn(fatJar) // Trigger fat jar creation during build
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.21.9"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.50.2"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
            }
        }
    }
}

javafx {
    version = "19"
    modules("javafx.base", "javafx.graphics", "javafx.controls", "javafx.fxml", "javafx.swing", "javafx.web")
}

dependencies {
    implementation(project(":javafasta"))
    implementation(project(":serifier"))
    implementation(project(":spilling"))
    implementation(project(":TreeDraw"))

    runtimeOnly("io.grpc:grpc-netty:1.50.2")
    implementation("com.google.protobuf:protobuf-java:3.21.9")
    implementation("io.grpc:grpc-stub:1.50.2")
    implementation("io.grpc:grpc-protobuf:1.50.2")

    implementation("org.java-websocket:Java-WebSocket:1.5.2")

    implementation(group = "org.apache.poi", name = "poi", version = "5.2.3")
    implementation(group = "org.apache.poi", name = "poi-ooxml", version = "5.2.3")
    implementation(group = "commons-codec", name = "commons-codec", version = "1.11")
    implementation(group = "org.apache.commons", name = "commons-compress", version = "1.14")
    implementation(group = "org.apache.commons", name = "commons-vfs2", version = "2.2")
    //implementation group = "org.gorpipe", name = "gor-spark", version = "0.5.6"

    // https://mvnrepository.com/artifact/org.openjfx/javafx
    /*implementation(group = "org.openjfx", name = "javafx", version = "17.0.1")

    implementation("org.openjfx:javafx-base:17.0.1:mac")
    implementation("org.openjfx:javafx-controls:17.0.1:mac")
    implementation("org.openjfx:javafx-graphics:17.0.1:mac")
    implementation("org.openjfx:javafx-fxml:17.0.1:mac")
    implementation("org.openjfx:javafx-web:17.0.1:mac")
    implementation("org.openjfx:javafx-swing:17.0.1:mac")*/
    implementation("org.apache.logging.log4j:log4j-core:2.19.0")
    //implementation("graphframes:graphframes:0.8.2-spark3.2-s_2.12")

    implementation("com.fasterxml.jackson:jackson-bom:2.14.0-rc3")

    //implementation("com.github.fommil.netlib:all:1.1.2")

    implementation (group = "org.apache.spark", name = "spark-core_2.13", version = "3.3.1") {
        exclude(group = "avro-mapred")
        exclude(group = "com.fasterxml.jackson")
    }
    implementation (group = "org.apache.spark", name = "spark-mllib_2.13", version = "3.3.1") {
        exclude(group = "avro-mapred")
        exclude(group = "com.fasterxml.jackson")
    }
    implementation (group = "org.apache.spark", name = "spark-kubernetes_2.13", version = "3.3.1") {
        exclude(group = "avro-mapred")
        exclude(group = "com.fasterxml.jackson")
    }
    implementation (group = "org.apache.spark", name = "spark-graphx_2.13", version = "3.3.1") {
        exclude(group = "avro-mapred")
        exclude(group = "com.fasterxml.jackson")
    }

}

application {
    mainClass.set("org.simmi.distann.DistAnn")
    applicationDefaultJvmArgs = listOf("--add-opens=java.base/java.io=ALL-UNNAMED",
            "--add-opens=java.base/java.nio=ALL-UNNAMED",
            "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
            "--add-opens=java.base/sun.security.action=ALL-UNNAMED")
}