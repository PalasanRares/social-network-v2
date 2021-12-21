package com.example.labsocialnetworkv2.domain;

import java.time.LocalDate;
import java.util.Objects;

public class FriendRequest extends Entity<Tuple<User, User>> {
    private String status;
    private LocalDate dataTrimiterii;
    public FriendRequest(Tuple<User, User> id, String status) {
        super(id);
        this.status = status;
        this.dataTrimiterii = LocalDate.now();
    }

    public String getStatus() {
        return status;
    }
    public LocalDate getDataTrimiterii(){return dataTrimiterii;}

    public User getUser1(){
        return id.getFirst();
    }
    @Override
    public String toString() {
        return "From: " + id.getFirst() + " | To: " + id.getSecond() + " | Status: " + status + " | Data: " +dataTrimiterii;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FriendRequest request = (FriendRequest) o;
        return Objects.equals(status, request.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), status);
    }
}
