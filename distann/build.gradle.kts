plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin")
}

javafx {
    version = "18.0.1"
    modules("javafx.base", "javafx.graphics", "javafx.controls", "javafx.fxml", "javafx.swing", "javafx.web")
}

dependencies {
    implementation(project(":javafasta"))
    implementation(project(":serifier"))
    implementation(project(":spilling"))
    implementation(project(":TreeDraw"))

    implementation("org.java-websocket:Java-WebSocket:1.5.2")

    implementation(group = "org.apache.poi", name = "poi", version = "3.17")
    implementation(group = "org.apache.poi", name = "poi-ooxml", version = "3.17")
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

    implementation (group = "org.apache.spark", name = "spark-core_2.13", version = "3.2.1") {
        exclude(group = "avro-mapred")
    }
    implementation (group = "org.apache.spark", name = "spark-mllib_2.13", version = "3.2.1") {
        exclude(group = "avro-mapred")
    }
    implementation (group = "org.apache.spark", name = "spark-kubernetes_2.13", version = "3.2.1") {
        exclude(group = "avro-mapred")
    }

}

application {
    mainClassName = "org.simmi.distann.DistAnn"
}