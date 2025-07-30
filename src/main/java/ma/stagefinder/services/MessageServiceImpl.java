package ma.stagefinder.services;

import lombok.AllArgsConstructor;
import ma.stagefinder.dtos.MessageDTO;
import ma.stagefinder.entities.Message;
import ma.stagefinder.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Transactional
    public MessageDTO sendMessage(MessageDTO messageDTO) {
        System.out.println("Sending message: " + messageDTO);
        // Créer une entité Message
        Message message = new Message();
        message.setSenderId(messageDTO.getSenderId());
        message.setRecipientId(messageDTO.getRecipientId());
        message.setContent(messageDTO.getContent());
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);

        // Sauvegarder dans la base de données
        Message savedMessage = messageRepository.save(message);

        // Convertir en DTO
        MessageDTO savedMessageDTO = new MessageDTO();
        savedMessageDTO.setId(savedMessage.getId());
        savedMessageDTO.setSenderId(savedMessage.getSenderId());
        savedMessageDTO.setRecipientId(savedMessage.getRecipientId());
        savedMessageDTO.setContent(savedMessage.getContent());
        savedMessageDTO.setTimestamp(savedMessage.getTimestamp());
        savedMessageDTO.setRead(savedMessage.isRead());

        // Envoyer le message au destinataire via WebSocket
        messagingTemplate.convertAndSendToUser(
                String.valueOf(messageDTO.getRecipientId()),
                "/queue/messages",
                savedMessageDTO
        );

        return savedMessageDTO;
    }

    public List<MessageDTO> getConversation(Long userId1, Long userId2) {
        List<Message> messages = messageRepository.findBySenderIdAndRecipientIdOrSenderIdAndRecipientIdOrderByTimestamp(
                userId1, userId2, userId2, userId1);
        return messages.stream().map(message -> {
            MessageDTO dto = new MessageDTO();
            dto.setId(message.getId());
            dto.setSenderId(message.getSenderId());
            dto.setRecipientId(message.getRecipientId());
            dto.setContent(message.getContent());
            dto.setTimestamp(message.getTimestamp());
            dto.setRead(message.isRead());
            return dto;
        }).collect(Collectors.toList());
    }
}
