package com.example.labsocialnetworkv2.controller;

import com.example.labsocialnetworkv2.application.Service;
import com.example.labsocialnetworkv2.domain.FriendRequest;
import com.example.labsocialnetworkv2.utils.events.RemoveUserEvent;
import com.example.labsocialnetworkv2.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.util.List;
import java.util.stream.StreamSupport;

public class FriendRequestsPageController implements Observer<RemoveUserEvent> {
    private Service service;

    private ObservableList<FriendRequest> receivedRequestsModel;
    private ObservableList<FriendRequest> sentRequestsModel;

    @FXML
    TableView<FriendRequest> receivedTableView;
    @FXML
    TableColumn<FriendRequest, String> receivedUserColumn;
    @FXML
    TableColumn<FriendRequest, String> receivedDateColumn;
    @FXML
    TableColumn<FriendRequest, String> receivedAcceptColumn;
    @FXML
    TableColumn<FriendRequest, String> receivedRejectColumn;

    @FXML
    TableView<FriendRequest> sentTableView;
    @FXML
    TableColumn<FriendRequest, String> sentUserColumn;
    @FXML
    TableColumn<FriendRequest, String> sentDateColumn;
    @FXML
    TableColumn<FriendRequest, String> sentUnsendColumn;

    public void init(Service service) {
        this.service = service;
        this.receivedRequestsModel = FXCollections.observableArrayList();
        this.sentRequestsModel = FXCollections.observableArrayList();
        setColumnFactories();
        populateTables();

        receivedTableView.setItems(receivedRequestsModel);
        sentTableView.setItems(sentRequestsModel);
    }

    private void setColumnFactories() {
        receivedUserColumn.setCellValueFactory(new PropertyValueFactory<>("fromName"));
        receivedDateColumn.setCellValueFactory(new PropertyValueFactory<>("sentDate"));

        sentUserColumn.setCellValueFactory(new PropertyValueFactory<>("toName"));
        sentDateColumn.setCellValueFactory(new PropertyValueFactory<>("sentDate"));

        receivedAcceptColumn.setCellFactory(acceptButtonFactory());
        receivedRejectColumn.setCellFactory(rejectButtonFactory());

        sentUnsendColumn.setCellFactory(unsendButtonFactory());
    }

    private void populateTables() {
        Iterable<FriendRequest> receivedFriendRequests = service.getPendingFriendRequests();
        Iterable<FriendRequest> sentFriendRequests = service.getSentFriendRequests();

        List<FriendRequest> receivedFriendRequestsList = StreamSupport.stream(receivedFriendRequests.spliterator(), false).toList();
        List<FriendRequest> sentFriendRequestsList = StreamSupport.stream(sentFriendRequests.spliterator(), false).toList();

        receivedRequestsModel.setAll(receivedFriendRequestsList);
        sentRequestsModel.setAll(sentFriendRequestsList);
    }

    private Callback<TableColumn<FriendRequest, String>, TableCell<FriendRequest, String>> acceptButtonFactory() {
        return new Callback<>() {
            @Override
            public TableCell<FriendRequest, String> call(TableColumn<FriendRequest, String> param) {
                return new TableCell<>() {
                    final Button acceptButton = new Button("Accept");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            acceptButton.setOnAction(event -> {
                                FriendRequest friendRequest = getTableView().getItems().get(getIndex());
                                service.acceptFriendRequest(friendRequest.getId().getFirst().getId());
                                populateTables();
                            });
                            setGraphic(acceptButton);
                            setText(null);
                        }
                    }
                };
            }
        };
    }

    private Callback<TableColumn<FriendRequest, String>, TableCell<FriendRequest, String>> rejectButtonFactory() {
        return new Callback<>() {
            @Override
            public TableCell<FriendRequest, String> call(TableColumn<FriendRequest, String> param) {
                return new TableCell<>() {
                    final Button rejectButton = new Button("Reject");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            rejectButton.setOnAction(event -> {
                                FriendRequest friendRequest = getTableView().getItems().get(getIndex());
                                service.rejectFriendRequest(friendRequest.getId().getFirst().getId());
                                populateTables();
                            });
                            setGraphic(rejectButton);
                            setText(null);
                        }
                    }
                };
            }
        };
    }

    private Callback<TableColumn<FriendRequest, String>, TableCell<FriendRequest, String>> unsendButtonFactory() {
        return new Callback<>() {
            @Override
            public TableCell<FriendRequest, String> call(TableColumn<FriendRequest, String> param) {
                return new TableCell<>() {
                    final Button unsendButton = new Button("Unsend");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            unsendButton.setOnAction(event -> {
                                FriendRequest friendRequest = getTableView().getItems().get(getIndex());
                                service.removeFriendRequest(friendRequest);
                                populateTables();
                            });
                            setGraphic(unsendButton);
                            setText(null);
                        }
                    }
                };
            }
        };
    }

    @Override
    public void update(RemoveUserEvent event) {
        populateTables();
    }
}
