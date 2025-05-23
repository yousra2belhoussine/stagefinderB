package ma.stagefinder.services;

import ma.stagefinder.dtos.UserDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    List<UserDTO> getUsers();
    UserDTO updateEstValide(Long id, boolean estValide);
    UserDTO updateUserProfile(Long userId, UserDTO userDTO) ;
    UserDTO getUserProfile(Long userId);
}
