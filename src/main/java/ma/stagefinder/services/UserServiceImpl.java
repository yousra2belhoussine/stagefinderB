package ma.stagefinder.services;

import ma.stagefinder.dtos.UserDTO;
import ma.stagefinder.entities.User;
import ma.stagefinder.entities.enums.Role;
import ma.stagefinder.mapper.EntityMapper;
import ma.stagefinder.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private EntityMapper entityMapper;

  @Override
  public UserDTO create(UserDTO dto) {
    User user = entityMapper.toUser(dto);
    // System.out.println("💡 Lettre dans DTO avant save = " + dto.getLettreMotivationFile());
    //System.out.println("💡 Lettre dans entité après mappage = " + user.getLettreMotivationFile());
    User savedUser = userRepository.save(user);
    return entityMapper.toUserDTO(savedUser);
  }
  @Override
  public long count() {
    return userRepository.count();
  }

  @Override
  public long countByRole(Role role) {
    return userRepository.countByRole(role);
  }

  @Override
  public UserDTO update(Long id, UserDTO dto) {
    Optional<User> optionalUser = userRepository.findById(id);
    if (optionalUser.isEmpty()) {
      throw new RuntimeException("Utilisateur non trouvé avec ID : " + id);
    }

    User user = optionalUser.get();
    user.setNom(dto.getNom());
    user.setEmail(dto.getEmail());
    user.setTel(dto.getTel());
    user.setAdresse(dto.getAdresse());
    user.setEstValide(dto.isEstValide());
    user.setRole(dto.getRole());
    user.setNomEntreprise(dto.getNomEntreprise());
    user.setRC(dto.getRC());
    user.setICE(dto.getICE());
    user.setImage(dto.getImage());
    user.setCvFile(dto.getCvFile());


    User updatedUser = userRepository.save(user);
    return entityMapper.toUserDTO(updatedUser);
  }
  @Override
  public UserDTO partialUpdate(Long id, UserDTO dto) {
    User user = userRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec ID : " + id));

    if (dto.getNom() != null) user.setNom(dto.getNom());
    if (dto.getEmail() != null) user.setEmail(dto.getEmail());
    if (dto.getTel() != null) user.setTel(dto.getTel());
    if (dto.getAdresse() != null) user.setAdresse(dto.getAdresse());
    if (dto.getImage() != null) user.setImage(dto.getImage());
    if (dto.getCvFile() != null) user.setCvFile(dto.getCvFile());

    if (dto.getRole() != null) user.setRole(dto.getRole());
    if (dto.getNomEntreprise() != null) user.setNomEntreprise(dto.getNomEntreprise());
    if (dto.getRC() != null) user.setRC(dto.getRC());
    if (dto.getICE() != null) user.setICE(dto.getICE());
    user.setEstValide(dto.isEstValide());

    User updated = userRepository.save(user);
    return entityMapper.toUserDTO(updated);
  }

  @Override
  public UserDTO getById(Long id) {
    Optional<User> optionalUser = userRepository.findById(id);
    if (optionalUser.isEmpty()) {
      throw new RuntimeException("Utilisateur non trouvé avec ID : " + id);
    }
    return entityMapper.toUserDTO(optionalUser.get());
  }

  @Override
  public List<UserDTO> getAll() {
    List<User> users = userRepository.findAll();

    return users.stream()
      .map(entityMapper::toUserDTO)
      .collect(Collectors.toList());
  }

  @Override
  public void delete(Long id) {
    if (!userRepository.existsById(id)) {
      throw new RuntimeException("Utilisateur non trouvé avec ID : " + id);
    }
    userRepository.deleteById(id);
    //return ResponseEntity.ok(new MessageResponse("Utilisateur supprimé avec succès"));

  }
  @Override
  public void deleteAll() {
    userRepository.deleteAll();
  }

}
