package com.example.labsocialnetworkv2;

import com.example.labsocialnetworkv2.application.Service;
import com.example.labsocialnetworkv2.domain.*;
import com.example.labsocialnetworkv2.presentation.UI;
import com.example.labsocialnetworkv2.repository.ConvRepository;
import com.example.labsocialnetworkv2.repository.ModifiableRepository;
import com.example.labsocialnetworkv2.repository.Repository;
import com.example.labsocialnetworkv2.repository.UsernameRepository;
import com.example.labsocialnetworkv2.repository.db.FriendRequestDbRepository;
import com.example.labsocialnetworkv2.repository.db.FriendshipDbRepository;
import com.example.labsocialnetworkv2.repository.db.MessagesDbRepository;
import com.example.labsocialnetworkv2.repository.db.UserDbRepository;

public class Main {
    /**
     * main program
     * @param args console line arguments
     */
    public static void main(String[] args) {
        UsernameRepository<Integer, User> userRepository = new UserDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "postgres");
        Repository<Tuple<User, User>, Friendship> friendshipRepository = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "postgres");
        ModifiableRepository<Tuple<User, User>, FriendRequest> friendRequestRepository = new FriendRequestDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "postgres");
        ConvRepository<Integer, Message> msgRepository = new MessagesDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "postgres");

        Service service = new Service(friendshipRepository, userRepository, msgRepository, friendRequestRepository);


       UI ui = new UI(service);
       ui.runUI();

//        System.out.println("printing msgs");
//        Repository<Integer, Message> msgRepository = new MessagesDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "postgres");
//        for(Message msg:msgRepository.findAll()){
//            System.out.println(msg);
//        }
//        System.out.println("done");
    }
}
