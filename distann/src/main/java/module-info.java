module distann.main {
    requires javafx.base;
    requires javafx.swing;
    requires javafx.controls;
    requires javafx.web;
    requires javafasta.main;
    requires java.jnlp;
    requires TreeDraw;
    requires serifier.main;
    requires commons.compress;
    requires jdk.jsobject;
    requires spilling.main;

    exports org.simmi.distann;
    opens org.simmi.distann;
}
