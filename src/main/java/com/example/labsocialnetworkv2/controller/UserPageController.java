package com.example.labsocialnetworkv2.controller;

import com.example.labsocialnetworkv2.application.Service;

public class UserPageController {
   private Service service;


    public void setService(Service service) {
        this.service = service;
        //service.addObserver(this);
       // initModel();
    }
}
