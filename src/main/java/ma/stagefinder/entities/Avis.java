package ma.stagefinder.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Avis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String commentaire; // Le contenu de l'avis

    @Column(name = "date_publication")
    private LocalDateTime datePublication;

    @ManyToOne
    @JoinColumn(name = "auteur_id")
    private User auteur;

    @ManyToOne
    @JoinColumn(name = "destinataire_id")
    private User destinataire;

    @ManyToOne
    @JoinColumn(name = "offre_id")
    private Offre offre;

    private int note; // Une note associée à l'avis (par exemple, de 1 à 5)
}
