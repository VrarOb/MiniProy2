module com.example.miniproyecto2 {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.desktop;

    opens com.example.miniproyecto2 to javafx.fxml;
    exports com.example.miniproyecto2;

}
