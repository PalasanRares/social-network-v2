package com.example.labsocialnetworkv2;

import com.example.labsocialnetworkv2.application.Service;
import com.example.labsocialnetworkv2.controller.MainPageController;
import com.example.labsocialnetworkv2.domain.*;
import com.example.labsocialnetworkv2.repository.ConvRepository;
import com.example.labsocialnetworkv2.repository.ModifiableRepository;
import com.example.labsocialnetworkv2.repository.Repository;
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

public class MainPage extends Application {
    private UserDbRepository userRepository;
    private MessagesDbRepository msgRepository;
    private FriendRequestDbRepository friendRequestRepository;
    private FriendshipDbRepository friendshipRepository;
    private Service service;

    @Override
    public void start(Stage primaryStage) throws IOException {
        userRepository = new UserDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "postgres");
        friendshipRepository = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "postgres");
        friendRequestRepository = new FriendRequestDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "postgres");
        msgRepository = new MessagesDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "postgres");

        service = new Service(friendshipRepository, userRepository, msgRepository, friendRequestRepository);

        initView(primaryStage);
        primaryStage.show();
    }

    private void initView(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("main-page.fxml"));
        AnchorPane mainPageLayout = fxmlLoader.load();
        primaryStage.setTitle("Main");
        primaryStage.setScene(new Scene(mainPageLayout));

        MainPageController mainPageController = fxmlLoader.getController();
        mainPageController.setService(service);
    }
}
