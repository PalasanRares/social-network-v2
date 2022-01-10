package com.example.labsocialnetworkv2.repository.db;

import com.example.labsocialnetworkv2.domain.Message;
import com.example.labsocialnetworkv2.domain.User;
import com.example.labsocialnetworkv2.repository.ConvRepository;
import com.example.labsocialnetworkv2.validator.MessageValidator;
import com.example.labsocialnetworkv2.validator.Validator;
import com.example.labsocialnetworkv2.validator.exception.ValidationException;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class MessagesDbRepository implements ConvRepository<Integer, Message> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Message> validator;
    public MessagesDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = new MessageValidator();
    }

    @Override
    public void save(Message entity) {
        try {
            validator.validate(entity);
        } catch (ValidationException ex) {
            System.out.println(ex.getMessage());
            return;
        }
        String sql = "INSERT INTO \"Messages\" (\"SenderId\", \"Message\", \"SendingDate\", \"ReplyTo\") VALUES (?, ? ,? ,?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, entity.getSender().getId());

            ps.setString(2, entity.getMessage());
            ps.setDate(3, Date.valueOf(entity.getData()));
            if (entity.getReply()==null) {
                ps.setNull(4, Types.INTEGER);
            }
            else {
                ps.setInt(4,entity.getReply().getId());
            }

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        //String sql2 = "SELECT TOP 1 * FROM \"Messages\" ORDER BY ID DESC";
        String sql2="SELECT \"Id\" FROM \"Messages\" ORDER BY \"Id\" DESC LIMIT 1";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement psu = connection.prepareStatement(sql2)) {

            ResultSet resultSet = psu.executeQuery();
            while (resultSet.next()) {
            Integer id = resultSet.getInt("Id");

            saveReceivers(id,entity.getReceivers());}
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void saveReceivers(Integer msgId,List<User> receivers){
        String sql = "INSERT INTO \"MessagesUsers\" (\"MessageId\", \"UserId\") VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            for(User u:receivers){
                ps.setInt(1, msgId);
                ps.setInt(2, u.getId());
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<Message> getReceivedMessagesPeriod(Integer loggedinid,Integer id,LocalDate startDate,LocalDate endDate){
        List<Message> messages = new ArrayList<>();
        String sql ="Select e.\"Id\" ,e.\"SenderId\",e.\"Message\",e.\"SendingDate\",e.\"ReplyTo\" from \"Messages\" e INNER JOIN \"MessagesUsers\" mu on e.\"Id\"=mu.\"MessageId\" WHERE (e.\"SenderId\" =? AND mu.\"UserId\" = ?)AND(e.\"SendingDate\" > ?)AND (e.\"SendingDate\" < ?)  ";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql) ){
            ps.setInt(1,id);
            ps.setInt(2,loggedinid);
            ps.setDate(3, Date.valueOf(startDate));
            ps.setDate(4, Date.valueOf(endDate));
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Integer id1 = resultSet.getInt("Id");
                Integer sid = resultSet.getInt("SenderId");

                String msg = resultSet.getString("Message");
                LocalDate day = resultSet.getDate("SendingDate").toLocalDate();
                Integer reply = resultSet.getInt("ReplyTo");

                List<User> receivers = getReceiversList(id);
                Message message = new Message(getSender(sid),receivers,msg,day,findOne(reply));
                message.setId(id1);
                messages.add(message);
            }
            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
    public void removeReceivers(Integer msgId){
        String sql = "DELETE FROM \"MessagesUsers\" WHERE \"MessageId\" =?;";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1,msgId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void remove(Integer integer) {
        String sql = "DELETE FROM \"Messages\" WHERE \"Id\" = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, integer);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();;
        }
    }

    @Override
    public Message findOne(Integer integer) {
        Message message = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM \"Messages\" WHERE \"Id\" = ?")) {
            ps.setInt(1, integer);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Integer sid = resultSet.getInt("SenderId");
                String msg = resultSet.getString("Message");
                LocalDate day = resultSet.getDate("SendingDate").toLocalDate();
                Integer reply = resultSet.getInt("ReplyTo");

               // PreparedStatement psUser = connection.prepareStatement("SELECT * FROM \"Users\" WHERE \"UserId\" = ?");
                //    psUser.setInt(1, sid);
                //    ResultSet resUser = psUser.executeQuery();
                //String firstName = resUser.getString("FirstName");
               // String lastName = resUser.getString("LastName");
                //LocalDate birthday = resUser.getDate("Birthday").toLocalDate();
               // User sender = new User(firstName, lastName, birthday);
               // sender.setId(sid);

                List<User> receivers = getReceiversList(integer);
                message = new Message(getSender(sid),receivers,msg,day,findOne(reply));
                message.setId(integer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return message;
    }
    public User getSender(Integer id){
        User user = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM \"Users\" WHERE \"UserId\" = ?")) {
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
    public Integer getMostRecentMessage(){
        Integer id=0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("SELECT \"Id\" FROM \"Messages\" ORDER BY \"Id\" DESC LIMIT 1")) {


            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
               id = resultSet.getInt("Id");
                return id;

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }
    public List<User> getReceiversList(Integer id){
        List<User> rez =new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("SELECT \"UserId\" FROM  \"MessagesUsers\" WHERE \"MessageId\"= ? ")) {

                ps.setInt(1, id);
                ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
               Integer usid = resultSet.getInt("UserId");

                User us = getSender(usid);//find one user
                us.setId(usid);
                rez.add(us);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rez;
    }
    public  List<Message> conversatie(Integer u1 ,Integer u2){
        List<Message> messages = new ArrayList<>();
        String sql ="Select e.\"Id\",e.\"SenderId\",e.\"Message\",e.\"SendingDate\",e.\"ReplyTo\" from \"Messages\" e INNER JOIN \"MessagesUsers\" mu on e.\"Id\"=mu.\"MessageId\" WHERE (e.\"SenderId\" =? AND mu.\"UserId\" = ?) OR (e.\"SenderId\" = ? AND mu.\"UserId\"= ?)   ";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql) ){
                  ps.setInt(1,u1);
                  ps.setInt(2,u2);
                  ps.setInt(3,u2);
                  ps.setInt(4,u1);
                  ResultSet resultSet = ps.executeQuery();
                 while (resultSet.next()) {
                Integer id = resultSet.getInt("Id");
                Integer sid = resultSet.getInt("SenderId");

                String msg = resultSet.getString("Message");
                LocalDate day = resultSet.getDate("SendingDate").toLocalDate();
                Integer reply = resultSet.getInt("ReplyTo");

                List<User> receivers = getReceiversList(id);
                Message message = new Message(getSender(sid),receivers,msg,day,findOne(reply));
                message.setId(id);
                messages.add(message);
            }
            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;

    }
    @Override
    public Iterable<Message> findAll() {
        Set<Message> messages = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from \"Messages\"");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Integer id = resultSet.getInt("Id");
                Integer sid = resultSet.getInt("SenderId");
                //Array rec= resultSet.getArray("ReceiversIds");
                String msg = resultSet.getString("Message");
                LocalDate day = resultSet.getDate("SendingDate").toLocalDate();
                Integer reply = resultSet.getInt("ReplyTo");

               // PreparedStatement psUser = connection.prepareStatement("SELECT * FROM \"Users\" WHERE \"UserId\" = ?");
                //psUser.setInt(1, sid);
                //ResultSet resUser = psUser.executeQuery();
                //String firstName = resUser.getString("FirstName");
               // String lastName = resUser.getString("LastName");
               // LocalDate birthday = resUser.getDate("Birthday").toLocalDate();
                //User sender = new User(firstName, lastName, birthday);
               // sender.setId(sid);

                List<User> receivers = getReceiversList(id);
                Message message = new Message(getSender(sid),receivers,msg,day,findOne(reply));
                message.setId(id);
                messages.add(message);
            }
            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public int size() {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) AS Size FROM \"Messages\"");
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

    @Override
    public boolean modify(Message newEntity,Boolean all) {
        //if(newEntity.getId()==null)throw NullPointerException("Id must not be null");
        //if(findOne(newEntity.getId())==null)throw MessageNotFoundException("The message you want to modify does not exist!");
        //de mutat in service
        //if(all)removeReceivers(newEntity.getId());
        String sql = "UPDATE \"Messages\" SET \"Message\" = ? , \"ReplyTo\" = ? WHERE \"Id\" = ? ";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newEntity.getMessage());
            //ps.setInt(2, newEntity.getReply().getId());
            if (newEntity.getReply()==null) {
                ps.setNull(2, Types.INTEGER);
            }
            else {
                ps.setInt(2,newEntity.getReply().getId());
                if(all){
                    List<User> m=getReceiversList(newEntity.getReply().getId());


                    m.add(newEntity.getReply().getSender());

                    m.remove(findOne(newEntity.getId()).getSender());
                    System.out.println(m);
                    removeReceivers(newEntity.getId());
                    saveReceivers(newEntity.getId(),m);}

            }
            ps.setInt(3, newEntity.getId());
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }


    @Override
    public Iterable<Message> findAllForId(Integer integer) {
        List<Message> list = new ArrayList<>();
        for(Message m:findAll()){
            if(m.getSender().getId().equals(integer))
                list.add(m);
        }
            return list;
    }
}
