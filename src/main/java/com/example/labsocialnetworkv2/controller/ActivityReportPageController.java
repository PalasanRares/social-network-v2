package com.example.labsocialnetworkv2.controller;

import com.example.labsocialnetworkv2.application.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Date;

public class ActivityReportPageController {
    private Service service;
    private Stage stage;

    @FXML
    TextField fileNameTextField;
    @FXML
    DatePicker startDate;
    @FXML
    DatePicker endDate;

    public void setService(Service service) {
        this.service = service;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void handleSaveToPDF() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);
        service.saveActivityReportToPDF(selectedDirectory.getAbsolutePath(), fileNameTextField.getText() + ".pdf", startDate.getValue(), endDate.getValue());
    }
}
