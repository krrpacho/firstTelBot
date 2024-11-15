package com.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {
    private boolean screaming = false;
    private boolean lowering = false;
    
    @Override
    public String getBotUsername() {
        return "";
    }

    @Override
  public String getBotToken() {
    return "";
  }

    public void sendText(Long who, String what) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to
                .text(what).build();    //Message content
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

    private void scream(Long id, Message msg) {
        if (msg.hasText()) {
            sendText(id, msg.getText().toUpperCase());
        } else {
            copyMessage(id, msg.getMessageId());  
        }
    }

    private void lower(Long id, Message msg) {
        if (msg.hasText()) {
            sendText(id, msg.getText().toLowerCase());
        } else {
            copyMessage(id, msg.getMessageId());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message msg = update.getMessage();
        Long id = msg.getFrom().getId();

        //sendText(id, msg.getText());

        if (screaming) {                        
            scream(id, msg);                    
        } else if (lowering) {
            lower(id, msg);
        } else {
            copyMessage(id, msg.getMessageId()); 
        }

        if (msg.getText().startsWith("/")) {     
            if (msg.getText().equals("/scream")) {
                screaming = true;
            } if(msg.getText().equals("/lower")) {
                screaming = false;
                lowering = true;
            } else if (msg.getText().equals("/normal")) {
                screaming = false;
                lowering = false;
            }
            return;    
        }
    }

    public void copyMessage(Long who, Integer msgId) {
        CopyMessage cm = CopyMessage.builder()
                .fromChatId(who.toString())  // We copy from the user
                .chatId(who.toString())      // And send it back to him
                .messageId(msgId)            // Specifying what message
                .build();
        try {
            execute(cm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
