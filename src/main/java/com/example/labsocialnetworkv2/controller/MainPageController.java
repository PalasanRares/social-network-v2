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
        columnId.setPrefWidth(100);

        TableColumn<User, String> columnFirstName = new TableColumn<>("First Name");
        columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        columnFirstName.setPrefWidth(100);

        TableColumn<User, String> columnLastName = new TableColumn<>("Last Name");
        columnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        columnLastName.setPrefWidth(100);

        TableColumn<User, String> columnBirthday = new TableColumn<>("Birthday");
        columnBirthday.setCellValueFactory(new PropertyValueFactory<>("birthday"));
        columnBirthday.setPrefWidth(100);

        table.getColumns().add(columnId);
        table.getColumns().add(columnFirstName);
        table.getColumns().add(columnLastName);
        table.getColumns().add(columnBirthday);
        addButtonToTable(table);
        return table;
    }
  
    private void addButtonToTable(TableView<User> tableView) {
        TableColumn<User, Void> colBtn = new TableColumn<>("");

        Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory = new Callback<TableColumn<User, Void>, TableCell<User, Void>>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                return new TableCell<User, Void>() {

                    private final Button btn = new Button("Message");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            User data = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData: " + data);
                            ///aici pagina noua
                            try {
                                FXMLLoader fxmlLoader = new FXMLLoader();
                                fxmlLoader.setLocation(getClass().getClassLoader().getResource("com/example/labsocialnetworkv2/message-page.fxml"));
                                AnchorPane msgPageLayout = fxmlLoader.load();
                                Stage stage = new Stage();
                                stage.setTitle("Message");
                                stage.setScene(new Scene(msgPageLayout));
                                stage.show();

                                MessagePageController msgPageController = fxmlLoader.getController();
                                msgPageController.setService(service,data);

                                ((Node) (event.getSource())).getScene().getWindow().hide();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };

        colBtn.setCellFactory(cellFactory);
        colBtn.setPrefWidth(100);

        tableView.setPrefWidth(500);
        tableView.setPrefHeight(200);

        tableView.getColumns().add(colBtn);

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
    protected void handleRequestButton() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getClassLoader().getResource("com/example/labsocialnetworkv2/friend-requests-page.fxml"));
            AnchorPane friendRequestsPageLayout = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Friend Requests");
            stage.setScene(new Scene(friendRequestsPageLayout));
            stage.show();

            FriendRequestsPageController searchUserController = fxmlLoader.getController();
            searchUserController.init(service);
        } catch (IOException ex) {
            ex.printStackTrace();
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
