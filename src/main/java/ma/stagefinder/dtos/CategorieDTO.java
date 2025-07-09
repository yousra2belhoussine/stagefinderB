package ma.stagefinder.dtos;

import lombok.Data;
import ma.stagefinder.entities.enums.TypeCategorie;

import java.io.Serializable;

@Data
public class CategorieDTO implements Serializable {
    private Long id;
    private String titre;
    private TypeCategorie typeCategorie;
}
