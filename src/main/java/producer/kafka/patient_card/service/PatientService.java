package producer.kafka.patient_card.service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import producer.kafka.patient_card.dto.DiseaseDTO;
import producer.kafka.patient_card.dto.PatientDTO;
import producer.kafka.patient_card.exception.GlobalExceptionHandler;
import producer.kafka.patient_card.exception.ResourceNotFoundException;
import producer.kafka.patient_card.model.Disease;
import producer.kafka.patient_card.model.Patient;
import producer.kafka.patient_card.repository.PatientRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PatientService implements producer.kafka.patient_card.service.Service {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public PatientDTO getPatient(Long id) {
        log.debug("Получение данных пациента с ID: {}", id);
        Patient patient = patientRepository.findByIdWithDiseases(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пациент не найден"));
                log.warn("Пациент с ID {} не найден", id);
        return mapToDTO(patient);
    }

    public PatientDTO createPatient(PatientDTO patientDTO){
        log.debug("Создание нового пациента: {}", patientDTO);
        if (patientDTO.getBirthDate().isAfter(LocalDate.now())) {
            log.error("Неверная дата рождения: {}", patientDTO.getBirthDate());
            throw new IllegalArgumentException("Дата рождения не может быть в будущем");
        }
        Patient patient = mapToEntity(patientDTO);
        if (patientDTO.getOmsNumber() == null || patientDTO.getOmsNumber().isEmpty()) {
            log.error("OMS номер не может быть пустым");
            throw new IllegalArgumentException("OMS номер не может быть пустым");
        }
        log.info("Пациент создан с ID: {}", patient.getId());
        return mapToDTO(patientRepository.save(patient));
    }

    public PatientDTO updatePatient(Long id, PatientDTO patientDTO) {
        log.debug("Обновление данных пациента с ID: {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Пациент с ID {} не найден для обновления", id);
                    return new ResourceNotFoundException("Пациент не найден");
                });
        if (patientDTO.getBirthDate().isAfter(LocalDate.now())) {
            log.error("Неверная дата рождения при обновлении: {}", patientDTO.getBirthDate());
            throw new IllegalArgumentException("Дата рождения не может быть в будущем");
        }

        patient.setLastName(patientDTO.getLastName());
        patient.setFirstName(patientDTO.getFirstName());
        patient.setMiddleName(patientDTO.getMiddleName());
        patient.setGender(patientDTO.getGender());
        patient.setBirthDate(patientDTO.getBirthDate());
        patient.setOmsNumber(patientDTO.getOmsNumber());
        log.info("Пациент с ID {} обновлен", patient.getId());
        return mapToDTO(patientRepository.save(patient));
    }

    private PatientDTO mapToDTO(Patient patient) {
        return PatientDTO.builder()
                .id(patient.getId())
                .lastName(patient.getLastName())
                .firstName(patient.getFirstName())
                .middleName(patient.getMiddleName())
                .gender(patient.getGender())
                .birthDate(patient.getBirthDate())
                .omsNumber(patient.getOmsNumber())
                .disease(patient.getDisease() != null ? mapToListDTO(patient.getDisease()): new ArrayList<>())
                .build();
    }

    public void deletePatient(Long id) {
        log.debug("Удаление пациента с ID: {}", id);
        if (!patientRepository.existsById(id)) {
            log.warn("Попытка удалить пациента, который не найден с ID: {}", id);
            throw new ResourceNotFoundException("Пациент не найден");
        }
        patientRepository.deleteById(id);
        log.info("Пациент с ID {} удален", id);

    }

    private Patient mapToEntity(PatientDTO dto) {
        Patient patient = new Patient();
        patient.setLastName(dto.getLastName());
        patient.setFirstName(dto.getFirstName());
        patient.setMiddleName(dto.getMiddleName());
        patient.setGender(dto.getGender());
        patient.setBirthDate(dto.getBirthDate());
        patient.setOmsNumber(dto.getOmsNumber());
        patient.setDisease(patient.getDisease());
        return patient;
    }

    private List<DiseaseDTO> mapToListDTO(List<Disease> diseases) {
        return diseases.stream()
                .map(disease -> DiseaseDTO.builder()
                        .icdCode(disease.getIcdCode())
                        .startDate(disease.getStartDate())
                        .endDate(disease.getEndDate())
                        .prescription(disease.getPrescription())
                        .build())
                .collect(Collectors.toList());
    }
}
