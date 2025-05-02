package ma.stagefinder.dtos;


import lombok.Data;

@Data
public class AuthRequest {


  private String email;
  private String password;
  @Override
  public String toString() {
    return "email=" + email + ", password=" + password;
  }
}
