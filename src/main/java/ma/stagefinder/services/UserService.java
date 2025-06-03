package ma.stagefinder.services;

import ma.stagefinder.dtos.UserDTO;
import ma.stagefinder.entities.enums.Role;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {

  long count();

  long countByRole(Role role);

  UserDTO create(UserDTO dto);

  UserDTO update(Long id, UserDTO dto);

  UserDTO partialUpdate(Long id, UserDTO dto);
  //UserDTO updateOwnProfile(UserDTO dto);


  UserDTO updateEstValide(Long id, boolean estValide);

  //UserDTO updateUserProfile(Long userId, UserDTO dto);

  UserDTO getUserProfile(Long userId);

  UserDTO getById(Long id);

  List<UserDTO> getAll();

  void delete(Long id);

  void deleteAll();

  List<UserDTO> getUsers();

  String storeFile(MultipartFile file, String type) throws IOException;
  UserDTO updateWithFiles(Long id, UserDTO dto, MultipartFile cv, MultipartFile image) throws IOException;

}
