package producer.kafka.patient_card.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import producer.kafka.patient_card.dto.DiseaseDTO;
import producer.kafka.patient_card.exception.ResourceNotFoundException;
import producer.kafka.patient_card.model.Disease;
import producer.kafka.patient_card.model.Patient;
import producer.kafka.patient_card.repository.DiseaseRepository;
import producer.kafka.patient_card.repository.PatientRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiseaseServiceImpl implements DiseaseService {
    private static final Logger logger = LoggerFactory.getLogger(DiseaseServiceImpl.class);
    private final DiseaseRepository diseaseRepository;
    private final PatientRepository patientRepository;

    public DiseaseServiceImpl(DiseaseRepository diseaseRepository, PatientRepository patientRepository) {
        this.diseaseRepository = diseaseRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    @Transactional
    public DiseaseDTO createDisease(Long patientId, DiseaseDTO diseaseDTO) {
        logger.info("Начато создание болезни для пациента с ID: {}", patientId);
        validateDiseaseDate(diseaseDTO);
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Пациент с ID " + patientId + " не найден"));

        Disease disease = mapToEntity(diseaseDTO);

        disease.setPatient(patient);
        logger.info("Болезнь успешно создана для пациента с ID: {}", patientId);
        return mapToDto(diseaseRepository.save(disease));

    }
    @Override
    @Transactional(readOnly = true)
    public List<DiseaseDTO> getAllDiseases(Long id) {
        logger.debug("Получение всех заболеваний для пациента с ID: {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Пациент не найден с таким "  + id));
        return mapToListDTO(patient.getDisease());
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public DiseaseDTO getDiseaseById(Long id) {
        logger.debug("Получение болезни по ID: {}", id);
         Disease disease = diseaseRepository.findById(id)
                .orElseThrow( () ->  new ResourceNotFoundException("Заболевание не найдено"));
        return mapToDto(disease);
    }

    @Override
    @Transactional
    public DiseaseDTO updateDisease(Long id, DiseaseDTO diseaseDTO) {
        logger.info("Обновление болезни с ID: {}", id);
        Disease disease = diseaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Заболевание не найдено"));
        validateDiseaseDate(diseaseDTO);
        updateEntity(disease,diseaseDTO);
        Disease updateDisease = diseaseRepository.save(disease);
        logger.info("Болезнь с ID {} успешно обновлена", id);
        return mapToDto(updateDisease);

    }

    @Override
    @Transactional
    public void deleteDisease(Long id) {
        logger.warn("Удаление болезни с ID: {}", id);
        if (!diseaseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Заболевание не найдено");
        }
        diseaseRepository.deleteById(id);
        logger.info("Болезнь с ID {} успешно удалена", id);
    }

    private void validateDiseaseDate(DiseaseDTO diseaseDTO) {
        if (diseaseDTO.getStartDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Дата начала болезни не может быть в будущем");
        }
        if (diseaseDTO.getEndDate() != null && diseaseDTO.getEndDate().isAfter(LocalDate.now())){
            throw new IllegalArgumentException("Дата окончания болезни не может быть в будущем");
        }
        if (diseaseDTO.getPrescription() != null && diseaseDTO.getPrescription().length() > 1024 ) {
            throw new IllegalArgumentException("Описание болезни не может превышать 1024 символа");
        }
    }

    private Disease mapToEntity(DiseaseDTO diseaseDTO) {
        return Disease.builder()
                .icdCode(diseaseDTO.getIcdCode())
                .startDate(diseaseDTO.getStartDate())
                .endDate(diseaseDTO.getEndDate())
                .prescription(diseaseDTO.getPrescription())
                .build();
    }

    private DiseaseDTO mapToDto(Disease disease) {
        return DiseaseDTO.builder()
                .icdCode(disease.getIcdCode())
                .startDate(disease.getStartDate())
                .endDate(disease.getEndDate())
                .prescription(disease.getPrescription())
                .build();
    }

    private void updateEntity(Disease disease , DiseaseDTO diseaseDTO) {
        disease.setIcdCode(diseaseDTO.getIcdCode());
        disease.setStartDate(diseaseDTO.getStartDate());
        disease.setEndDate(diseaseDTO.getEndDate());
        disease.setPrescription(diseaseDTO.getPrescription());
    }

    private List<DiseaseDTO> mapToListDTO(List<Disease> listDisease) {
        return listDisease.stream()
                .map(disease -> mapToDto(disease))
                .collect(Collectors.toList());
    }
}
