package ma.stagefinder.controllers;

import lombok.AllArgsConstructor;
import ma.stagefinder.dtos.UpdateEstValideRequest;
import ma.stagefinder.dtos.UserDTO;
import ma.stagefinder.entities.enums.Role;
import ma.stagefinder.repositories.UserRepository;
import ma.stagefinder.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private UserRepository userRepository;
    private UserService userService;

    @GetMapping("/count")
    public long countUsers(@RequestParam(value = "role", required = false) Role role) {
        if (role != null) {
            return userRepository.countByRole(role);
        }
        return userRepository.count();
    }

    @GetMapping
    public List<UserDTO> findAllUsers() {
        return userService.getUsers();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> updateEstValide(@PathVariable Long id, @RequestBody UpdateEstValideRequest request) {
        UserDTO updatedUser = userService.updateEstValide(id, request.isEstValide());
        return ResponseEntity.ok(updatedUser);
    }


}
