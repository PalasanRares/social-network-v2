package com.example.labsocialnetworkv2.controller;

import com.example.labsocialnetworkv2.application.Service;
import com.example.labsocialnetworkv2.domain.FriendRequest;
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
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
    public void handleRemoveButton(ActionEvent actionEvent) {
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            service.removeFriendship(service.getLoggedInUser(), selectedUser);

        }
    }


    @FXML
    protected void handleRequestButton(ActionEvent actionEvent) {

        Label secondLabel = new Label("Friend Requests:");
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(secondLabel);



        TableView tableView = new TableView();
        tableView.setPlaceholder(new Label("Nu exista cereri de prietenie"));

        TableColumn<FriendRequest, String> column1 = new TableColumn<>("From");
        column1.setCellValueFactory(new PropertyValueFactory<>("user1"));
        //TableColumn<FriendRequest, String> column2 = new TableColumn<>("To");
        //column2.setCellValueFactory(new PropertyValueFactory<>("to"));
        TableColumn<FriendRequest, String> column3 = new TableColumn<>("Status");
        column3.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<FriendRequest, String> column4 = new TableColumn<>("DataTrimiterii");
        column4.setCellValueFactory(new PropertyValueFactory<>("dataTrimiterii"));

        tableView.getColumns().add(column1);
        //tableView.getColumns().add(column2);
        tableView.getColumns().add(column3);
        tableView.getColumns().add(column4);

        for(FriendRequest fr: service.getFriendRequests())
            tableView.getItems().add(fr);


        VBox vbox = new VBox(tableView);
        //Scene secondScene = new Scene(secondaryLayout, 230, 100);
        Scene secondScene = new Scene(vbox, 230, 100);

        Stage newWindow = new Stage();
        newWindow.setTitle("Second Stage");
        newWindow.setScene(secondScene);


        newWindow.show();
    }

}
