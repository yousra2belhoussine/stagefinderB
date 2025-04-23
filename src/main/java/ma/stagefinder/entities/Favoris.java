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
    public class Favoris {
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
