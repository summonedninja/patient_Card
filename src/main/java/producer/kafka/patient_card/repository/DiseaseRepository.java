package producer.kafka.patient_card.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import producer.kafka.patient_card.model.Disease;

import java.util.List;

@Repository
public interface DiseaseRepository extends JpaRepository<Disease,Long> {
    List<Disease> findByPatientId(Long patientId);
}
