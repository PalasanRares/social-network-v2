module com.example.labsocialnetworkv2 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.example.labsocialnetworkv2 to javafx.fxml;
    opens com.example.labsocialnetworkv2.controller to javafx.fxml;
    exports com.example.labsocialnetworkv2;

    opens com.example.labsocialnetworkv2.domain to javafx.base;
    exports com.example.labsocialnetworkv2.controller;
}