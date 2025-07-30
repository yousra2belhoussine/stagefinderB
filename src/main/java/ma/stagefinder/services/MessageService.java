package ma.stagefinder.services;

import ma.stagefinder.dtos.MessageDTO;

import java.util.List;

public interface MessageService {
    MessageDTO sendMessage(MessageDTO messageDTO);
    List<MessageDTO> getConversation(Long userId1, Long userId2);
}
