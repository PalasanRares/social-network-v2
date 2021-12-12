package com.example.labsocialnetworkv2.controller;

import com.example.labsocialnetworkv2.MainPage;
import com.example.labsocialnetworkv2.application.Service;
import com.example.labsocialnetworkv2.domain.User;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class LoginPageController {
    private Service service;

    ObservableList<User> model = FXCollections.observableArrayList();

    @FXML
    Button buttonLogin;
    @FXML
    TextField textFieldUserId;
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
        Iterable<User> users = service.findAllUsers();
        List<User> userList = StreamSupport.stream(users.spliterator(), false).collect(Collectors.toList());
        model.setAll(userList);
    }

    public void handleLoginButton(ActionEvent event) {
        Integer value1 = Integer.valueOf(textFieldUserId.getText());
        Boolean userExist = service.loginUser(value1);
        if(!userExist) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Login Error");
            alert.setContentText("The UserId you tried to logged in with does not exist!");
            alert.showAndWait();}
        else {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getClassLoader().getResource("com/example/labsocialnetworkv2/main-page.fxml"));
                AnchorPane mainPageLayout = fxmlLoader.load();
                Stage stage = new Stage();
                stage.setTitle("Main");
                stage.setScene(new Scene(mainPageLayout));
                stage.show();

                MainPageController mainPageController = fxmlLoader.getController();
                mainPageController.setService(service);
                ((Node)(event.getSource())).getScene().getWindow().hide();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
