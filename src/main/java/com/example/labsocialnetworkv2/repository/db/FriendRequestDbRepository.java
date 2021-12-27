package com.example.labsocialnetworkv2.repository.db;

import com.example.labsocialnetworkv2.domain.FriendRequest;
import com.example.labsocialnetworkv2.domain.Tuple;
import com.example.labsocialnetworkv2.domain.User;
import com.example.labsocialnetworkv2.repository.ModifiableRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class FriendRequestDbRepository implements ModifiableRepository<Tuple<User, User>, FriendRequest> {
    private final String url;
    private final String username;
    private final String password;

    public FriendRequestDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public void save(FriendRequest entity) {
        String sql = "INSERT INTO friend_requests (\"from\", \"to\", status ,\"dataTrimiterii\") VALUES (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, entity.getId().getFirst().getId());
            ps.setInt(2, entity.getId().getSecond().getId());
            ps.setString(3, entity.getStatus());
            ps.setDate(4,Date.valueOf(entity.getDataTrimiterii()));
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void remove(Tuple<User, User> requestId) {
        String sql = "DELETE FROM friend_requests WHERE \"from\" = ? AND \"to\" = ? OR \"from\" = ? AND \"to\" = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, requestId.getFirst().getId());
            ps.setInt(2, requestId.getSecond().getId());
            ps.setInt(3, requestId.getSecond().getId());
            ps.setInt(4, requestId.getFirst().getId());
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public FriendRequest findOne(Tuple<User, User> id) {
        FriendRequest request = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM friend_requests WHERE \"from\" = ? AND \"to\" = ?")) {
            ps.setInt(1, id.getFirst().getId());
            ps.setInt(2, id.getSecond().getId());
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                Integer id1 = resultSet.getInt("from");
                Integer id2 = resultSet.getInt("to");
                String status = resultSet.getString("status");

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

                request = new FriendRequest(new Tuple<>(user1, user2), status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return request;
    }

    @Override
    public Iterable<FriendRequest> findAll() {
        Set<FriendRequest> friendRequests = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM friend_requests");
             ResultSet resultSet = ps.executeQuery()) {
            while (resultSet.next()) {
                Integer from = resultSet.getInt("from");
                Integer to = resultSet.getInt("to");
                String status = resultSet.getString("status");
                LocalDate sendDate = resultSet.getDate("dataTrimiterii").toLocalDate();

                PreparedStatement psUsers = connection.prepareStatement("SELECT * FROM \"Users\" WHERE \"UserId\" = ? OR \"UserId\" = ?");
                psUsers.setInt(1, from);
                psUsers.setInt(2, to);
                ResultSet rsUsers = psUsers.executeQuery();
                User user1 = new User();
                User user2 = new User();
                while (rsUsers.next()) {
                    if (rsUsers.getInt("UserId") == from) {
                        user1 = new User(rsUsers.getString("FirstName"), rsUsers.getString("LastName"), rsUsers.getDate("Birthday").toLocalDate());
                        user1.setId(from);
                    }
                    if (rsUsers.getInt("UserId") == to) {
                        user2 = new User(rsUsers.getString("FirstName"), rsUsers.getString("LastName"), rsUsers.getDate("Birthday").toLocalDate());
                        user2.setId(to);
                    }
                }
                FriendRequest friendRequest = new FriendRequest(new Tuple<>(user1, user2), status, sendDate);
                friendRequests.add(friendRequest);
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return friendRequests;
    }

    @Override
    public Iterable<FriendRequest> findAllForId(Tuple<User, User> requestId) {
        Integer searchTo = requestId.getSecond().getId();
        Set<FriendRequest> requests = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friend_requests WHERE \"to\" = ?")) {
            statement.setInt(1, searchTo);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Integer from = resultSet.getInt("from");
                Integer to = resultSet.getInt("to");
                String status = resultSet.getString("status");
                PreparedStatement psUsers = connection.prepareStatement("SELECT * FROM \"Users\" WHERE \"UserId\" = ? OR \"UserId\" = ?");
                psUsers.setInt(1, from);
                psUsers.setInt(2, to);
                ResultSet users = psUsers.executeQuery();
                User user1 = new User();
                User user2 = new User();
                while (users.next()) {
                    if (users.getInt("UserId") == from) {
                        user1 = new User(users.getString("FirstName"), users.getString("LastName"), users.getDate("Birthday").toLocalDate());
                        user1.setId(from);
                    }
                    if (users.getInt("UserId") == to) {
                        user2 = new User(users.getString("FirstName"), users.getString("LastName"), users.getDate("Birthday").toLocalDate());
                        user2.setId(to);
                    }
                }
                FriendRequest friendRequest = new FriendRequest(new Tuple<User, User>(user1, user2), status);
                requests.add(friendRequest);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    @Override
    public int size() {
        return -1;
    }

    @Override
    public boolean modify(FriendRequest newRequest) {
        String sql = "UPDATE friend_requests SET status = ? WHERE \"from\" = ? AND \"to\" = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newRequest.getStatus());
            ps.setInt(2, newRequest.getId().getFirst().getId());
            ps.setInt(3, newRequest.getId().getSecond().getId());
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
