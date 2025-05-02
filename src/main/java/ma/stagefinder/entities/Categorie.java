package ma.stagefinder.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.stagefinder.entities.enums.TypeCategorie;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Categorie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titre;

    @Enumerated(EnumType.STRING)
    private TypeCategorie typeCategorie;
    @OneToMany(mappedBy = "categorie")
    @JsonIgnore
    private List<Offre> offres;
}
