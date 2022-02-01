package com.bot.service;

import com.bot.entity.BotUser;
import com.bot.repository.BotUserRepository;
import org.jvnet.hk2.annotations.Service;
import org.springframework.stereotype.Component;

//@Service
@Component
public class BotUserService {

    private final BotUserRepository botUserRepository;

    public BotUserService(BotUserRepository botUserRepository) {
        this.botUserRepository = botUserRepository;
    }

    public void addUser(BotUser botUser){
        var botUsers= botUserRepository.findAll();
        if (botUsers.stream().anyMatch(user->user.getChatId().equals(botUser.getChatId()))){
            return;
        }
        botUserRepository.save(botUser);
    }

    public void updateLastCommand(String chatId, String newCommand){
        var botUsers= botUserRepository.findAll();
        botUsers.stream().filter(user->user.getChatId().equals(chatId))
                .forEach(user -> user.setLastCommand(newCommand));
    }

    public String getLastCommand(String chatId){
        var botUsers= botUserRepository.findAll();
        try {
            return botUsers.stream().filter(user -> user.getChatId().equals(chatId)).findFirst().get().getLastCommand();
        } catch (Exception e){
            return "";
        }
    }

    public int getNowInDialog(){
        var botUsers= botUserRepository.findAll();
        return (int) botUsers.stream().filter(botUser -> botUser.getLastCommand().equals("/next")).count();
    }

    public int getSize(){
        var botUsers= botUserRepository.findAll();
        return botUsers.size();
    }
}
