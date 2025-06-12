package ma.stagefinder.services;

import lombok.AllArgsConstructor;
import ma.stagefinder.dtos.UserDTO;
import ma.stagefinder.entities.User;
import ma.stagefinder.mapper.EntityMapper;
import ma.stagefinder.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private EntityMapper entityMapper;
    private FileStorageService fileStorageService;

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

    public UserDTO createUser(UserDTO userDTO) throws IOException {
        User user = entityMapper.toUser(userDTO);

        // Gérer l'upload des fichiers
        if (userDTO.getCvFile() != null && !userDTO.getCvFile().isEmpty()) {
            String cvFilename = fileStorageService.storeFile(userDTO.getCvFile(), "cvFile");
            user.setCvFile(cvFilename);
        }
        if (userDTO.getImage() != null && !userDTO.getImage().isEmpty()) {
            String imageFilename = fileStorageService.storeFile(userDTO.getImage(), "image");
            user.setImage(imageFilename);
        }

        User savedUser = userRepository.save(user);
        return entityMapper.toUserDTO(savedUser);
    }

    @Override
    public UserDTO updateUserProfile(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + userId));

        // Mettre à jour les champs modifiables
        user.setNom(userDTO.getNom());
        user.setEmail(userDTO.getEmail());
        user.setAdresse(userDTO.getAdresse());
        user.setTel(userDTO.getTel());
        // Ajoute d'autres champs si nécessaire (ex. mot de passe, photo)

        User updatedUser = userRepository.save(user);
        return entityMapper.toUserDTO(updatedUser);
    }

    @Override
    public UserDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + userId));
        UserDTO userDTO = entityMapper.toUserDTO(user);
        return userDTO;
    }

    // Nouvelle méthode pour le login
    public UserDTO login(String email, String password) {
        User user = userRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new RuntimeException("Email ou mot de passe incorrect"));
        if (!user.isEstValide()) {
            throw new RuntimeException("Compte non validé");
        }
        return entityMapper.toUserDTO(user);
    }
}