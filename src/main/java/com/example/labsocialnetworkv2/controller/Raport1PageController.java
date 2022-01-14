package com.example.labsocialnetworkv2.controller;

import com.example.labsocialnetworkv2.application.Service;
import com.example.labsocialnetworkv2.domain.Event;
import com.example.labsocialnetworkv2.domain.Message;
import com.example.labsocialnetworkv2.domain.User;
import com.example.labsocialnetworkv2.validator.exception.UserNotFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Raport1PageController {
    @FXML
     Button savePdf;
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


        dateColumn.setCellValueFactory(new PropertyValueFactory<>("data"));


        replyColumn.setCellValueFactory(new PropertyValueFactory<>("reply"));


    }

    ObservableList<Message>model=FXCollections.observableArrayList();
    @FXML
    public void handleSearchButton(ActionEvent mouseEvent) {
        String username = usernameTextField.getText();
        LocalDate startDate = startDateDatePicker.getValue();
        LocalDate endDate = endDateDatePicker.getValue();
        try{
            List<Message> msgs=new ArrayList<Message>(StreamSupport.stream(service.getReceivedMessages(username,startDate,endDate).spliterator(), false).toList());
            model.setAll(msgs);
            tableView.setItems(model);
        }catch(UserNotFoundException e)
        {
            System.out.println(e);
        }catch(NullPointerException e){
            System.out.println(e);
        }

    }
    private Stage stage;

    @FXML
    TextField fileNameField;



    public void setStage(Stage stage) {
        this.stage = stage;
    }


    public void handleCreatePdfButton(ActionEvent actionEvent) {
        String username = usernameTextField.getText();
        LocalDate startDate = startDateDatePicker.getValue();
        LocalDate endDate = endDateDatePicker.getValue();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);
        service.saveMessageReportToPDF(selectedDirectory.getAbsolutePath(), fileNameField.getText() + ".pdf", startDate, endDate,username);

    }
}
