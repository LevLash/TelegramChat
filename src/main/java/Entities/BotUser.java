package Entities;

import lombok.Data;

import java.io.Serializable;

public class BotUser  implements Serializable {
    private String chatId;
    private String user;
    private String lastCommand;
    
    public BotUser(String chatId, String user, String lastCommand) {
        this.chatId = chatId;
        this.user = user;
        this.lastCommand = lastCommand;
    }
    
    public String getChatId() {
        return chatId;
    }
    
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
    
    public String getUser() {
        return user;
    }
    
    public void setUser(String user) {
        this.user = user;
    }
    
    public String getLastCommand() {
        return lastCommand;
    }
    
    public void setLastCommand(String lastCommand) {
        this.lastCommand = lastCommand;
    }
}
