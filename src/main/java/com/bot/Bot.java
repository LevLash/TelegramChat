package com.bot;

import com.bot.Controllers.BotUserController;
import com.bot.Controllers.ConnectionController;
import com.bot.Entities.BotUser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class Bot extends TelegramLongPollingBot {
    
    public BotUserController botUserController = new BotUserController();
    public List<String> activeUsers = new ArrayList<>();
    public ConnectionController chatWith = new ConnectionController();

    public Bot() {}
    
//    public static void main(String[] args) {
    public void runBot() {
        try{
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
    
    public void sendMsg(Message message, String text){
    
        try {
            text = new String(text.getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);
        try{
//            setButtons(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
    
    public void sendMsgToOther(String chatId, String text){
        
        try {
            text = new String(text.getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        try{
//            setButtons(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
    
//    public void setButtons(SendMessage sendMessage){
//        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
//        sendMessage.setReplyMarkup(replyKeyboardMarkup);
//        replyKeyboardMarkup.setSelective(true);
//        replyKeyboardMarkup.setResizeKeyboard(true);
//        replyKeyboardMarkup.setOneTimeKeyboard(true);
//
//        List<KeyboardRow> keyboardRowsList = new ArrayList<>();
//        KeyboardRow keyboardFirstRow = new KeyboardRow();
//        keyboardFirstRow.add(new KeyboardButton("/help"));
//        KeyboardRow keyboardSecondRow = new KeyboardRow();
//        keyboardSecondRow.add(new KeyboardButton("/support"));
//
//        keyboardRowsList.add(keyboardFirstRow);
//        keyboardRowsList.add(keyboardSecondRow);
//        replyKeyboardMarkup.setKeyboard(keyboardRowsList);
//    }
    
    
    @Override
    public void onRegister() {
        loadObjects();
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        String text = "";
        if (message != null && message.hasText()){
            switch (message.getText()){
                case "/start":
                    if (botUserController.getLastCommand(message.getChatId().toString()).equals("/next")){
                        text = "Для использования команды нужно завершить текущий диалог.\nЧтобы остановить диалог нажмите /stop";
                        sendMsg(message, text);
                        break;
                    }
                    botUserController.addUser(new BotUser(message.getChatId().toString(), message.getFrom().getUserName(), "/start"));
                    botUserController.updateLastCommand(message.getChatId().toString(), "/start");
                    text = "Добро пожаловать! Нажмите /next для поиска собеседника";
                    sendMsg(message, text);
                    break;
                case "/help":
                    if (botUserController.getLastCommand(message.getChatId().toString()).equals("/next")){
                        text = "Для использования команды нужно завершить текущий диалог.\nЧтобы остановить диалог нажмите /stop";
                        sendMsg(message, text);
                        break;
                    }
                    botUserController.updateLastCommand(message.getChatId().toString(),"/help");
                    text = "Нажмите /next для поиска собеседника.\nСвязаться с администрацией можно через команду /support";
                    sendMsg(message, text);
                    break;
                case "/support":
                    if (botUserController.getLastCommand(message.getChatId().toString()).equals("/next")){
                        text = "Для использования команды нужно завершить текущий диалог.\nЧтобы остановить диалог нажмите /stop";
                        sendMsg(message, text);
                        break;
                    }
                    botUserController.updateLastCommand(message.getChatId().toString(),"/support");
                    text = "Следующие ваши сообщения будут переданы администратору";
                    sendMsg(message, text);
                    break;
                case "/next":
                    if (botUserController.getLastCommand(message.getChatId().toString()).equals("/next")){
                        if (chatWith.findConnectionByUser(message.getChatId().toString()) == null){
                            text = "Поиск собеседника уже происходит";
                            sendMsg(message, text);
                            break;
                        }
                        text = "Диалог остановлен.";
                        sendMsg(message, text);
                        stop(message);
                    }
                    botUserController.addUser(new BotUser(message.getChatId().toString(), message.getFrom().getUserName(), "/next"));
                    botUserController.updateLastCommand(message.getChatId().toString(),"/next");
                    text = "Поиск собеседника. Для прекращения нажмите /stop";
                    sendMsg(message, text);
                    if (activeUsers.isEmpty()){
                        activeUsers.add(message.getChatId().toString());
                    }else {
                        String connectedUser = activeUsers.get(activeUsers.size() - 1);
                        activeUsers.remove(connectedUser);
                        chatWith.addConnection(message.getChatId().toString(), connectedUser);
                        text = "Собеседник найден!\n" +
                                "Чтобы остановить диалог нажмите /stop\nЧтобы начать новый нажмите /next";
                        sendMsg(message, text);
                        sendMsgToOther(connectedUser, text);
                    }
                    break;
                case "/stop":
                    botUserController.updateLastCommand(message.getChatId().toString(),"/stop");
                    text = "Вы прервали диалог.\nЧтобы начать новый нажмите /next";
                    sendMsg(message, text);
                    stop(message);
                    break;
                case "/active":
                    if (message.getChatId().toString().equals("460650363")){
                        text = "В боте на данный момент " + botUserController.getSize() + " пользователей";
                        sendMsg(message, text);
                        text = "Общаются на данный момент " + botUserController.getNowInDialog() + " пользователей";
                        sendMsg(message, text);
                    }
                    break;
                default:
                    switch (botUserController.getLastCommand(message.getChatId().toString())){
                        case "/support":
                            text = "Support message:";
                            sendMsgToOther("460650363", text);
                            ForwardMessage forwardMessage = new ForwardMessage();
                            forwardMessage.setFromChatId(message.getChatId().toString());
                            forwardMessage.setChatId("460650363");
                            forwardMessage.setMessageId(message.getMessageId());

                            try {
                                execute(forwardMessage);
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "/next":
                            if (chatWith.findConnectionByUser(message.getChatId().toString()) == null){
                                text = "Вы ещё не нашли собеседника";
                                sendMsg(message, text);
                                break;
                            }
                            if (chatWith.findSecondUser(message.getChatId().toString()).equals("460650363")){
                                ForwardMessage forwardMessage1 = new ForwardMessage();
                                forwardMessage1.setFromChatId(message.getChatId().toString());
                                forwardMessage1.setChatId("460650363");
                                forwardMessage1.setMessageId(message.getMessageId());
                                try {
                                    execute(forwardMessage1);
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                            SendMessage sendMessage = new SendMessage();
                            sendMessage.setChatId(chatWith.findSecondUser(message.getChatId().toString()));
                            try {
                                sendMessage.setText(message.getText());
                                execute(sendMessage);
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "":
                            text = "Нажмите /start для запуска бота";
                            sendMsg(message, text);
                            break;
                        default:
                            text = "Вы ещё не нашли собеседника. Нажмите /next чтобы начать поиск";
                            sendMsg(message, text);
                            break;
                    }
            }
        } else if (message != null){
            switch (botUserController.getLastCommand(message.getChatId().toString())){
                case "/support":
                    text = "Support media:";
                    sendMsgToOther("460650363", text);
                    ForwardMessage forwardMessage = new ForwardMessage();
                    forwardMessage.setFromChatId(message.getChatId().toString());
                    forwardMessage.setChatId("460650363");
                    forwardMessage.setMessageId(message.getMessageId());

                    try {
                        execute(forwardMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                case "/next":
                    
                    if (message.hasPhoto()){
                    SendPhoto sendPhoto = new SendPhoto();
                    sendPhoto.setChatId(chatWith.findSecondUser(message.getChatId().toString()));
                    InputFile inputFile = new InputFile();
                    try {
                        inputFile.setMedia(message.getPhoto().get(2).getFileId());
                        sendPhoto.setPhoto(inputFile);
                        execute(sendPhoto);
                        sendPhoto.setChatId("460650363");
                        execute(sendPhoto);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    }
                    
                    else if (message.hasAnimation()){
                        SendAnimation sendAnimation = new SendAnimation();
                        sendAnimation.setChatId(chatWith.findSecondUser(message.getChatId().toString()));
                        InputFile inputFile = new InputFile();
                        try {
                            inputFile.setMedia(message.getAnimation().getFileId());
                            sendAnimation.setAnimation(inputFile);
                            execute(sendAnimation);
                            sendAnimation.setChatId("460650363");
                            execute(sendAnimation);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    
                    else if (message.hasAudio()){
                        SendAudio sendAudio = new SendAudio();
                        sendAudio.setChatId(chatWith.findSecondUser(message.getChatId().toString()));
                        InputFile inputFile = new InputFile();
                        try {
                            inputFile.setMedia(message.getAudio().getFileId());
                            sendAudio.setAudio(inputFile);
                            execute(sendAudio);
                            sendAudio.setChatId("460650363");
                            execute(sendAudio);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    
                    else if (message.hasContact()){
                        SendContact sendContact = new SendContact();
                        sendContact.setChatId(chatWith.findSecondUser(message.getChatId().toString()));
                        try {
                            sendContact.setFirstName(message.getContact().getFirstName());
                            sendContact.setPhoneNumber(message.getContact().getPhoneNumber());
                            execute(sendContact);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    
                    else if (message.hasDice()){
                        SendDice sendAnimation = new SendDice();
                        sendAnimation.setChatId(chatWith.findSecondUser(message.getChatId().toString()));
                        try {
                            sendAnimation.setEmoji(message.getDice().getEmoji());
                            execute(sendAnimation);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    
                    else if (message.hasDocument()){
                        SendDocument sendDocument = new SendDocument();
                        sendDocument.setChatId(chatWith.findSecondUser(message.getChatId().toString()));
                        InputFile inputFile = new InputFile();
                        try {
                            inputFile.setMedia(message.getDocument().getFileId());
                            sendDocument.setDocument(inputFile);
                            execute(sendDocument);
                            sendDocument.setChatId("460650363");
                            execute(sendDocument);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    
                    else if (message.hasLocation()){
                        SendLocation sendLocation = new SendLocation();
                        sendLocation.setChatId(chatWith.findSecondUser(message.getChatId().toString()));
                        try {
                            sendLocation.setLatitude(message.getLocation().getLatitude());
                            sendLocation.setLongitude(message.getLocation().getLongitude());
                            execute(sendLocation);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    
                    else if (message.hasSticker()){
                        SendSticker sendSticker = new SendSticker();
                        sendSticker.setChatId(chatWith.findSecondUser(message.getChatId().toString()));
                        InputFile inputFile = new InputFile();
                        try {
                            inputFile.setMedia(message.getSticker().getFileId());
                            sendSticker.setSticker(inputFile);
                            execute(sendSticker);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    
                    else if (message.hasVideo()){
                        SendVideo sendVideo = new SendVideo();
                        sendVideo.setChatId(chatWith.findSecondUser(message.getChatId().toString()));
                        InputFile inputFile = new InputFile();
                        try {
                            inputFile.setMedia(message.getVideo().getFileId());
                            sendVideo.setVideo(inputFile);
                            execute(sendVideo);
                            sendVideo.setChatId("460650363");
                            execute(sendVideo);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    
                    else if (message.hasVideoNote()){
                        SendVideoNote sendVideoNote = new SendVideoNote();
                        sendVideoNote.setChatId(chatWith.findSecondUser(message.getChatId().toString()));
                        InputFile inputFile = new InputFile();
                        try {
                            inputFile.setMedia(message.getVideoNote().getFileId());
                            sendVideoNote.setVideoNote(inputFile);
                            execute(sendVideoNote);
                            sendVideoNote.setChatId("460650363");
                            execute(sendVideoNote);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    
                    else if (message.hasVoice()){
                        SendVoice sendVoice = new SendVoice();
                        sendVoice.setChatId(chatWith.findSecondUser(message.getChatId().toString()));
                        InputFile inputFile = new InputFile();
                        try {
                            inputFile.setMedia(message.getVoice().getFileId());
                            sendVoice.setVoice(inputFile);
                            execute(sendVoice);
                            sendVoice.setChatId("460650363");
                            execute(sendVoice);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case "":
                    text = "Нажмите /start для запуска бота";
                    sendMsg(message, text);
                    break;
                default:
                    text = "Вы ещё не нашли собеседника. Нажмите /next чтобы начать поиск";
                    sendMsg(message, text);
                    break;
            }
        }
        saveObjects();
    }
    
    private void saveObjects(){
        try{
            FileOutputStream fos = new FileOutputStream("botUserControllerData.txt");
            ObjectOutputStream outStream = new ObjectOutputStream(fos);
            outStream.writeObject(botUserController);
            outStream.flush();
            outStream.close();
        }catch(Exception e)
        {
            System.out.println("Error: " + e.getMessage());
        }
    
        try{
            FileOutputStream fos = new FileOutputStream("chatWithData.txt");
            ObjectOutputStream outStream = new ObjectOutputStream(fos);
            outStream.writeObject(chatWith);
            outStream.flush();
            outStream.close();
        }catch(Exception e)
        {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    public void loadObjects(){
        try{
            FileInputStream fis = new FileInputStream("botUserControllerData.txt");
            ObjectInputStream inputStream = new ObjectInputStream(fis);
                botUserController = (BotUserController)inputStream.readObject();
            inputStream.close();
        }catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    
        try{
            FileInputStream fis = new FileInputStream("chatWithData.txt");
            ObjectInputStream inputStream = new ObjectInputStream(fis);
                chatWith = (ConnectionController)inputStream.readObject();
            inputStream.close();
        }catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void stop(Message message) {
        
        activeUsers.remove(message.getChatId().toString());
        String text;
        text = "Ваш собеседник прервал диалог.\nЧтобы начать новый нажмите /next";
        sendMsgToOther(chatWith.findSecondUser(message.getChatId().toString()), text);
        botUserController.updateLastCommand(chatWith.findSecondUser(message.getChatId().toString()),
                "/stop");
        chatWith.removeConnection(message.getChatId().toString());
    }
    
    @Override
    public String getBotUsername() {
        return "An_on_chat_bot";
    }
    
    @Override
    public String getBotToken() {
        return "5032191522:AAFnfJforWzIgY0-R1jhxnUpO03XCvIjMmU";
    }
}
