package com.example.labsocialnetworkv2;

import com.example.labsocialnetworkv2.application.Service;
import com.example.labsocialnetworkv2.controller.MainPageController;
import com.example.labsocialnetworkv2.controller.UserPageController;
import com.example.labsocialnetworkv2.repository.db.FriendRequestDbRepository;
import com.example.labsocialnetworkv2.repository.db.FriendshipDbRepository;
import com.example.labsocialnetworkv2.repository.db.MessagesDbRepository;
import com.example.labsocialnetworkv2.repository.db.UserDbRepository;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;


public class UserPage extends Application {

   private UserDbRepository userRepository;
    private MessagesDbRepository msgRepository;
    private FriendRequestDbRepository friendRequestRepository;
    private FriendshipDbRepository friendshipRepository;
    private Service service;

    @Override
    public void start(Stage primaryStage) throws IOException {
        userRepository = new UserDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "Indiferent1");
        friendshipRepository = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "Indiferent1");
        friendRequestRepository = new FriendRequestDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "Indiferent1");
        msgRepository = new MessagesDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "Indiferent1");

        service = new Service(friendshipRepository, userRepository, msgRepository, friendRequestRepository);

        initView(primaryStage);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private void initView(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("user-page.fxml"));
        AnchorPane mainPageLayout = fxmlLoader.load();
        primaryStage.setTitle("User");
        primaryStage.setScene(new Scene(mainPageLayout));

        UserPageController userPageController = fxmlLoader.getController();
        //userPageController.setService(service);
    }

}

