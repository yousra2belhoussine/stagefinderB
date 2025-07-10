package ma.stagefinder.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // ✅ constructeur vide
@AllArgsConstructor // ✅ constructeur avec tous les champs
public class AuthResponse {

  private String token;
  private String refreshToken;
  private String message;
}
