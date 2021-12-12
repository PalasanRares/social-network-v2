package com.example.labsocialnetworkv2.controller;

import com.example.labsocialnetworkv2.application.Service;
import com.example.labsocialnetworkv2.domain.Tuple;
import com.example.labsocialnetworkv2.domain.User;
import com.example.labsocialnetworkv2.utils.events.RemoveUserEvent;
import com.example.labsocialnetworkv2.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MainPageController implements Observer<RemoveUserEvent> {
    private Service service;

    ObservableList<User> model = FXCollections.observableArrayList();

    @FXML
    TableView<User> tableView;
    @FXML
    TableColumn<User, String> tableColumnUserId;
    @FXML
    TableColumn<User, String> tableColumnFirstName;
    @FXML
    TableColumn<User, String> tableColumnLastName;
    @FXML
    TableColumn<User, String> tableColumnBirthday;

    public void setService(Service service) {
        this.service = service;
        service.addObserver(this);
        initModel();
    }

    @FXML
    public void initialize() {
        tableColumnUserId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableColumnBirthday.setCellValueFactory(new PropertyValueFactory<>("birthday"));
        tableView.setItems(model);
    }

    private void initModel() {
        Iterable<User> users = service.findLoggedUsersFriends();
        List<User> userList = StreamSupport.stream(users.spliterator(), false).collect(Collectors.toList());
        model.setAll(userList);
    }

    @Override
    public void update(RemoveUserEvent event) {
        initModel();
    }

    @FXML
    public void handleRemoveButton() {
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            service.removeFriendship(service.getLoggedInUser(), selectedUser);

        }
    }

    @FXML
    public void handleAddFriendButton() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getClassLoader().getResource("com/example/labsocialnetworkv2/search-user-view.fxml"));
            AnchorPane mainPageLayout = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Search User");
            stage.setScene(new Scene(mainPageLayout));
            stage.show();

            SearchUserController searchUserController = fxmlLoader.getController();
            searchUserController.setService(service);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
