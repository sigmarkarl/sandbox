plugins {
    id("java")
    id("org.openjfx.javafxplugin")
}

javafx {
    version = "19"
    modules("javafx.base", "javafx.graphics", "javafx.controls", "javafx.fxml","javafx.swing","javafx.web")
}

dependencies {
    implementation(group = "commons-net", name = "commons-net", version = "3.7")
    implementation(group = "org.apache.commons", name = "commons-vfs2", version = "2.1")
    implementation ("org.apache.spark:spark-core_2.13:3.3.1") {
        exclude(group = "avro-mapred")
        exclude(group = "com.fasterxml.jackson")
    }
    implementation ("org.apache.spark:spark-mllib_2.13:3.3.1") {
        exclude(group = "avro-mapred")
        exclude(group = "com.fasterxml.jackson")
    }
    implementation ("org.apache.spark:spark-sql_2.13:3.3.1") {
        exclude(group = "avro-mapred")
        exclude(group = "com.fasterxml.jackson")
    }
    implementation ("org.apache.spark:spark-kubernetes_2.13:3.3.1") {
        exclude(group = "avro-mapred")
        exclude(group = "com.fasterxml.jackson")
    }
    implementation(project(":javafasta"))
    implementation(project(":TreeDraw"))
}