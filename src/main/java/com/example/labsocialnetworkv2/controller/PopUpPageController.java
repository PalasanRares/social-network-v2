package com.example.labsocialnetworkv2.controller;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PopUpPageController {
    @FXML
    Text errorText;

    @FXML
    public void handleOkButton() {
        Stage stage = (Stage) errorText.getScene().getWindow();
        stage.close();
    }

    public void setErrorText(String text) {
        errorText.setText(text);
    }
}
