package com.example.labsocialnetworkv2.repository.db;

import com.example.labsocialnetworkv2.domain.User;
import com.example.labsocialnetworkv2.repository.Repository;
import com.example.labsocialnetworkv2.repository.UsernameRepository;
import com.example.labsocialnetworkv2.validator.UserValidator;
import com.example.labsocialnetworkv2.validator.Validator;
import com.example.labsocialnetworkv2.validator.exception.ValidationException;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UserDbRepository implements UsernameRepository<Integer, User> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<User> validator;

    public UserDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = new UserValidator();
    }

    @Override
    public void save(User entity) {
        try {
            validator.validate(entity);
        } catch (ValidationException ex) {
            System.out.println(ex.getMessage());
            return;
        }


        String sql = "INSERT INTO \"Users\" (\"username\",\"password\",\"FirstName\", \"LastName\", \"Birthday\",\"salt\") VALUES (?,?,?, ?, ?,?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, entity.getUsername());
           // ps.setString(2, entity.getPassword());
            SecureRandom random = new SecureRandom();//-----------------
                Integer randomInt = random.nextInt();
                String salt = randomInt.toString();
                String p = salt + entity.getPassword();

                MessageDigest digest = MessageDigest.getInstance("SHA-256");


            byte[] hash = digest.digest(p.getBytes(StandardCharsets.UTF_8));//-----------
            BigInteger no = new BigInteger(1, hash);
            String hashtext = no.toString(16);
            ps.setString(2, hashtext );
            ps.setString(3, entity.getFirstName());
            ps.setString(4, entity.getLastName());
            ps.setDate(5, Date.valueOf(entity.getBirthday()));
            ps.setString(6, salt);
            ps.executeUpdate();
        } catch (SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(Integer id) {
        String sql = "DELETE FROM \"Users\" WHERE \"UserId\" = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public String getSalt(Integer id){
        String salt=null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("SELECT \"salt\" FROM \"Users\" WHERE \"UserId\" = ?")) {
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {

                salt = resultSet.getString("salt");


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salt;
    }
    public User getByUsername(String usern){
        User user = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("SELECT \"UserId\",\"password\",\"FirstName\", \"LastName\", \"Birthday\" FROM \"Users\" WHERE \"username\" = ?")) {
            ps.setString(1, usern);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Integer id = resultSet.getInt("UserId");
                String pass = resultSet.getString("password");
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("LastName");
                LocalDate birthday = resultSet.getDate("Birthday").toLocalDate();
                user = new User(usern,pass,firstName, lastName, birthday);
                user.setId(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
    @Override
    public User findOne(Integer id) {
        User user = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement("SELECT \"username\",\"password\",\"FirstName\", \"LastName\", \"Birthday\" FROM \"Users\" WHERE \"UserId\" = ?")) {
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                String use = resultSet.getString("username");
                String pass = resultSet.getString("password");
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("LastName");
                LocalDate birthday = resultSet.getDate("Birthday").toLocalDate();
                user = new User(use,pass,firstName, lastName, birthday);
                user.setId(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT \"username\",\"password\",\"FirstName\", \"LastName\", \"Birthday\" from \"Users\"");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Integer id = resultSet.getInt("UserId");
                String use = resultSet.getString("username");
                String pass = resultSet.getString("password");
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("LastName");
                LocalDate birthday = resultSet.getDate("Birthday").toLocalDate();
                User user = new User(use,pass,firstName, lastName, birthday);
                user.setId(id);
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public int size() {
        try (Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) AS Size FROM \"Users\"");
            ResultSet resultSet = ps.executeQuery()) {
            while (resultSet.next()) {
                Integer s = resultSet.getInt("Size");
                return s;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
