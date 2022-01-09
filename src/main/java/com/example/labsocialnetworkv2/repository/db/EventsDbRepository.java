package com.example.labsocialnetworkv2.repository.db;

import com.example.labsocialnetworkv2.domain.Event;
import com.example.labsocialnetworkv2.repository.Repository;
import com.example.labsocialnetworkv2.validator.exception.NotImplementedException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventsDbRepository implements Repository<Integer, Event> {
    private final String url;
    private final String username;
    private final String password;

    public EventsDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public void save(Event event) {
        String sql = "INSERT INTO events (event_name, event_date) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, event.getEventName());
            ps.setDate(2, Date.valueOf(event.getEventDate()));
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void remove(Integer id) {
        throw new NotImplementedException("This method may not be called");
    }

    @Override
    public Event findOne(Integer id) {
        throw new NotImplementedException("This method may not be called");
    }

    @Override
    public Iterable<Event> findAll() {
        String sql = "SELECT * FROM events";
        List<Event> events = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Integer eventId = resultSet.getInt("event_id");
                String eventName = resultSet.getString("event_name");
                LocalDate eventDate = resultSet.getDate("event_date").toLocalDate();
                Event event = new Event(eventId, eventName, eventDate);
                events.add(event);
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return events;
    }

    @Override
    public int size() {
        throw new NotImplementedException("This method may not be called");
    }

    public Iterable<Event> findUserEvents(Integer userId) {
        String sql = "SELECT events.event_id, events.event_name, events.event_date FROM events INNER JOIN users_events ON events.event_id = users_events.event_id " +
                "WHERE users_events.user_id = ? AND users_events.notifications = TRUE";
        List<Event> events = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Integer eventId = resultSet.getInt("event_id");
                String eventName = resultSet.getString("event_name");
                LocalDate eventDate = resultSet.getDate("event_date").toLocalDate();
                Event event = new Event(eventId, eventName, eventDate);
                events.add(event);
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return events;
    }

    public void addUserToEvent(Integer userId, Integer eventId, Boolean notifications) {
        String sql = "INSERT INTO users_events(user_id, event_id, notifications) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, eventId);
            ps.setBoolean(3, notifications);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void unsubscribeToEvent(Integer userId, Integer eventId) {
        String sql = "DELETE FROM users_events WHERE user_id = ? AND event_id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, eventId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
