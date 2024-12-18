package producer.kafka.patient_card;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import producer.kafka.patient_card.model.Disease;
import producer.kafka.patient_card.model.Patient;
import producer.kafka.patient_card.repository.PatientRepository;
import producer.kafka.patient_card.service.PatientService;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    public PatientService patientService;
    @Autowired
    private ObjectMapper objectMapper;
    private Long savedPatientId;

    @BeforeEach
    void setUp() throws Exception {
        patientRepository.deleteAll();
    }


    @Test
    void testGetPatientSuccess() throws Exception {
        Patient patient = Patient.builder()
                .firstName("Mark")
                .lastName("Brown")
                .gender("man")
                .birthDate(LocalDate.of(1990, 1, 1))
                .omsNumber("1234567891234567")
                .build();
        Disease disease = Disease.builder()
                .prescription("Грипп")
                .icdCode("A123")
                .startDate(LocalDate.now())
                .patient(patient)
                .build();
        patient.setDisease(List.of(disease));
        patientRepository.save(patient);

        savedPatientId = patientRepository.findAll().get(0).getId();
        mockMvc.perform(get("/patient/" + savedPatientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedPatientId))
                .andExpect(jsonPath("$.firstName").value("Mark"))
                .andExpect(jsonPath("$.lastName").value("Brown"))
                .andExpect(jsonPath("$.disease[0].prescription").value("Грипп"))
                .andExpect(jsonPath("$.disease[0].icdCode").value("A123"));
    }

    @Test
    void testGetPatientFound() throws Exception {
        mockMvc.perform(get("/patient/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Пациент не найден"));
    }

    @Test
    void testCreatePatientSuccess() throws Exception {
        Patient patientNew = new Patient();
        patientNew.setFirstName("Jonny");
        patientNew.setLastName("Jonny");
        patientNew.setGender("man");
        patientNew.setBirthDate(LocalDate.of(1990, 5, 20));
        patientNew.setOmsNumber("9485782649359747");

        mockMvc.perform(post("/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientNew)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Jonny"))
                .andExpect(jsonPath("$.lastName").value("Jonny"))
                .andExpect(jsonPath("$.omsNumber").value("9485782649359747"));
    }

    @Test
    void testCreatePatientInvalidBirthDate() throws Exception {

        Patient patientInvalidBirthDate = new Patient();
        patientInvalidBirthDate.setFirstName("Invalid");
        patientInvalidBirthDate.setLastName("Date");
        patientInvalidBirthDate.setGender("female");
        patientInvalidBirthDate.setBirthDate(LocalDate.now().plusDays(1));
        patientInvalidBirthDate.setOmsNumber("9876543210987654");

        mockMvc.perform(post("/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientInvalidBirthDate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Дата рождения не может быть в будущем"));
    }

    @Test
    void testPatientNullDateOmsNumber() throws Exception {
        Patient patientNullDateOmsNumber = new Patient();
        patientNullDateOmsNumber.setFirstName("Invalid2");
        patientNullDateOmsNumber.setLastName("Date2");
        patientNullDateOmsNumber.setGender("female");
        patientNullDateOmsNumber.setBirthDate(LocalDate.of(2004,12,4));
        patientNullDateOmsNumber.setOmsNumber(null);

        mockMvc.perform(post("/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientNullDateOmsNumber)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdatePatientSuccess() throws Exception {
        Patient patient = new Patient();
        patient.setFirstName("Mark");
        patient.setLastName("Brown");
        patient.setGender("man");
        patient.setBirthDate(LocalDate.of(2000, 1, 1));
        patient.setOmsNumber("1234567891234567");
        patientRepository.save(patient);

        savedPatientId = patient.getId();

        Patient updatePatient = new Patient();
        updatePatient.setFirstName("UpdateMark");
        updatePatient.setLastName("UpdateBrown");
        updatePatient.setGender("man");
        updatePatient.setBirthDate(LocalDate.of(2020, 5, 10));
        updatePatient.setOmsNumber("1234567831345576");

        mockMvc.perform(put("/patient/" + savedPatientId,updatePatient)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatePatient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("UpdateMark"))
                .andExpect(jsonPath("$.lastName").value("UpdateBrown"))
                .andExpect(jsonPath("$.birthDate").value("2020-05-10"));
    }

    @Test
    void testUpdatePatientNotFound() throws Exception {
        Patient patientNotFound = new Patient();
        patientNotFound.setFirstName("NoneExistName");
        patientNotFound.setLastName("NoneExistSurname");
        patientNotFound.setGender("man");
        patientNotFound.setBirthDate(LocalDate.of(2021, 5, 10));
        patientNotFound.setOmsNumber("1234284831345576");

        mockMvc.perform(put("/patient/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientNotFound)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Пациент не найден"));
    }

}

