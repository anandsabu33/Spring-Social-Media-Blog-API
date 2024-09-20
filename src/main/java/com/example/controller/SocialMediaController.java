package com.example.controller;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

//import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@RestController
//@RequestMapping("/api")
public class SocialMediaController {

    private final AccountService accountService;
    private final MessageService messageService;

    @Autowired
    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    //User Registration
    @PostMapping("/register")
    public ResponseEntity<Account> register(@RequestBody Account account) throws IllegalAccessException {
        try {
            Account createdAccount = accountService.register(account);
            return ResponseEntity.ok(createdAccount);  
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("Username already exists.")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();  
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();  
        }
    }

    //Login
    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Account loginRequest) {
        Optional<Account> accountOptional = accountService.login(loginRequest.getUsername(), loginRequest.getPassword());
        if (accountOptional.isPresent()) {
            return ResponseEntity.ok(accountOptional.get());  
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();  
        }
    }

    //Create New Message
    @PostMapping("/messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        try {
            Message createdMessage = messageService.createMessage(message);
            return ResponseEntity.ok(createdMessage); 
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();  
        }
    }

    //Get All Messages
    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageService.getAllMessages();
        return ResponseEntity.ok(messages);  
    }
    
    //Get One Message Given Message Id
    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable Integer messageId) {
        Optional<Message> messageOptional = messageService.getMessageById(messageId);
        if (messageOptional.isPresent()) {
            return ResponseEntity.ok(messageOptional.get());  
        } else {
            return ResponseEntity.status(200).build();  
        }
    }

    //Delete a Message Given Message Id
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<String> deleteMessage(@PathVariable Integer messageId) {
        int rowsDeleted = messageService.deleteMessage(messageId);
        if (rowsDeleted == 1) {
            return ResponseEntity.ok("1");
        } else {
            return ResponseEntity.status(200).build();  
        }
    }

    //Update Message Given Message Id
    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<String> updateMessage(@PathVariable Integer messageId, @RequestBody Message newMessage) {
        if (newMessage == null || newMessage.getMessageText() == null || newMessage.getMessageText().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New message text cannot be blank.");
        }

        try {
            boolean updated = messageService.updateMessage(messageId, newMessage.getMessageText());
            if (updated) {
             return ResponseEntity.ok("1");
            } else {
                return ResponseEntity.status(400).build(); 
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    //Get All Messages From User Given Account Id
    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getMessagesByUser(@PathVariable Integer accountId) {
        List<Message> messages = messageService.getMessagesByUser(accountId);
        return ResponseEntity.ok(messages);  
    }
}