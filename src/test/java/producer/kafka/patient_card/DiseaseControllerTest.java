package producer.kafka.patient_card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import producer.kafka.patient_card.controller.DiseaseController;
import producer.kafka.patient_card.dto.DiseaseDTO;
import producer.kafka.patient_card.exception.ResourceNotFoundException;
import producer.kafka.patient_card.service.DiseaseService;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiseaseControllerTest {
    @InjectMocks
    private DiseaseController diseaseController;

    @Mock
    private DiseaseService diseaseService;
    private DiseaseDTO disease1;
    private DiseaseDTO disease2;

    @BeforeEach
    void setUp() {
        disease1 = new DiseaseDTO("ICD-10", LocalDate.now(), null, "Prescription 1");
        disease2 = new DiseaseDTO("ICD-11", LocalDate.now(), null, "Prescription 2");
        validateDiseaseDate(disease1);
        validateDiseaseDate(disease2);
    }

    static void validateDiseaseDate(DiseaseDTO diseaseDTO) {
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

    @Test
    void testGetAllDiseases() {
        List<DiseaseDTO> diseases = Arrays.asList(disease1,disease2);
        when(diseaseService.getAllDiseases(1L)).thenReturn(diseases);
        ResponseEntity<List<DiseaseDTO>> response = diseaseController.getAllDisease(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertThat(response.getBody()).containsExactly(disease1, disease2);
    }


    @Test
    void testGetAllDiseasesNotFound() {
        when(diseaseService.getAllDiseases(999L)).thenThrow(new ResourceNotFoundException("Пациент не найден"));
        assertThrows(ResourceNotFoundException.class, () -> diseaseController.getAllDisease(999L));
    }

    @Test
    void testGetDiseases() {
        when(diseaseService.getDiseaseById(1L)).thenReturn(disease1);
        ResponseEntity<DiseaseDTO> response = diseaseController.getDisease(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertThat(response.getBody()).isEqualTo(disease1);
    }

    @Test
    void testGetDiseaseNotFound() {
        when(diseaseService.getDiseaseById(999L)).thenThrow(new ResourceNotFoundException("Заболевание не найдено"));
        assertThrows(ResourceNotFoundException.class, () -> diseaseController.getDisease(999L));
    }

    @Test
    void testCreatedDisease() {
        DiseaseDTO newDisease = new DiseaseDTO("ICD-12", LocalDate.now(),null, "Prescription 3");
        validateDiseaseDate(newDisease);
        when(diseaseService.createDisease(1L, newDisease)).thenReturn(newDisease);
        ResponseEntity<DiseaseDTO> response = diseaseController.createDisease(1L,newDisease);
        assertEquals(201, response.getStatusCodeValue());
        assertThat(response.getBody()).isEqualTo(newDisease);
    }

    @Test
    void testCreateDiseasePatientNotFound() {
        DiseaseDTO newDisease = new DiseaseDTO("ICD-12", LocalDate.now(),null, "Prescription 3");
        when(diseaseService.createDisease(999L,newDisease))
                .thenThrow(new ResourceNotFoundException("Пациент с ID 999 не найден"));
        assertThrows(ResourceNotFoundException.class, () -> diseaseController.createDisease(999L,newDisease));
    }

    @Test
    void testUpdateDiseaseSuccess() {
        DiseaseDTO updateDisease = new DiseaseDTO("ICD-10-Updated", LocalDate.now(), null, "Updated Prescription");
        validateDiseaseDate(updateDisease);
        when(diseaseService.updateDisease(1L, updateDisease)).thenReturn(updateDisease);
        DiseaseDTO response = diseaseController.updateDisease(1L,updateDisease);
        assertEquals(updateDisease, response);
        assertThat(response.getIcdCode()).isEqualTo("ICD-10-Updated");
        assertThat(response.getPrescription()).isEqualTo("Updated Prescription");
    }

    @Test
    void testUpdateDiseaseNotFound() {
        DiseaseDTO updateDisease = new DiseaseDTO("ICD-10-Updated", LocalDate.now(), null, "Updated Prescription");
        validateDiseaseDate(updateDisease);
        when(diseaseService.updateDisease(999L, updateDisease))
                .thenThrow(new ResourceNotFoundException("Заболевание с ID 999 не найдено"));
        assertThrows(ResourceNotFoundException.class ,() -> diseaseController.updateDisease(999L,updateDisease));
    }

    @Test
    void testDeleteSuccess() {
        doNothing().when(diseaseService).deleteDisease(1L);
        ResponseEntity<Void> response = diseaseController.deleteDisease(1L);
        assertEquals(204, response.getStatusCodeValue());
        verify(diseaseService, times(1)).deleteDisease(1L);
    }

    @Test
    void testDeleteDiseaseNotFound() {
        doThrow(new ResourceNotFoundException("Заболевание с ID 999 не найдено" ))
                .when(diseaseService).deleteDisease(999L);
        assertThrows(ResourceNotFoundException.class, () -> diseaseController.deleteDisease(999L));
        verify(diseaseService, times(1)).deleteDisease(999L);
    }

}