package com.example.labsocialnetworkv2.domain;

import com.example.labsocialnetworkv2.constants.DateFormatter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * User class for defining a network user
 */
public class User extends Entity<Integer> {

    private String firstName;
    private String lastName;
    private LocalDate birthday;
    //--
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //--
    private List<User> friendsList = null;

    /**
     * Creates a new instance of the User class
     * @param id unique identifier for user
     * @param firstName first name of the user
     * @param lastName last name of the user
     * @param birthday birthday of the user
     */
    public User(String username,String password,String firstName, String lastName, LocalDate birthday) {
        this.username=username;
        this.password=password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
    }

    public User() {
        this.username=null;
        this.password=null;
        this.firstName = null;
        this.lastName = null;
        this.birthday = null;
    }

    /**
     * Add a friend to the user's friends list
     * @param friend friend to be added
     */
    public void addFriend(User friend) {
        if (friendsList == null) {
            friendsList = new ArrayList<>();
        }
        friendsList.add(friend);
    }

    /**
     * Removes a friend from the user's friends list
     * @param friend friend to be removed
     */
    public void removeFriend(User friend) {
        if (friendsList == null) {
            throw new NullPointerException("Friends list is empty");
        }
        friendsList.removeIf(user -> user.equals(friend));
    }

    @Override
    public String toString() {
        return super.toString() + " " + username + " " + firstName + " " + lastName + " " + birthday.format(DateFormatter.STANDARD_DATE_FORMAT);
    }

    /**
     * First name getter
     * @return user's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Last name getter
     * @return user's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Birthday getter
     * @return user's birthday
     */
    public LocalDate getBirthday() {
        return birthday;
    }

    /**
     * Friends list getter
     * @return user's friends list
     */
    public List<User> getFriendsList() {
        return friendsList;
    }
}
