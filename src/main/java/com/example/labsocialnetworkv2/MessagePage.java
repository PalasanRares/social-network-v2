package com.example.labsocialnetworkv2;

import com.example.labsocialnetworkv2.application.Service;
import com.example.labsocialnetworkv2.controller.MainPageController;
import com.example.labsocialnetworkv2.controller.MessagePageController;
import com.example.labsocialnetworkv2.domain.User;
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


public class MessagePage extends Application {

    private Service service;
    private User user;
    @Override
    public void start(Stage primaryStage) throws IOException {
        UserDbRepository userRepository = new UserDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "postgres");
        FriendshipDbRepository friendshipRepository = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "postgres");
        FriendRequestDbRepository friendRequestRepository = new FriendRequestDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "postgres");
        MessagesDbRepository msgRepository = new MessagesDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "postgres");

//        service = new Service(friendshipRepository, userRepository, msgRepository, friendRequestRepository);

        initView(primaryStage);
        primaryStage.show();
    }

    public void setService(Service service,User user) {
        this.service = service;
        this.user=user;
        // initView();
    }
    private void initView(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("message-page.fxml"));
        AnchorPane mainPageLayout = fxmlLoader.load();
        primaryStage.setTitle("Messages");
        primaryStage.setScene(new Scene(mainPageLayout));

        MessagePageController msgPageController = fxmlLoader.getController();
        msgPageController.setService(service,user);
    }
}

