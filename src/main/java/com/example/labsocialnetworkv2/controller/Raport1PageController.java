package com.example.labsocialnetworkv2.controller;

import com.example.labsocialnetworkv2.application.Service;
import com.example.labsocialnetworkv2.domain.Event;
import com.example.labsocialnetworkv2.domain.Message;
import com.example.labsocialnetworkv2.domain.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
    TableView<Message> tableView ;
    @FXML
    TableColumn<Message, String> messageColumn;
    @FXML
    TableColumn<Message, String> dateColumn;
    @FXML
    TableColumn<Message, Integer> replyColumn;

    @FXML
    TextField usernameTextField;

    @FXML
    DatePicker startDateDatePicker;
    @FXML
    DatePicker endDateDatePicker;



    private Service service;

    public void setService(Service service) {
        this.service = service;

       createTable();
    }

    private void createTable() {



        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        messageColumn.setPrefWidth(100);


        dateColumn.setCellValueFactory(new PropertyValueFactory<>("data"));
        dateColumn.setPrefWidth(100);


        replyColumn.setCellValueFactory(new PropertyValueFactory<>("reply"));
        replyColumn.setPrefWidth(100);


    }

    /*private void initModel() {


    }*/
    ObservableList<Message>model=FXCollections.observableArrayList();
    @FXML
    public void handleSearchButton(ActionEvent mouseEvent) {
        String username = usernameTextField.getText();
        LocalDate startDate = startDateDatePicker.getValue();
        LocalDate endDate = endDateDatePicker.getValue();
        List<Message> msgs=new ArrayList<Message>(StreamSupport.stream(service.getReceivedMessages(username,startDate,endDate).spliterator(), false).toList());
        model.setAll(msgs);
        tableView.setItems(model);
    }
}
