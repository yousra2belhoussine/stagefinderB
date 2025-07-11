package ma.stagefinder.controllers;

import ma.stagefinder.services.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LogoutController {

  private final LogoutService logoutService;

  @PostMapping("/logout")
  public String logout(HttpServletRequest request) {
    logoutService.logout(request);
    return "Déconnexion réussie ✅";
  }
}
