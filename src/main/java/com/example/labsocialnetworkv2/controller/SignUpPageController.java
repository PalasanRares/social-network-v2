package com.example.labsocialnetworkv2.controller;

import com.example.labsocialnetworkv2.application.Service;
import com.example.labsocialnetworkv2.constants.DateFormatter;
import com.example.labsocialnetworkv2.domain.FriendRequest;
import com.example.labsocialnetworkv2.domain.Message;
import com.example.labsocialnetworkv2.domain.User;
import com.example.labsocialnetworkv2.utils.events.RemoveUserEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SignUpPageController{
    private Service service;
    @FXML
    private TextField userName;
    @FXML
    private TextField firstName;
    @FXML
    private DatePicker birthday;
    @FXML
    private TextField lastName;
    @FXML
    private PasswordField password;

        public void setService(Service service) {
            this.service = service;

            init();
        }


        public void init() {

            // Converter
            StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
                DateTimeFormatter dateFormatter =
                        DateTimeFormatter.ofPattern("dd-MM-yyyy");

                @Override
                public String toString(LocalDate date) {
                    if (date != null) {
                        return dateFormatter.format(date);
                    } else {
                        return "";
                    }
                }
                @Override
                public LocalDate fromString(String string) {
                    if (string != null && !string.isEmpty()) {
                        return LocalDate.parse(string, dateFormatter);
                    } else {
                        return null;
                    }
                }
            };
            birthday.setConverter(converter);
            birthday.setPromptText("dd-MM-yyyy");
        }

    @FXML
    public void handleGoBackButton(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getClassLoader().getResource("com/example/labsocialnetworkv2/login-page.fxml"));
            AnchorPane mainPageLayout = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(mainPageLayout));
            stage.show();

            LoginPageController mainPageController = fxmlLoader.getController();
            mainPageController.setService(service);

            ((Node) (event.getSource())).getScene().getWindow().hide();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    @FXML
    public void handleSignUpButton(ActionEvent event) {
        String username = userName.getText();
        String pas = password.getText();
        String firstname = firstName.getText();
        String lastname = lastName.getText();
        String bday = birthday.getValue().format(DateFormatter.STANDARD_DATE_FORMAT);
        try {

            if(username == "" || pas == ""|| firstname == ""|| lastname == "")
                throw new Exception("smh");
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("SignUp Error");
            alert.setContentText("You need to give something!");
            alert.showAndWait();
            return ;
        }
        String[] args = new String[6];
        args[0]=username;
        args[1]=pas;
        args[2]=firstname;
        args[3]=lastname;
        args[4]=bday;
        service.addUser(args);

        Boolean userExist = service.loginUser(username, pas);

        if(!userExist) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Login Error");
            alert.setContentText("The User does not exist!WRONG USERNAME AND/OR PASSWORD");
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
