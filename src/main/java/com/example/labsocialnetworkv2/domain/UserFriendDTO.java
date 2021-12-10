package com.example.labsocialnetworkv2.domain;


import com.example.labsocialnetworkv2.constants.DateFormatter;

import java.time.LocalDate;

public class UserFriendDTO {
    private final String firstName;
    private final String lastName;
    private final LocalDate friendshipDate;

    public UserFriendDTO(String firstName, String lastName, LocalDate friendshipDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.friendshipDate = friendshipDate;
    }

    @Override
    public String toString() {
        return firstName + " | " + lastName + " | " + friendshipDate.format(DateFormatter.STANDARD_DATE_FORMAT);
    }
}
