package ma.stagefinder.dtos;

import lombok.Data;
import ma.stagefinder.entities.enums.Role;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String nom;
    private String email;
    private String nomEntreprise;
    private String RC;
    private String ICE;
    private String tel;
    private String cvFile;
    private boolean estValide;
    private LocalDateTime verifiedAt;  // ← Ajouté
    private String adresse;
    private String image;
    private Role role;
}
