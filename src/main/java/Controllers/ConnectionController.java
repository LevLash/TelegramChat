package Controllers;

import Entities.Connection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConnectionController implements Serializable {
    private final List<Connection> connections = new ArrayList<>();
    
    public void addConnection(String user1, String user2){
        connections.add(new Connection(user1, user2));
    }
    
    public Connection findConnectionByUser(String userId){
        try {
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
        connections.remove(findConnectionByUser(userId));
    }
}
