package com.example.labsocialnetworkv2.repository.db;

import com.example.labsocialnetworkv2.domain.Friendship;
import com.example.labsocialnetworkv2.domain.Tuple;
import com.example.labsocialnetworkv2.domain.User;
import com.example.labsocialnetworkv2.repository.PaginatedRepository;
import com.example.labsocialnetworkv2.repository.Repository;
import com.example.labsocialnetworkv2.validator.FriendshipValidator;
import com.example.labsocialnetworkv2.validator.Validator;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class FriendshipDbRepository implements PaginatedRepository<Tuple<User, User>, Friendship> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Friendship> validator;

    public FriendshipDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = new FriendshipValidator();
    }

    @Override
    public void save(Friendship entity) {
        String sql = "INSERT INTO \"Friendships\" (\"FirstUserId\", \"SecondUserId\", \"FriendshipDate\") VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, entity.getId().getFirst().getId());
            ps.setInt(2, entity.getId().getSecond().getId());
            ps.setDate(3, Date.valueOf(entity.getDate()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(Tuple<User, User> id) {
        String sql = "DELETE FROM \"Friendships\" WHERE \"FirstUserId\" = ? AND \"SecondUserId\" = ? OR \"FirstUserId\" = ? AND \"SecondUserId\" = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id.getFirst().getId());
            ps.setInt(2, id.getSecond().getId());
            ps.setInt(3, id.getSecond().getId());
            ps.setInt(4, id.getFirst().getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();;
        }
    }

    @Override
    public Friendship findOne(Tuple<User, User> id) {
        Friendship friendship = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM \"Friendships\" WHERE \"FirstUserId\" = ? AND \"SecondUserId\" = ? " +
                     "OR \"FirstUserId\" = ? AND \"SecondUserId\" = ?")) {
            ps.setInt(1, id.getFirst().getId());
            ps.setInt(2, id.getSecond().getId());
            ps.setInt(3, id.getSecond().getId());
            ps.setInt(4, id.getFirst().getId());
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();
            Integer id1 = resultSet.getInt("FirstUserId");
            Integer id2 = resultSet.getInt("SecondUserId");
            LocalDate friendshipDate = resultSet.getDate("FriendshipDate").toLocalDate();

            PreparedStatement psUsers = connection.prepareStatement("SELECT * FROM \"Users\" WHERE \"UserId\" = ? OR \"UserId\" = ?");
            psUsers.setInt(1, id1);
            psUsers.setInt(2, id2);
            ResultSet users = psUsers.executeQuery();
            users.next();
            User user1 = new User(users.getString("username"),users.getString("password"),users.getString("FirstName"), users.getString("LastName"), users.getDate("Birthday").toLocalDate());
            user1.setId(users.getInt("UserId"));
            users.next();
            User user2 = new User(users.getString("username"),users.getString("password"),users.getString("FirstName"), users.getString("LastName"), users.getDate("Birthday").toLocalDate());
            user2.setId(users.getInt("UserId"));

            friendship = new Friendship(new Tuple<>(user1, user2), friendshipDate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendship;
    }

    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM \"Friendships\"");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Integer id1 = resultSet.getInt("FirstUserId");
                Integer id2 = resultSet.getInt("SecondUserId");
                LocalDate friendshipDate = resultSet.getDate("FriendshipDate").toLocalDate();

                PreparedStatement psUsers = connection.prepareStatement("SELECT * FROM \"Users\" WHERE \"UserId\" = ? OR \"UserId\" = ?");
                psUsers.setInt(1, id1);
                psUsers.setInt(2, id2);
                ResultSet users = psUsers.executeQuery();
                users.next();
                User user1 = new User(users.getString("username"),users.getString("password"),users.getString("FirstName"), users.getString("LastName"), users.getDate("Birthday").toLocalDate());
                user1.setId(users.getInt("UserId"));
                users.next();
                User user2 = new User(users.getString("username"),users.getString("password"),users.getString("FirstName"), users.getString("LastName"), users.getDate("Birthday").toLocalDate());
                user2.setId(users.getInt("UserId"));

                Friendship friendship = new Friendship(new Tuple<>(user1, user2), friendshipDate);
                friendships.add(friendship);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    @Override
    public Iterable<Friendship> findAllPage(int pageNumber, int rowsOnPage, Integer userId) {
        String sql = "SELECT * FROM \"Friendships\" " +
                "WHERE \"FirstUserId\" = ? OR \"SecondUserId\" = ? " +
                "ORDER BY \"FriendshipDate\" " +
                "OFFSET (? * ?) ROWS " +
                "FETCH NEXT ? ROWS ONLY";
        Set<Friendship> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setInt(1, userId);
            statement.setInt(2, userId);
            statement.setInt(3, pageNumber);
            statement.setInt(4, rowsOnPage);
            statement.setInt(5, rowsOnPage);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Integer id1 = resultSet.getInt("FirstUserId");
                Integer id2 = resultSet.getInt("SecondUserId");
                LocalDate friendshipDate = resultSet.getDate("FriendshipDate").toLocalDate();

                PreparedStatement psUsers = connection.prepareStatement("SELECT * FROM \"Users\" WHERE \"UserId\" = ? OR \"UserId\" = ?");
                psUsers.setInt(1, id1);
                psUsers.setInt(2, id2);
                ResultSet users = psUsers.executeQuery();
                users.next();
                User user1 = new User(users.getString("FirstName"), users.getString("LastName"), users.getDate("Birthday").toLocalDate());
                user1.setId(users.getInt("UserId"));
                users.next();
                User user2 = new User(users.getString("FirstName"), users.getString("LastName"), users.getDate("Birthday").toLocalDate());
                user2.setId(users.getInt("UserId"));

                Friendship friendship = new Friendship(new Tuple<>(user1, user2), friendshipDate);
                friendships.add(friendship);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    @Override
    public int size() {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) AS Size FROM \"Friendships\"");
             ResultSet resultSet = ps.executeQuery()) {
            resultSet.next();
            return resultSet.getInt("Size");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
