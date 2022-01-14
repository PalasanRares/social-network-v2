package com.example.labsocialnetworkv2.application;

import com.example.labsocialnetworkv2.constants.DateFormatter;
import com.example.labsocialnetworkv2.constants.Sortbydate;
import com.example.labsocialnetworkv2.domain.*;
import com.example.labsocialnetworkv2.repository.ConvRepository;
import com.example.labsocialnetworkv2.repository.ModifiableRepository;
import com.example.labsocialnetworkv2.repository.PaginatedRepository;
import com.example.labsocialnetworkv2.repository.Repository;
import com.example.labsocialnetworkv2.repository.UsernameRepository;
import com.example.labsocialnetworkv2.repository.db.EventsDbRepository;
import com.example.labsocialnetworkv2.utils.events.RemoveUserEvent;
import com.example.labsocialnetworkv2.utils.observer.Observable;
import com.example.labsocialnetworkv2.utils.observer.Observer;
import com.example.labsocialnetworkv2.validator.exception.DuplicateFriendshipException;
import com.example.labsocialnetworkv2.validator.exception.MessageNotFoundException;
import com.example.labsocialnetworkv2.validator.exception.UserNotFoundException;
import com.example.labsocialnetworkv2.validator.exception.ValidationException;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1CFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import java.security.SecureRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service which manages user and friendship repositories
 */
public class Service implements Observable<RemoveUserEvent> {

    private final UsernameRepository<Integer, User> userRepository;

    private final PaginatedRepository<Tuple<User, User>, Friendship> friendshipRepository;

    private final ModifiableRepository<Tuple<User, User>, FriendRequest> friendRequestRepository;
    private User loggedInUser;
    private final ConvRepository<Integer, Message> messageRepository;

    private final EventsDbRepository eventRepository;
    /**
     * Creates an instance of type Services
     * @param friendshipRepository friendshipRepository to be used
     * @param userRepository userRepository to be used
     * @param messageRepository messageRepository to be used
     */

    public Service(PaginatedRepository<Tuple<User, User>, Friendship> friendshipRepository, UsernameRepository<Integer, User> userRepository, ConvRepository<Integer, Message> messageRepository, ModifiableRepository<Tuple<User, User>, FriendRequest> friendRequestRepository, EventsDbRepository eventRepository) {

        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.eventRepository = eventRepository;
        loggedInUser = null;
    }

    public Iterable<User> getFriendshipsPage(int pageNumber, int rowsOnPage) {
        return StreamSupport.stream(friendshipRepository.findAllPage(pageNumber, rowsOnPage, loggedInUser.getId()).spliterator(), false)
                .map(friendship -> {
                    if (friendship.getId().getFirst().equals(loggedInUser)) {
                        return friendship.getId().getSecond();
                    }
                    if (friendship.getId().getSecond().equals(loggedInUser)) {
                        return friendship.getId().getFirst();
                    }
                    return null;
                })
                .collect(Collectors.toList());
    }

    /**
     * Adds a friendship connection between two users
     * @param args String array, contains first user id and second user id
     */
    public void addFriendship(String[] args) {
        try {
            User u1 = userRepository.findOne(Integer.parseInt(args[0]));
            User u2 = userRepository.findOne(Integer.parseInt(args[1]));
            if (u1 == null || u2 == null) {
                throw new UserNotFoundException("Could not find user id");
            }
            Friendship friendship = new Friendship(new Tuple<>(u1, u2));
            for (Friendship f : friendshipRepository.findAll()) {
                if (f.equals(friendship)) {
                    throw new DuplicateFriendshipException("The users are already friends");
                }
            }
            u1.addFriend(u2);
            u2.addFriend(u1);
            friendshipRepository.save(friendship);
        }
        catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Removes a friendship connection between two users if it exists
     * @param id1 id of the first user
     * @param id2 id of the second user
     */
    public void removeFriendship(String id1, String id2) {
        User user1 = userRepository.findOne(Integer.parseInt(id1));
        User user2 = userRepository.findOne(Integer.parseInt(id2));
        Tuple<User, User> firstId = new Tuple<>(user1, user2);
        Tuple<User, User> secondId = new Tuple<>(user2, user1);
        if (friendshipRepository.findOne(firstId) != null) {
            friendshipRepository.remove(firstId);
            if (friendRequestRepository.findOne(firstId) != null) {
                friendRequestRepository.remove(firstId);
            }
            else if (friendRequestRepository.findOne(secondId) != null) {
                friendRequestRepository.remove(secondId);
            }
        }
    }

    public void removeFriendship(User user1, User user2) {
        Friendship friendship = new Friendship(new Tuple<>(user1, user2));
        if (friendshipRepository.findOne(friendship.getId()) != null) {
            friendshipRepository.remove(friendship.getId());
            if (friendRequestRepository.findOne(friendship.getId()) != null) {
                friendRequestRepository.remove(friendship.getId());
            }
            if (friendRequestRepository.findOne(new Tuple<>(user2, user1)) != null) {
                friendRequestRepository.remove(new Tuple<>(user2, user1));
            }
        }
        notifyObservers(new RemoveUserEvent(friendship.getId().getFirst()));
    }

    /**
     * Returns all the friendship connections from the repository
     * @return an iterable object containing all the friendships in repository
     */
    public Iterable<Friendship> findAllFriendships() {
        return friendshipRepository.findAll();
    }

    /**
     * Returns all the messages from the repository
     * @return an iterable object containing all the messages in repository
     */
    public Iterable<Message> findAllMessages() {
        return messageRepository.findAll();
    }
    /**
     * Return the largest connected component of the friendship network
     * @return the number of users in the largest component
     */
    public int nrOfCommunities() {
        Network network = new Network(userRepository.size());
        for (User user : userRepository.findAll()) {
            network.addUser(user);
        }
        return network.connectedComponents(friendshipRepository.findAll());
    }

    /**
     * Return the users which form the deepest tree in the friendship network
     * @return list containing all the users in the deepest tree
     */
    public List<User> largestCommunity() {
        Network network = new Network(userRepository.size());
        for (User user : userRepository.findAll()) {
            network.addUser(user);
        }
        return network.largestSocialCommunity(friendshipRepository.findAll());
    }

    /**
     * Creates and adds a new user to the user repository
     * @param args attributes of the new user
     */
    public void addUser(String[] args) {
        User user = new User(args[0], args[1],args[2], args[3], LocalDate.parse(args[4], DateFormatter.STANDARD_DATE_FORMAT));


        userRepository.save(user);
    }
    /**
     * Creates and adds a new message to the message repository
     * @param args attributes of the new message
     */
    public void addMessage(String[] args){

        //User sender = userRepository.findOne(Integer.parseInt(args[0]));
        User sender =userRepository.findOne(getLoggedInUser().getId());
        List<User> list = new ArrayList<>();
        int i=0;

            while (Integer.parseInt(args[i]) != 0) {
                User r = userRepository.findOne(Integer.parseInt(args[i]));
                if (r != null)
                    list.add(r);
                i++;
            }

        i++;
        Message msg = new Message(sender,list,args[i],LocalDate.now(),null);
        messageRepository.save(msg);
    }
    public Integer getLastMessage(){ return messageRepository.getMostRecentMessage();}

    public  Iterable<Message> Conversatie(Integer u1,Integer u2){
        if(u1 == null || u2 == null)throw new NullPointerException("id must not be null");
        User user1 = userRepository.findOne(u1);
        User user2 = userRepository.findOne(u2);
        if(user1 == null || user2 == null )throw new UserNotFoundException("user does not exist");

        List<Message> ar=messageRepository.conversatie(u1,u2);
        Collections.sort(ar, new Sortbydate());
        return ar;
    }
    /**
     * Removes a message based on his id
     * @param id id of the message to be removed
     */
    public void removeMessage(String id) {
        try {
            Message msg = messageRepository.findOne(Integer.parseInt(id));
            if (msg == null) {
                throw new MessageNotFoundException("Could not find message id");
            }
            Iterable<Message> messages = messageRepository.findAll();
            if (messages != null) {
                for (Message m : messages) {
                    if (m.getReply()!=null && m.getReply().getId()!=null)
                        if(m.getReply().getId().equals(Integer.parseInt(id))) {
                        //update reply sa fie null pt ca nu mai exista mesajul
                            Message ms= new Message(null,null,m.getMessage(),null,null);
                            ms.setId(m.getId());
                            messageRepository.modify(ms,false);
                    }
                }
            }
            messageRepository.remove(Integer.parseInt(id));
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }

    }
    public void modifyMessage(Integer id,String msg,Integer reply,Boolean all){
        if(id == null)throw new NullPointerException("id must not be null");
        Message m = messageRepository.findOne(id);
        if(m == null)throw new MessageNotFoundException("message not found");
        try {
            Message ms= new Message(null,null,msg,null,messageRepository.findOne(reply));
            ms.setId(id);

            messageRepository.modify(ms,all);

        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void removeUser(String id) {
        try {
            User user = userRepository.findOne(Integer.parseInt(id));
            if (user == null) {
                throw new UserNotFoundException("Could not find user id");
            }
            Iterable<Friendship> friendships = friendshipRepository.findAll();
            if (friendships != null) {
                for (Friendship friendship : friendships) {
                    if (friendship.getId().getFirst().getId().equals(user.getId()) || friendship.getId().getSecond().getId().equals(user.getId())) {
                        friendshipRepository.remove(friendship.getId());
                    }
                }
            }
            userRepository.remove(Integer.parseInt(id));
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Returns an iterable object containing all the users in the repository
     * @return iterable object containing all the users
     */
    public Iterable<User> findAllUsers() {
        return userRepository.findAll();
    }


    public Iterable<UserFriendDTO> findFriendsForUser(Integer userId) {
        return StreamSupport.stream(findAllFriendships().spliterator(), false)
                .filter(friendship -> friendship.getId().getFirst().getId().equals(userId) || friendship.getId().getSecond().getId().equals(userId))
                .map(friendship -> {
                    User friend;
                    if (friendship.getId().getFirst().getId().equals(userId)) {
                        friend = friendship.getId().getSecond();
                    }
                    else {
                        friend = friendship.getId().getFirst();
                    }
                    return new UserFriendDTO(friend.getFirstName(), friend.getLastName(), friendship.getDate());
                })
                .collect(Collectors.toList());
    }

    public Iterable<UserFriendDTO> findRelationsByMonth(int user, String mon) {
     return StreamSupport.stream(findAllFriendships().spliterator(),false)
             .filter(friendship->(friendship.getId().getFirst().getId().equals(user)|| friendship.getId().getSecond().getId().equals(user))&&friendship.getDate().getMonth().toString().equals(mon))
             .map(friendship->{
                 User friend;
                 if(friendship.getId().getFirst().equals(userRepository.findOne(user))){
                    friend = friendship.getId().getSecond();
                 }
                 else{
                     friend = friendship.getId().getFirst();
                 }
                 return new UserFriendDTO(friend.getFirstName(),friend.getLastName(),friendship.getDate());
             })
             .collect(Collectors.toList());
    }

    public boolean loginUser(String username,String password) throws NoSuchAlgorithmException {
        if (username == null || password == null) {
            return false;
        }
        //User foundUser = userRepository.findOne(userId);
        User foundUser = userRepository.getByUsername(username);
        if (foundUser == null ) {
            return false;
        }

        String salt =userRepository.getSalt(foundUser.getId());
        salt = salt + password;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(salt.getBytes(StandardCharsets.UTF_8));//-----------
        BigInteger no = new BigInteger(1, hash);
        String hashtext = no.toString(16);



        if(!foundUser.getPassword().equals(hashtext))
            return false;
        foundUser.setPassword(hashtext);
        loggedInUser = foundUser;
        return true;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public boolean sendFriendRequest(Integer friendId) {
        try {
            User friend = userRepository.findOne(friendId);
            if (friend == null) {
                throw new UserNotFoundException("User was not found");
            }
            FriendRequest friendRequest = new FriendRequest(new Tuple<>(loggedInUser, friend), "pending");
            if (friendRequestRepository.findOne(friendRequest.getId()) != null) {
                throw new DuplicateFriendshipException("This friend request already exists");
            }
            FriendRequest friendRequest1 = new FriendRequest(new Tuple<>(friend, loggedInUser), "pending");
            if (friendRequestRepository.findOne(friendRequest1.getId()) != null) {
                throw new DuplicateFriendshipException("This friend request already exists");
            }
            friendRequestRepository.save(friendRequest);
            return true;
        }
        catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    public List<FriendRequest> getPendingFriendRequests() {
        return StreamSupport.stream(friendRequestRepository.findAllForId(new Tuple<>(new User(), loggedInUser)).spliterator(), false)
                .filter(friendRequest -> friendRequest.getStatus().equals("pending"))
                .collect(Collectors.toList());
    }

    public Iterable<FriendRequest> getFriendRequests() {
        return friendRequestRepository.findAllForId(new Tuple<>(new User(), loggedInUser));
    }

    public Iterable<FriendRequest> getSentFriendRequests() {
        return StreamSupport.stream(friendRequestRepository.findAll().spliterator(), false)
                .filter(friendRequest -> friendRequest.getId().getFirst().equals(loggedInUser) && friendRequest.getStatus().equals("pending"))
                .collect(Collectors.toList());
    }

    public void acceptFriendRequest(Integer from) {
        try {
            User friend = userRepository.findOne(from);
            if (friend == null) {
                throw new UserNotFoundException("User was not found");
            }
            Tuple<User, User> requestId = new Tuple<>(friend, loggedInUser);
            if (!friendRequestRepository.findOne(requestId).getStatus().equals("pending")) {
                throw new ValidationException("Cannot accept request");
            }
            FriendRequest toAccept = new FriendRequest(requestId, "accepted");
            if (!friendRequestRepository.modify(toAccept)) {
                throw new ValidationException("Request could not be accepted");
            }
            else {
                friendshipRepository.save(new Friendship(toAccept.getId()));
                notifyObservers(new RemoveUserEvent(toAccept.getUser1())); //to change event
            }
        }
        catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void removeFriendRequest(FriendRequest friendRequest) {
        if (friendRequestRepository.findOne(friendRequest.getId()) != null) {
            friendRequestRepository.remove(friendRequest.getId());
        }
    }

    public void rejectFriendRequest(Integer from) {
        try {
            User friend = userRepository.findOne(from);
            if (friend == null) {
                throw new UserNotFoundException("User was not found");
            }
            Tuple<User, User> requestId = new Tuple<>(friend, loggedInUser);
            if (!friendRequestRepository.findOne(requestId).getStatus().equals("pending")) {
                throw new ValidationException("Cannot reject request");
            }
            FriendRequest toReject = new FriendRequest(requestId, "rejected");
            if (!friendRequestRepository.modify(toReject)) {
                throw new ValidationException("Request could not be rejected");
            }
        }
        catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private List<Observer<RemoveUserEvent>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<RemoveUserEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<RemoveUserEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(RemoveUserEvent t) {
        observers.forEach(x -> x.update(t));
    }

    public Iterable<User> findLoggedUsersFriends() {
        return StreamSupport.stream(findAllFriendships().spliterator(), false)
                .filter(friendship -> friendship.getId().getFirst().equals(loggedInUser) || friendship.getId().getSecond().equals(loggedInUser))
                .map(friendship -> {
                    if (friendship.getId().getFirst().equals(loggedInUser)) {
                        return friendship.getId().getSecond();
                    }
                    if (friendship.getId().getSecond().equals(loggedInUser)) {
                        return friendship.getId().getFirst();
                    }
                    return null;
                })
                .collect(Collectors.toList());
    }

    public Iterable<User> searchByName(String searchedName) {
        return StreamSupport.stream(findAllUsers().spliterator(), false)
                .filter(user -> (user.getFirstName().contains(searchedName) || user.getLastName().contains(searchedName)) &&
                        !user.equals(loggedInUser))
                .collect(Collectors.toList());
    }

    public Page createPageForUser(Integer id){
        if(id==null)throw new NullPointerException("id ul nu poate fi null");
        User user =userRepository.findOne(id);
        if(user == null)throw new UserNotFoundException("nu s-a gasit utilizatorul");
        List<User> fr=new ArrayList<User>(StreamSupport.stream(findLoggedUsersFriends().spliterator(), false).toList());
        List<FriendRequest> req =new ArrayList<FriendRequest>(StreamSupport.stream(getFriendRequests().spliterator(), false).toList());
        Page pagina = new Page(user.getFirstName(),user.getLastName(),fr,req);
        return pagina;
    }
   /* public Page updatePageForUser(Page pagina){
        List<User> fr=new ArrayList<User>(StreamSupport.stream(findLoggedUsersFriends().spliterator(), false).toList());
        List<FriendRequest> req =new ArrayList<FriendRequest>(StreamSupport.stream(getFriendRequests().spliterator(), false).toList());
        pagina.setCereriDePrietenie(req);
        pagina.setPrieteni(fr);
        return pagina;
    }*/

    public Iterable<Event> findNotifications() {
        return StreamSupport.stream(eventRepository.findUserEvents(loggedInUser.getId()).spliterator(), false)
                .filter(event -> (LocalDate.now().plusDays(5).isAfter(event.getEventDate())) && (LocalDate.now().isBefore(event.getEventDate()))).collect(Collectors.toList());
    }

    public Long getNumberOfNotifications() {
        return StreamSupport.stream(eventRepository.findUserEvents(loggedInUser.getId()).spliterator(), false)
                .filter(event -> (LocalDate.now().plusDays(5).isAfter(event.getEventDate())) && (LocalDate.now().isBefore(event.getEventDate()))).count();
    }

    public Iterable<Event> findSubscribedToEvents() {
        return eventRepository.findUserEvents(loggedInUser.getId());
    }

    public void subscribeUserToEvent(Integer eventId) {
        eventRepository.addUserToEvent(loggedInUser.getId(), eventId, true);
        notifyObservers(new RemoveUserEvent(loggedInUser));
    }

    public Iterable<Event> findAllEvents() {
        return eventRepository.findAll();
    }

    public void unsubscribeUserToEvent(Integer eventId) {
        eventRepository.unsubscribeToEvent(loggedInUser.getId(), eventId);
        notifyObservers(new RemoveUserEvent(loggedInUser));
    }

    public void addEvent(Event event) {
        eventRepository.save(event);
    }

    public Iterable<Message> getReceivedMessages(String username, LocalDate startDate, LocalDate endDate) {
        if(username==null)throw new NullPointerException("username can't be null");
        User u=userRepository.getByUsername(username);
        if(u==null||u==loggedInUser)throw new UserNotFoundException("user does not exist");
        if(startDate==null||endDate==null)throw new NullPointerException("date cannot be null");
        List<Message> ar=messageRepository.getReceivedMessagesPeriod(getLoggedInUser().getId(),u.getId(),startDate,endDate);
        Collections.sort(ar, new Sortbydate());
        return ar;
    }

    public void saveActivityReportToPDF(String path, String fileName, LocalDate startDate, LocalDate endDate) {
        if (!fileName.equals("") && !path.equals("")) {
            PDDocument document = new PDDocument();
            Path pathToFile = Paths.get(path, fileName);
            try {
                Iterable<User> friendships = getFriendshipsForActivityReport(startDate, endDate);
                Iterable<Message> messages = getMessagesForActivityReport(startDate, endDate);
                PDPage page1 = new PDPage();
                PDPage page2 = new PDPage();
                document.addPage(page1);
                document.addPage(page2);
                PDPageContentStream contentStream = new PDPageContentStream(document, page1);
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 16);
                contentStream.setLeading(14.5f);
                contentStream.beginText();
                contentStream.newLineAtOffset(25, 725);
                for (User user : friendships) {
                    contentStream.showText(user.toString());
                    contentStream.newLine();
                }
                contentStream.endText();
                contentStream.close();
                contentStream = new PDPageContentStream(document, page2);
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 16);
                contentStream.setLeading(14.5f);
                contentStream.beginText();
                contentStream.newLineAtOffset(25, 725);
                for (Message message : messages) {
                    contentStream.showText(message.toString());
                    contentStream.newLine();
                }
                contentStream.endText();
                contentStream.close();
                document.save(pathToFile.toString());
                document.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private Iterable<User> getFriendshipsForActivityReport(LocalDate startDate, LocalDate endDate) {
        return StreamSupport.stream(findAllFriendships().spliterator(), false)
                .filter(friendship -> (friendship.getId().getFirst().equals(loggedInUser) || friendship.getId().getSecond().equals(loggedInUser)) && friendship.getDate().isAfter(startDate) && friendship.getDate().isBefore(endDate))
                .map(friendship -> {
                    if (friendship.getId().getFirst().equals(loggedInUser)) {
                        return friendship.getId().getSecond();
                    }
                    if (friendship.getId().getSecond().equals(loggedInUser)) {
                        return friendship.getId().getFirst();
                    }
                    return null;
                })
                .collect(Collectors.toList());
    }

    private Iterable<Message> getMessagesForActivityReport(LocalDate startDate, LocalDate endDate) {
        Iterable<Message> allMessages = messageRepository.findAll();
        return StreamSupport.stream(allMessages.spliterator(), false)
                .filter(message -> message.getData().isAfter(startDate) && message.getData().isBefore(endDate) && message.getReceivers().contains(loggedInUser))
                .collect(Collectors.toList());
    }
    public void saveMessageReportToPDF(String path, String fileName, LocalDate startDate, LocalDate endDate,String username) {
        if (!fileName.equals("") && !path.equals("")) {
            PDDocument document = new PDDocument();
            Path pathToFile = Paths.get(path, fileName);
            try {
                Iterable<Message> messages =getReceivedMessages(username,startDate,endDate);
                PDPage page1 = new PDPage();
                document.addPage(page1);

                PDPageContentStream contentStream = new PDPageContentStream(document, page1);
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 16);
                contentStream.setLeading(14.5f);
                contentStream.beginText();
                contentStream.newLineAtOffset(25, 725);
                for (Message msg : messages) {
                    contentStream.showText(msg.toString());
                    contentStream.newLine();
                }
                contentStream.endText();
                contentStream.close();
                document.save(pathToFile.toString());
                document.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public int getNumberOfFriendsForLoggedInUser() {
        return friendshipRepository.findTotalSize(loggedInUser.getId());
    }
}
