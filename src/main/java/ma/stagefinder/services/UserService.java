package ma.stagefinder.services;

import ma.stagefinder.dtos.UserDTO;

import java.util.List;

public interface UserService {
    List<UserDTO> getUsers();
    UserDTO updateEstValide(Long id, boolean estValide);
}
