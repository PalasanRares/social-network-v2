package com.example.labsocialnetworkv2.controller;

import com.example.labsocialnetworkv2.application.Service;
import com.example.labsocialnetworkv2.domain.Event;
import com.example.labsocialnetworkv2.domain.Message;
import com.example.labsocialnetworkv2.domain.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Raport1PageController {
    @FXML
    TableView<Message> tableView = createTable();
   /* @FXML
    TableColumn<Message, String> messageColumn;
    @FXML
    TableColumn<Message, String> dateColumn;
    @FXML
    TableColumn<Message, Integer> replyColumn;*/

    @FXML
    TextField usernameTextField;

    @FXML
    DatePicker startDateDatePicker;
    @FXML
    DatePicker endDateDatePicker;

    ObservableList<Event> model = FXCollections.observableArrayList();

    private Service service;

    public void setService(Service service) {
        this.service = service;

       // initModel();
    }

    private TableView<Message> createTable() {
        TableView<Message> table = new TableView<>();

        TableColumn<Message, String> columnMessage = new TableColumn<>("Message");
        columnMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
        columnMessage.setPrefWidth(100);

        TableColumn<Message, String> columnSendingDate = new TableColumn<>("SendingDate");
        columnSendingDate.setCellValueFactory(new PropertyValueFactory<>("sendingDate"));
        columnSendingDate.setPrefWidth(100);

        TableColumn<Message, Integer> columnReplyTo = new TableColumn<>("ReplyTo");
        columnReplyTo.setCellValueFactory(new PropertyValueFactory<>("replyTo"));
        columnReplyTo.setPrefWidth(100);



        table.getColumns().add(columnMessage);
        table.getColumns().add(columnSendingDate);
        table.getColumns().add(columnReplyTo);
        return table;
    }

    /*private void initModel() {


    }*/

    @FXML
    public void handleSearchButton() {
        String username = usernameTextField.getText();
        LocalDate startDate = startDateDatePicker.getValue();
        LocalDate endDate = endDateDatePicker.getValue();
        List<Message> msgs=new ArrayList<Message>(StreamSupport.stream(service.getReceivedMessages(username,startDate,endDate).spliterator(), false).toList());

        tableView.setItems(FXCollections.observableArrayList(msgs));
    }
}
