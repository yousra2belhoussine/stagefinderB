package ma.stagefinder.controllers;

import lombok.RequiredArgsConstructor;
import ma.stagefinder.dtos.UserDTO;
import ma.stagefinder.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping
  public ResponseEntity<UserDTO> create(@RequestBody UserDTO dto) {
    return ResponseEntity.ok(userService.create(dto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserDTO dto) {
    return ResponseEntity.ok(userService.update(id, dto));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<UserDTO> partialUpdate(@PathVariable Long id, @RequestBody UserDTO dto) {
    return ResponseEntity.ok(userService.partialUpdate(id, dto));
  }


  @GetMapping("/{id}")
  public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getById(id));
  }

  @GetMapping
  public ResponseEntity<List<UserDTO>> getAll() {
    return ResponseEntity.ok(userService.getAll());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    userService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping
  public ResponseEntity<Void> deleteAll() {
    userService.deleteAll();
    return ResponseEntity.noContent().build(); // Code HTTP 204
  }
}
