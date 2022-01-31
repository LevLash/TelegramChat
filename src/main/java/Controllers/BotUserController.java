package Controllers;

import Entities.BotUser;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BotUserController implements Serializable {
    private final List<BotUser> botUsers = new ArrayList<>();
    
    public void addUser(BotUser botUser){
            if (botUsers.stream().anyMatch(user->user.getChatId().equals(botUser.getChatId()))){
                return;
            }
    botUsers.add(botUser);
    }
    
    public void updateLastCommand (String chatId, String newCommand){
        botUsers.stream().filter(user->user.getChatId().equals(chatId))
                .forEach(user -> user.setLastCommand(newCommand));
    }
    
    public String getLastCommand(String chatId){
        try {
            return botUsers.stream().filter(user -> user.getChatId().equals(chatId)).findFirst().get().getLastCommand();
        } catch (Exception e){
            return "";
        }
    }
    
    public int getNowInDialog(){
        return (int) botUsers.stream().filter(botUser -> botUser.getLastCommand().equals("/next")).count();
    }
    
    public int getSize(){
       return botUsers.size();
    }
    
    
}
