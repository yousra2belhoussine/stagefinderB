package ma.stagefinder.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable; // <-- 1. ZEDNA L'IMPORT
import java.time.LocalDateTime;

@Entity
@Table(name = "favoris") // Mzyan t'zid @Table l l'woudou7
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Favoris implements Serializable { // <-- 2. ZEDNA HADI

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_ajout")
    private LocalDateTime dateAjout;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "offre_id")
    private Offre offre;
}
