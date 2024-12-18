package producer.kafka.patient_card.dto;
import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseDTO {

    private String icdCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private String prescription;
}
