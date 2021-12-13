package com.example.labsocialnetworkv2.controller;

import com.example.labsocialnetworkv2.application.Service;
import com.example.labsocialnetworkv2.domain.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SearchUserController {
    private Service service;

    private ObservableList<User> model = FXCollections.observableArrayList();

    @FXML
    ListView<User> listView;

    @FXML
    TextField textFieldName;

    @FXML
    Button buttonSearch;
    @FXML
    Button buttonSendRequest;

    @FXML
    public void initialize() {
        listView.setItems(model);
    }

    @FXML
    public void handleSearchButton() {
        Iterable<User> userIterable = service.searchByName(textFieldName.getText());
        List<User> userList = StreamSupport.stream(userIterable.spliterator(), false).collect(Collectors.toList());
        model.setAll(userList);
    }

    @FXML
    public void handleSendRequestButton() {
        User selectedUser = listView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            if (!service.sendFriendRequest(selectedUser.getId())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Send Request Error");
                alert.setContentText("The request could not be sent!");
                alert.showAndWait();
            }
        }
    }

    public void setService(Service service) {
        this.service = service;
    }
}
