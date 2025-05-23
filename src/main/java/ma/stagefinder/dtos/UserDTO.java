package ma.stagefinder.dtos;

import lombok.Data;
import ma.stagefinder.entities.enums.Role;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserDTO {
    private Long id;
    private String nom;
    private String email;
    private String nomEntreprise;
    private String RC;
    private String ICE;
    private String tel;
    private MultipartFile cvFile; // Changé en MultipartFile pour l'upload
    private boolean estValide;
    private String adresse;
    private MultipartFile image;  // Changé en MultipartFile pour l'upload
    private Role role;
}