package com.bot.service;

import com.bot.entity.Connection;
import com.bot.repository.BotConnectionRepository;
import org.jvnet.hk2.annotations.Service;
import org.springframework.stereotype.Component;

//@Service
@Component
public class BotConnectionService {

    private final BotConnectionRepository botConnectionRepository;

    public BotConnectionService(BotConnectionRepository botConnectionRepository) {
        this.botConnectionRepository = botConnectionRepository;
    }

    public void addConnection(String user1, String user2){
        var connections = botConnectionRepository.findAll();
        connections.add(new Connection(user1, user2));
    }

    public Connection findConnectionByUser(String userId){
        try {
            var connections = botConnectionRepository.findAll();
            return connections.stream().filter(connection -> connection.getUser1().equals(userId)
                    || connection.getUser2().equals(userId)).findFirst().get();
        } catch (Exception e){
            return null;
        }
    }

    public String findSecondUser(String userId){
        Connection connection = findConnectionByUser(userId);
        if (connection.getUser1().equals(userId)){
            return connection.getUser2();
        } else {
            return connection.getUser1();
        }
    }

    public void removeConnection(String userId){
        var connections = botConnectionRepository.findAll();
        connections.remove(findConnectionByUser(userId));
    }

    public void saveConnection(Connection connection) {
        botConnectionRepository.save(connection);
    }
}
