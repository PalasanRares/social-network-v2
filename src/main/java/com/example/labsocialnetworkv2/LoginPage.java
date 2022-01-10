package com.example.labsocialnetworkv2;

import com.example.labsocialnetworkv2.application.Service;
import com.example.labsocialnetworkv2.controller.LoginPageController;
import com.example.labsocialnetworkv2.repository.db.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginPage extends Application {
    private Service service;

    @Override
    public void start(Stage primaryStage) throws IOException {
        UserDbRepository userRepository = new UserDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "Indiferent1");
        FriendshipDbRepository friendshipRepository = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "Indiferent1");
        FriendRequestDbRepository friendRequestRepository = new FriendRequestDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "Indiferent1");
        MessagesDbRepository msgRepository = new MessagesDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "Indiferent1");
        EventsDbRepository eventRepository = new EventsDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "Indiferent1");

        service = new Service(friendshipRepository, userRepository, msgRepository, friendRequestRepository, eventRepository);

        initView(primaryStage);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public void setService(Service service) {
        this.service = service;
       // initView();
    }

    private void initView(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("login-page.fxml"));
        AnchorPane loginPageLayout = fxmlLoader.load();
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(loginPageLayout));

        LoginPageController loginPageController = fxmlLoader.getController();
        loginPageController.setService(service);
    }
}
