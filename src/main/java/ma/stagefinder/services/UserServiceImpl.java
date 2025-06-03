package ma.stagefinder.services;

import lombok.RequiredArgsConstructor;
import ma.stagefinder.dtos.UserDTO;
import ma.stagefinder.entities.User;
import ma.stagefinder.entities.enums.Role;
import ma.stagefinder.mapper.EntityMapper;
import ma.stagefinder.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final EntityMapper entityMapper;
  private final FileStorageService fileStorageService;

  @Override
  public long count() {
    return userRepository.count();
  }

  @Override
  public long countByRole(Role role) {
    return userRepository.countByRole(role);
  }

  @Override
  public UserDTO create(UserDTO dto) {
    User user = entityMapper.toUser(dto);
    User saved = userRepository.save(user);
    return entityMapper.toUserDTO(saved);
  }

  @Override
  public UserDTO update(Long id, UserDTO dto) {
    User user = userRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("User not found"));

    if (dto.getNom() != null) user.setNom(dto.getNom());
    if (dto.getEmail() != null) user.setEmail(dto.getEmail());
    if (dto.getTel() != null) user.setTel(dto.getTel());
    if (dto.getRole() != null) user.setRole(dto.getRole());

    // Ne remplacer l'image et le cv que s'ils sont envoyés
    if (dto.getImage() != null && !dto.getImage().isEmpty()) {
      user.setImage(dto.getImage());
    }

    if (dto.getCvFile() != null && !dto.getCvFile().isEmpty()) {
      user.setCvFile(dto.getCvFile());
    }

    // Mise à jour du champ estValide
    user.setEstValide(dto.isEstValide());

    return entityMapper.toUserDTO(userRepository.save(user));
  }
  @Override
  public UserDTO updateWithFiles(Long id, UserDTO dto, MultipartFile cv, MultipartFile image) throws IOException {
    if (cv != null && !cv.isEmpty()) {
      String cvFileName = fileStorageService.storeFile(cv, "cv");
      dto.setCvFile(cvFileName);
    }

    if (image != null && !image.isEmpty()) {
      String imageFileName = fileStorageService.storeFile(image, "image");
      dto.setImage(imageFileName);
    }

    return update(id, dto); // réutilise la méthode existante
  }




  @Override
  public UserDTO
   partialUpdate(Long id, UserDTO dto) {
    User user = userRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("User not found"));

    if (dto.getNom() != null) user.setNom(dto.getNom());
    if (dto.getTel() != null) user.setTel(dto.getTel());
    if (dto.getEmail() != null) user.setEmail(dto.getEmail());
    if (dto.getImage() != null) user.setImage(dto.getImage());
    if (dto.getCvFile() != null) user.setCvFile(dto.getCvFile());

    return entityMapper.toUserDTO(userRepository.save(user));
  }

  @Override
  public UserDTO updateEstValide(Long id, boolean estValide) {
    User user = userRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("User not found"));
    user.setEstValide(estValide);
    return entityMapper.toUserDTO(userRepository.save(user));
  }


  /* @Override
 public UserDTO updateUserProfile(Long userId, UserDTO dto) {
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new RuntimeException("User not found"));
    if (dto.getNom() != null) user.setNom(dto.getNom());
    if (dto.getTel() != null) user.setTel(dto.getTel());
    if (dto.getImage() != null) user.setImage(dto.getImage());
    if (dto.getCvFile() != null) user.setCvFile(dto.getCvFile());
    return entityMapper.toUserDTO(userRepository.save(user));
  }*/

  @Override
  public UserDTO getUserProfile(Long userId) {
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new RuntimeException("User not found"));
    return entityMapper.toUserDTO(user);
  }

  @Override
  public UserDTO getById(Long id) {
    return entityMapper.toUserDTO(userRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("User not found")));
  }

  @Override
  public List<UserDTO> getAll() {
    return userRepository.findAll().stream()
      .map(entityMapper::toUserDTO)
      .collect(Collectors.toList());
  }

  @Override
  public void delete(Long id) {
    userRepository.deleteById(id);
  }

  @Override
  public void deleteAll() {
    userRepository.deleteAll();
  }

  @Override
  public List<UserDTO> getUsers() {
    return getAll();
  }

  // Méthode pour stocker un fichier via FileStorageService
  @Override
  public String storeFile(MultipartFile file, String type) throws IOException {
    return fileStorageService.storeFile(file, type);
  }

}
