module com.example.miniproyecto2 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.miniproyecto2 to javafx.fxml;
    exports com.example.miniproyecto2;
}