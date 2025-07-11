package ma.stagefinder.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ma.stagefinder.entities.enums.Role;

@Data
public class UserDTO {
    private Long id;
    private String nom;
    private String email;
    private String nomEntreprise;
    //@JsonProperty("rc")
    private String RC;
    //@JsonProperty("ice")

    private String ICE;
    private String tel;
    private String cvFile;
    private boolean estValide;
    private String adresse;
    private String image;
    private Role role;
    private String password;

}
