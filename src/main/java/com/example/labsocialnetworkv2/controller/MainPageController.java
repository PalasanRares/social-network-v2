package com.example.labsocialnetworkv2.controller;

import com.example.labsocialnetworkv2.application.Service;
import com.example.labsocialnetworkv2.domain.*;
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
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MainPageController implements Observer<RemoveUserEvent> {
    private Service service;
    private Page pagina;
    @FXML
    TableView<User> tableView = createTable();
    @FXML
    Pagination friendshipTablePagination;

    @FXML
    Button notificationsButton;
    @FXML
    ImageView notificationsIcon;


    public void setService(Service service) {
        this.service = service;
        service.addObserver(this);
        friendshipTablePagination.setPageFactory(this::createPage);
        friendshipTablePagination.setPageCount(service.getNumberOfFriendsForLoggedInUser() / 7 + 1);
        setNotificationsButton();
        pagina=service.createPageForUser(service.getLoggedInUser().getId());
    }

    private void setNotificationsButton() {
        Long numberOfNotifications = service.getNumberOfNotifications();
        if (numberOfNotifications != 0) {
            notificationsIcon.setVisible(false);
            notificationsButton.setText(numberOfNotifications.toString());
        }
        else {
            notificationsIcon.setVisible(true);
            notificationsButton.setText("");
        }
    }

    private TableView<User> createTable() {
        TableView<User> table = new TableView<>();

        TableColumn<User, Integer> columnId = new TableColumn<>("Username");
        columnId.setCellValueFactory(new PropertyValueFactory<>("username"));
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

        Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                return new TableCell<>() {

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
                                msgPageController.setService(service, data);

                                //((Node) (event.getSource())).getScene().getWindow().hide();
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
        setNotificationsButton();
        friendshipTablePagination.setPageCount(service.getNumberOfFriendsForLoggedInUser() / 7 + 1);
    }


    @FXML
    public void handleRemoveButton() {
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            service.removeFriendship(service.getLoggedInUser(), selectedUser);
            List<FriendRequest> req = new ArrayList<>(StreamSupport.stream(service.getFriendRequests().spliterator(), false).toList());
            pagina.setCereriDePrietenie(req);
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
    public void handleEventsButton() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getClassLoader().getResource("com/example/labsocialnetworkv2/events-page.fxml"));
            AnchorPane eventsPageLayout = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(eventsPageLayout));
            stage.setTitle("Events");
            stage.show();

            EventsPageController eventsPageController = fxmlLoader.getController();
            eventsPageController.setService(service);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void handleNotificationsButton() {
        Iterable<Event> notifications = service.findNotifications();
        for (Event event : notifications) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getClassLoader().getResource("com/example/labsocialnetworkv2/pop-up-page.fxml"));
                AnchorPane popUpLayout = fxmlLoader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(popUpLayout));
                stage.setTitle("Notification");
                stage.show();

                PopUpPageController popUpPageController = fxmlLoader.getController();
                popUpPageController.setErrorText("You have this upcoming event: " + event.toString());
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    void initModel(int pageIndex) {
        Iterable<User> friendshipsOnPage = service.getFriendshipsPage(pageIndex, 7);
        List<User> friendshipsOnePageList = StreamSupport.stream(friendshipsOnPage.spliterator(), false).collect(Collectors.toList());

        tableView.setItems(FXCollections.observableArrayList(friendshipsOnePageList));
        tableView.refresh();
    }

    private Node createPage(int pageIndex) {
        initModel(pageIndex);
        return new BorderPane(tableView);

    }

    public void handleRap1Button() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getClassLoader().getResource("com/example/labsocialnetworkv2/activity-report-page.fxml"));
            AnchorPane activityReportPage = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(activityReportPage));
            stage.setTitle("Activity Report");
            stage.show();

            ActivityReportPageController activityReportPageController = fxmlLoader.getController();
            activityReportPageController.setService(service);
            activityReportPageController.setStage(stage);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void handleRap2Button(ActionEvent actionEvent) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getClassLoader().getResource("com/example/labsocialnetworkv2/raport1-page.fxml"));
            AnchorPane eventsPageLayout = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(eventsPageLayout));
            stage.setTitle("Events");
            stage.show();

            Raport1PageController messageReportPageController = fxmlLoader.getController();
            messageReportPageController.setService(service);
            messageReportPageController.setStage(stage);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
