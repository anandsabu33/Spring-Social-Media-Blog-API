package com.example.service;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, AccountRepository accountRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }

    
    public Message createMessage(Message message) throws IllegalArgumentException {
        
        if (message.getMessageText() == null || message.getMessageText().trim().isEmpty()) {
            throw new IllegalArgumentException("Message text cannot be blank.");
        }
        if (message.getMessageText().length() > 255) {
            throw new IllegalArgumentException("Message text cannot exceed 255 characters.");
        }

        Optional<Account> accountOpt = accountRepository.findById(message.getPostedBy());
        if (!accountOpt.isPresent()) {
            throw new IllegalArgumentException("PostedBy user does not exist.");
        }

        if (message.getTimePostedEpoch() == null) {
            message.setTimePostedEpoch(System.currentTimeMillis() / 1000L);
        }

        return messageRepository.save(message);
    }

    
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    
    public Optional<Message> getMessageById(Integer messageId) {
        return messageRepository.findById(messageId);
    }

    
    public int deleteMessage(Integer messageId) {
        if (messageRepository.existsById(messageId)) {
            messageRepository.deleteById(messageId);
            return 1;
        }
        return 0;
    }

    public boolean updateMessage(Integer messageId, String newText) throws IllegalArgumentException {
        if (newText == null || newText.trim().isEmpty()) {
            throw new IllegalArgumentException("New message text cannot be blank.");
        }
        if (newText.length() > 255) {
            throw new IllegalArgumentException("New message text cannot exceed 255 characters.");
        }
    
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (!messageOpt.isPresent()) {
            return false; 
        }
    
        Message message = messageOpt.get();
        message.setMessageText(newText);
        messageRepository.save(message);
        return true; 
    }

    public List<Message> getMessagesByUser(Integer accountId) {
        return messageRepository.findByPostedBy(accountId);
    }
}