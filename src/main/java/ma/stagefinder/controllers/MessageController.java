package ma.stagefinder.controllers;

import ma.stagefinder.dtos.MessageDTO;
import ma.stagefinder.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @MessageMapping("/chat/{recipientId}")
    public void sendMessage(@DestinationVariable Long recipientId,
                            @Payload MessageDTO messageDTO,
                            Principal principal) {
        System.out.println("Principal name: " + principal.getName());
        Long senderId = Long.valueOf(principal.getName()); // Now correctly user ID
        messageDTO.setSenderId(senderId);
        messageDTO.setRecipientId(recipientId);
        messageService.sendMessage(messageDTO);
    }

    @SubscribeMapping("/queue/conversation/{otherUserId}")
    public List<MessageDTO> getConversation(@DestinationVariable Long otherUserId, Principal principal) {
        System.out.println("Fetching conversation for user: " + principal.getName() + ", otherUserId: " + otherUserId);
        Long userId = Long.valueOf(principal.getName());
        return messageService.getConversation(userId, otherUserId);
    }
}