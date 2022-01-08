package com.example.labsocialnetworkv2.controller;

import com.example.labsocialnetworkv2.application.Service;
import com.example.labsocialnetworkv2.domain.Event;
import com.example.labsocialnetworkv2.domain.FriendRequest;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.StreamSupport;

public class EventsPageController {
    @FXML
    TableView<Event> tableView;
    @FXML
    TableColumn<Event, String> eventNameColumn;
    @FXML
    TableColumn<Event, String> eventDateColumn;
    @FXML
    TableColumn<Event, String> notifyColumn;

    @FXML
    TextField eventNameTextField;
    @FXML
    DatePicker eventDateDatePicker;

    ObservableList<Event> model = FXCollections.observableArrayList();

    private Service service;

    public void setService(Service service) {
        this.service = service;
        setTableColumnFactories();
        initModel();
    }

    private void setTableColumnFactories() {
        eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        eventDateColumn.setCellValueFactory(new PropertyValueFactory<>("eventDate"));

        notifyColumn.setCellFactory(param -> new TableCell<>() {
            private Button notifyButton = new Button("Notify");

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                Iterable<Event> userEvents = service.findSubscribedToEvents();
                List<Event> userEventsList = StreamSupport.stream(userEvents.spliterator(), false).toList();

                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Event currentEvent = getTableView().getItems().get(getIndex());
                    if (userEventsList.contains(currentEvent)) {
                        notifyButton.setText("Unsubscribe");
                        notifyButton.setOnAction(event -> {
                            service.unsubscribeUserToEvent(currentEvent.getId());
                            initModel();
                        });
                    }
                    else {
                        notifyButton.setOnAction(event -> {
                            service.subscribeUserToEvent(currentEvent.getId());
                            initModel();
                        });
                    }
                    setGraphic(notifyButton);
                    setText(null);
                }
            }
        });
    }

    private void initModel() {
        Iterable<Event> events = service.findAllEvents();
        List<Event> eventList = StreamSupport.stream(events.spliterator(), false).toList();

        model.setAll(eventList);
        tableView.setItems(model);
        tableView.refresh();
    }

    @FXML
    public void handleAddEventButton() {
        String eventName = eventNameTextField.getText();
        LocalDate eventDate = eventDateDatePicker.getValue();
        service.addEvent(new Event(eventName, eventDate));
        initModel();
    }

}
