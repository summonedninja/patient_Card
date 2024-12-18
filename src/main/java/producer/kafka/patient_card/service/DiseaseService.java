package producer.kafka.patient_card.service;
import org.springframework.stereotype.Service;
import producer.kafka.patient_card.dto.DiseaseDTO;
import java.util.List;

@Service
public interface DiseaseService {
    DiseaseDTO createDisease(Long patientId, DiseaseDTO diseaseDTO);
    List<DiseaseDTO> getAllDiseases(Long id);
    DiseaseDTO getDiseaseById(Long id);
    DiseaseDTO updateDisease(Long id, DiseaseDTO diseaseDTO);
    void deleteDisease(Long id);
}
