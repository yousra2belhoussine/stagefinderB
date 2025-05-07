package ma.stagefinder.services;

import lombok.AllArgsConstructor;
import ma.stagefinder.dtos.UserDTO;
import ma.stagefinder.entities.User;
import ma.stagefinder.mapper.EntityMapper;
import ma.stagefinder.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private EntityMapper entityMapper;

    @Override
    public List<UserDTO> getUsers() {
        return userRepository.findAll().stream()
                .map(entityMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO updateEstValide(Long id, boolean estValide) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEstValide(estValide);
            User updatedUser = userRepository.save(user);
            return entityMapper.toUserDTO(updatedUser);
        } else {
            throw new RuntimeException("Utilisateur avec l'ID " + id + " non trouvé");
        }
    }
}
