package ma.stagefinder.services;

import ma.stagefinder.dtos.UserDTO;
import ma.stagefinder.entities.enums.Role;

import java.util.List;

public interface UserService {

  UserDTO create(UserDTO dto);

  long count();
  long countByRole(Role role);

  UserDTO update(Long id, UserDTO dto);


  UserDTO partialUpdate(Long id, UserDTO dto);


  UserDTO getById(Long id);

  List<UserDTO> getAll();

  void delete(Long id);


  void deleteAll();



}
