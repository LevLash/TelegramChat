package com.bot.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class BotUser  implements Serializable {
    @Id
    private String chatId;
    private String bot_user;
    private String lastCommand;
    
    public BotUser(String chatId, String user, String lastCommand) {
        this.chatId = chatId;
        this.bot_user = user;
        this.lastCommand = lastCommand;
    }

    public BotUser() {}
    
    public String getChatId() {
        return chatId;
    }
    
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
    
    public String getBot_user() {
        return bot_user;
    }
    
    public void setBot_user(String bot_user) {
        this.bot_user = bot_user;
    }
    
    public String getLastCommand() {
        return lastCommand;
    }
    
    public void setLastCommand(String lastCommand) {
        this.lastCommand = lastCommand;
    }
}
