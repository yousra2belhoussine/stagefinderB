package ma.stagefinder.dtos;

import lombok.Data;
import ma.stagefinder.entities.enums.TypeCategorie;

@Data
public class CategorieDTO {
    private Long id;
    private TypeCategorie typeCategorie;
}
