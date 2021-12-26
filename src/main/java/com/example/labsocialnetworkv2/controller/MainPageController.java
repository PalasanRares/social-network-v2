package com.example.labsocialnetworkv2.controller;

import com.example.labsocialnetworkv2.application.Service;
import com.example.labsocialnetworkv2.domain.FriendRequest;
import com.example.labsocialnetworkv2.domain.Friendship;
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
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MainPageController implements Observer<RemoveUserEvent> {
    private Service service;

    @FXML
    TableView<User> tableView = createTable();
    @FXML
    Pagination friendshipTablePagination;


    public void setService(Service service) {
        this.service = service;
        service.addObserver(this);
        friendshipTablePagination.setPageFactory(this::createPage);
    }

    private TableView<User> createTable() {
        TableView<User> table = new TableView<>();

        TableColumn<User, Integer> columnId = new TableColumn<>("User Id");
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<User, String> columnFirstName = new TableColumn<>("First Name");
        columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<User, String> columnLastName = new TableColumn<>("Last Name");
        columnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<User, String> columnBirthday = new TableColumn<>("Birthday");
        columnBirthday.setCellValueFactory(new PropertyValueFactory<>("birthday"));

        table.getColumns().add(columnId);
        table.getColumns().add(columnFirstName);
        table.getColumns().add(columnLastName);
        table.getColumns().add(columnBirthday);

        return table;
    }

    @Override
    public void update(RemoveUserEvent event) {
        initModel(friendshipTablePagination.getCurrentPageIndex());
    }


    @FXML
    public void handleRemoveButton() {
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

        for (FriendRequest fr : service.getFriendRequests())
            tableView.getItems().add(fr);


        VBox vbox = new VBox(tableView);
        //Scene secondScene = new Scene(secondaryLayout, 230, 100);
        Scene secondScene = new Scene(vbox, 230, 100);

        Stage newWindow = new Stage();
        newWindow.setTitle("Second Stage");
        newWindow.setScene(secondScene);


        newWindow.show();
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
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void handleLogoutButton(ActionEvent event) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getClassLoader().getResource("com/example/labsocialnetworkv2/login-page.fxml"));
            AnchorPane mainPageLayout = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(mainPageLayout));
            stage.show();

            LoginPageController loginPageController = fxmlLoader.getController();
            loginPageController.setService(service);

            ((Node) (event.getSource())).getScene().getWindow().hide();
        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }

    @FXML
    public void handleSentFriendRequestsButton() {
        TableView<FriendRequest> tableView = new TableView<>();

        TableColumn<FriendRequest, String> toColumn = new TableColumn<>("To");
        toColumn.setCellValueFactory(new PropertyValueFactory<>("toId"));

        TableColumn<FriendRequest, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<FriendRequest, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dataTrimiterii"));

        TableColumn<FriendRequest, String> deleteRequestColumn = new TableColumn<>("Delete Request");

        Callback<TableColumn<FriendRequest, String>, TableCell<FriendRequest, String>> deleteRequestFactory = new Callback<>() {
            @Override
            public TableCell<FriendRequest, String> call(TableColumn<FriendRequest, String> param) {
                final TableCell<FriendRequest, String> cell = new TableCell<>() {
                    final Button btn = new Button("Delete Request");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                FriendRequest friendRequest = getTableView().getItems().get(getIndex());
                                service.removeFriendRequest(friendRequest);
                                ObservableList<FriendRequest> tableModel = FXCollections.observableArrayList();
                                Iterable<FriendRequest> friendRequests = service.getSentFriendRequests();
                                List<FriendRequest> friendRequestList = StreamSupport.stream(friendRequests.spliterator(), false).collect(Collectors.toList());
                                tableModel.setAll(friendRequestList);
                                tableView.setItems(tableModel);
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };

        deleteRequestColumn.setCellFactory(deleteRequestFactory);

        tableView.getColumns().addAll(toColumn, statusColumn, dateColumn, deleteRequestColumn);

        ObservableList<FriendRequest> tableModel = FXCollections.observableArrayList();
        Iterable<FriendRequest> friendRequests = service.getSentFriendRequests();
        List<FriendRequest> friendRequestList = StreamSupport.stream(friendRequests.spliterator(), false).collect(Collectors.toList());
        tableModel.setAll(friendRequestList);
        tableView.setItems(tableModel);

        VBox vbox = new VBox(tableView);
        Scene secondScene = new Scene(vbox, 400, 300);

        Stage newWindow = new Stage();
        newWindow.setTitle("Manage Friend Requests");
        newWindow.setScene(secondScene);

        newWindow.show();
    }

    void initModel(int pageIndex) {
        Iterable<User> friendshipsOnPage = service.getFriendshipsPage(pageIndex, 7);
        List<User> friendshipsOnePageList = StreamSupport.stream(friendshipsOnPage.spliterator(), false).collect(Collectors.toList());

        tableView.setItems(FXCollections.observableArrayList(friendshipsOnePageList));
    }

    private Node createPage(int pageIndex) {
        initModel(pageIndex);
        return new BorderPane(tableView);

    }

}
