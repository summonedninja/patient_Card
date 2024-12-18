package producer.kafka.patient_card.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "disease_sone", schema ="all_schem")
public class Disease {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "disease_seq")
    @SequenceGenerator(name = "disease_seq", sequenceName = "disease_sequence",allocationSize=1)
    private Long id;
    @Column(nullable = false)
    private String icdCode; //Код МКБ-10 что это вообще ? я хз
    @Column(nullable = false)
    private LocalDate startDate;
    private LocalDate endDate;
    @Column(length = 1024, nullable = false)
    private String prescription;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
}
