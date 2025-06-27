package ma.stagefinder.dtos;

import lombok.Data;
import ma.stagefinder.entities.enums.TypeCategorie;
import java.io.Serializable; // <--- 1. Zid had l'import

@Data
public class CategorieDTO implements Serializable { // <--- 2. Zid 'implements Serializable' hna
    private Long id;
    private String titre;
    private TypeCategorie typeCategorie;
}