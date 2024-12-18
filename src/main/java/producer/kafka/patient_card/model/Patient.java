package producer.kafka.patient_card.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "patient_card_some",schema ="all_schem")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "patient_seq")
    @SequenceGenerator(name = "patient_seq", sequenceName = "patient_sequence",allocationSize=1)
    private Long id;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String firstName;
    private String middleName;
    @Column(nullable = false)
    private String gender;
    @Column(nullable = false)
    private LocalDate birthDate;
    @Column(nullable = false,length = 16, unique = true)
    private String omsNumber;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL,orphanRemoval = true) //fetch = FetchType.EAGER)
    private List<Disease> disease;

}
