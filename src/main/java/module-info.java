module org.example.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires annotations;
    requires static lombok;

    exports org.example.demo.view;
    exports org.example.demo.conroller;
    opens org.example.demo.view to javafx.fxml;
}