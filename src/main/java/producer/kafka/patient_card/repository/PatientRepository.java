package producer.kafka.patient_card.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import producer.kafka.patient_card.model.Patient;

import java.util.Optional;


@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    @Query("SELECT p FROM Patient p LEFT JOIN FETCH p.disease WHERE p.id = :id")
    Optional<Patient> findByIdWithDiseases(@Param("id") Long id);
}
