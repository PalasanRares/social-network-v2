package com.example.labsocialnetworkv2.controller;


import com.example.labsocialnetworkv2.application.Service;
import com.example.labsocialnetworkv2.domain.User;
import com.example.labsocialnetworkv2.domain.Message;

import com.example.labsocialnetworkv2.utils.events.RemoveUserEvent;
import com.example.labsocialnetworkv2.utils.observer.Observer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class MessagePageController  { //TODO: de vazut cum fac cu Observer
    private Service service;
    private User user;
    private Message msgSelected;
    @FXML
    private VBox vboxMsg;
    @FXML
    private ScrollPane scpn;
    @FXML
    private Button goBack;
    @FXML
    private Button sendButton;
    @FXML
    private TextField msgField;
    @FXML
    private Pane replyArea;

    public void setService(Service service,User user) {
        this.service = service;
        this.user=user;
        init();
    }


    public void init() {


        vboxMsg.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                scpn.setVvalue((Double) newValue);
            }
        });
        //System.out.println(service.getLoggedInUser());
        //List<Message> msgs =service.Conversatie(service.getLoggedInUser().getId(), user.getId());
       for(Message msg:service.Conversatie(service.getLoggedInUser().getId(), user.getId()))
        {
            HBox hBox =new HBox();
            if(Objects.equals(msg.getSender().getId(), service.getLoggedInUser().getId()))//mesajele tale
            {
            hBox.setAlignment(Pos.CENTER_RIGHT); //mesajele trimise de tine sunt la dreapta

            Text text=new Text(msg.getMessage());
            TextFlow textFlow =new TextFlow();
            textFlow.setStyle("-fx-color: rgb(57,83,112); " + " -fx-background-color: rgb(143,162,182);"+ " -fx-background-radius :20px ;");
            textFlow.setPadding(new Insets(5,10,5,10));
            text.setFill(Color.color(0, 0, 0));

                EventHandler<MouseEvent> mouseEventFilter
                        = e -> System.out.println("Mouse event filter has been called.");
                // Create a MouseEvent handler
                EventHandler<MouseEvent> mouseEventHandler
                        = e ->{ msgSelected=msg;
                Button btn =new Button("Reply to:"+ msg.getMessage()+" X");
                replyArea.getChildren().clear();
                    btn.setOnAction(evt -> {replyArea.getChildren().clear();msgSelected=null;});
                replyArea.getChildren().add(btn);
                };

                //TODO:de infrumusetat butonul


                // Register the MouseEvent filter and handler to the Circle
                // for mouse-clicked events
                textFlow.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEventFilter);
                textFlow.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandler);

            ///-REPLY
                if(msg.getReply()!=null){
                    hBox.setAlignment(Pos.CENTER_RIGHT); //mesajele trimise de tine sunt la dreapta

                    Text text1=new Text("Reply To:"+msg.getReply().getMessage()+"\n");
                    TextFlow textFlow1 =new TextFlow(text);
                    textFlow1.setStyle("-fx-color: rgb(57,83,112); " + " -fx-background-color: rgb(143,162,182);"+ " -fx-background-radius :20px ;");
                    textFlow1.setPadding(new Insets(5,10,5,10));
                    text1.setFill(Color.CRIMSON);
                    textFlow.getChildren().addAll(text1,text);
                }
                else {
                    textFlow.getChildren().add(text);
                }


            ///
            hBox.getChildren().add(textFlow);
            vboxMsg.getChildren().add(hBox);}
            else{
                hBox.setAlignment(Pos.CENTER_LEFT);

                Text text=new Text(msg.getMessage());
                TextFlow textFlow =new TextFlow();
                textFlow.setStyle("-fx-color: rgb(150,191,225);" +
                        "-fx-background-color: rgb(41,60,75);"+
                        "-fx-background-radius :20px;");
                textFlow.setPadding(new Insets(5,10,5,10));
                text.setFill(Color.color(1, 1, 1));


                EventHandler<MouseEvent> mouseEventFilter
                        = e -> System.out.println("Mouse event filter has been called.");
                // Create a MouseEvent handler
                EventHandler<MouseEvent> mouseEventHandler
                        = e -> { msgSelected=msg;
                Button btn =new Button("Reply to:"+ msg.getMessage()+" X");
                replyArea.getChildren().clear();
                    btn.setOnAction(evt -> {replyArea.getChildren().clear();msgSelected=null;});
                replyArea.getChildren().add(btn);
                };

                //TODO:de infrumusetat butonul
                //SI AICI

                // Register the MouseEvent filter and handler to the Circle
                // for mouse-clicked events
                textFlow.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEventFilter);
                textFlow.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandler);
                //
                if(msg.getReply()!=null){
                    hBox.setAlignment(Pos.CENTER_LEFT); //mesajele trimise de tine sunt la dreapta

                    Text text1=new Text("Reply To:"+msg.getReply().getMessage()+"\n");
                    TextFlow textFlow1 =new TextFlow(text);
                    textFlow1.setStyle("-fx-color: rgb(57,83,112); " + " -fx-background-color: rgb(143,162,182);"+ " -fx-background-radius :20px ;");
                    textFlow1.setPadding(new Insets(5,10,5,10));
                    text1.setFill(Color.CRIMSON);
                    textFlow.getChildren().addAll(text1,text);
                }
                else {
                    textFlow.getChildren().add(text);
                }

                hBox.getChildren().add(textFlow);

                vboxMsg.getChildren().add(hBox);
            }

        }

    }
    @FXML
    public void handleGoBackButton(ActionEvent event) {
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

            ((Node) (event.getSource())).getScene().getWindow().hide();//oare sa avem fereastra separata pt mesaje?
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    @FXML
    public void handleSendButton() {
        String value = msgField.getText();
        String[] args = new String[3];
        args[0]=user.getId().toString();
        args[1]="0";
        args[2]=value;

                //user.getId() + "0" + value;
        service.addMessage(args);

        //trebuie adaugat spatiu in care iti apare mesajul selectat ,si un x pt a face mesajul iar null
        if(msgSelected !=null)
            service.modifyMessage(service.getLastMessage(),value, msgSelected.getId(), true);

        msgField.clear();
        HBox hBox =new HBox();
            //TODO: de aratat  ca s-a dat reply la un mesaj mai frumos poate
            hBox.setAlignment(Pos.CENTER_RIGHT); //mesajele trimise de tine sunt la dreapta

            Text text=new Text(value);
            TextFlow textFlow =new TextFlow();
            textFlow.setStyle("-fx-color: rgb(57,83,112); " + " -fx-background-color: rgb(143,162,182);"+ " -fx-background-radius :20px ;");
            textFlow.setPadding(new Insets(5,10,5,10));
            text.setFill(Color.color(0, 0, 0));
        if(msgSelected !=null){
            hBox.setAlignment(Pos.CENTER_RIGHT); //mesajele trimise de tine sunt la dreapta

            Text text1=new Text("Reply To:"+msgSelected.getMessage()+"\n");
            TextFlow textFlow1 =new TextFlow(text);
            textFlow1.setStyle("-fx-color: rgb(57,83,112); " + " -fx-background-color: rgb(143,162,182);"+ " -fx-background-radius :20px ;");
            textFlow1.setPadding(new Insets(5,10,5,10));
            text1.setFill(Color.CRIMSON);
            textFlow.getChildren().addAll(text1,text);
        }
        else {
            textFlow.getChildren().add(text);
        }
        replyArea.getChildren().clear();
             hBox.getChildren().add(textFlow);
            vboxMsg.getChildren().add(hBox);

    }
}

