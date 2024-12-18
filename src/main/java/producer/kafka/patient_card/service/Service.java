package producer.kafka.patient_card.service;

import producer.kafka.patient_card.dto.PatientDTO;


@org.springframework.stereotype.Service
public interface Service {
    PatientDTO getPatient(Long id);
    PatientDTO createPatient(PatientDTO patientDTO);
    PatientDTO updatePatient(Long id, PatientDTO patientDTO);
    void deletePatient(Long id);


}
