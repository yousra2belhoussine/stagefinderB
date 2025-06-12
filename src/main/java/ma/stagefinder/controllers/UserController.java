package ma.stagefinder.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import ma.stagefinder.dtos.LoginRequest;
import ma.stagefinder.dtos.UpdateEstValideRequest;
import ma.stagefinder.dtos.UserDTO;
import ma.stagefinder.entities.User;
import ma.stagefinder.entities.enums.Role;
import ma.stagefinder.repositories.UserRepository;
import ma.stagefinder.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @PutMapping("/{userId}/profile")
    public ResponseEntity<UserDTO> updateUserProfile(
            @PathVariable Long userId,
            @RequestBody UserDTO userDTO)
          {
        UserDTO updatedUser = userService.updateUserProfile(userId, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable Long userId) {
        UserDTO user = userService.getUserProfile(userId);
        return ResponseEntity.ok(user);
    }

    // Nouveau endpoint pour le login
    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(
            @RequestBody LoginRequest loginRequest,
            HttpSession session) {
        UserDTO userDTO = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
        session.setAttribute("user", userDTO); // Stocker l'utilisateur dans la session
        return ResponseEntity.ok(userDTO);
    }

    // Nouveau endpoint pour le logout
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate(); // Détruire la session
        return ResponseEntity.ok("Déconnexion réussie");
    }
}

