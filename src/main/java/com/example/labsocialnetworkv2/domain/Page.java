package com.example.labsocialnetworkv2.domain;

import java.util.List;

public class Page {
   /* Mesajele primite -numa primite ,sau conversatii???, Cererile de prietenie numa primite??.*/
    private String nume;
    private String prenume;
    private List<User> prieteni;
    private List<FriendRequest> cereriDePrietenie;


    public Page(String nume, String prenume, List<User> prieteni, List<FriendRequest> cereriDePrietenie) {
        this.nume = nume;
        this.prenume = prenume;
        this.prieteni = prieteni;
        this.cereriDePrietenie = cereriDePrietenie;

    }

    public String getNume() {
        return nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public List<User> getPrieteni() {
        return prieteni;
    }

    public List<FriendRequest> getCereriDePrietenie() {
        return cereriDePrietenie;
    }




    public void setNume(String nume) {
        this.nume = nume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public void setPrieteni(List<User> prieteni) {
        this.prieteni = prieteni;
    }

    public void setCereriDePrietenie(List<FriendRequest> cereriDePrietenie) {
        this.cereriDePrietenie = cereriDePrietenie;
    }


}

