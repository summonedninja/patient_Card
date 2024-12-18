package producer.kafka.patient_card.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import producer.kafka.patient_card.dto.DiseaseDTO;
import producer.kafka.patient_card.service.DiseaseService;

import java.util.List;

@RestController
@RequestMapping("/diseases")
public class DiseaseController {
    private final DiseaseService diseaseService;

    public DiseaseController(DiseaseService diseaseService) {
        this.diseaseService = diseaseService;
    }
    @Operation(summary = "Получить все заболевания по идентификатору пациента",
            description = "Возвращает список всех заболеваний, связанных с определённым пациентом.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка заболеваний"),
            @ApiResponse(responseCode = "404", description = "Пациент не найден")
    })
    @GetMapping( "/{patientId}/getAllById")
    public ResponseEntity<List<DiseaseDTO>> getAllDisease(@PathVariable Long patientId) {
        List<DiseaseDTO> diseases = diseaseService.getAllDiseases(patientId);
        return ResponseEntity.ok(diseases);
    }
    @Operation(summary = "Получить заболевание по идентификатору",
            description = "Возвращает информацию о заболевании по его идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение заболевания"),
            @ApiResponse(responseCode = "404", description = "Заболевание не найдено")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DiseaseDTO> getDisease(@PathVariable Long id) {
        DiseaseDTO getDiseaseById = diseaseService.getDiseaseById(id);
        return ResponseEntity.ok(getDiseaseById);
    }
    @Operation(summary = "Создать заболевание для пациента",
            description = "Добавляет новое заболевание для пациента по его идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Заболевание успешно создано"),
            @ApiResponse(responseCode = "404", description = "Пациент не найден")
    })
    @PostMapping("/{patientId}")
    public ResponseEntity<DiseaseDTO> createDisease(@PathVariable Long patientId,@RequestBody DiseaseDTO diseaseDTO) {
        return new ResponseEntity<>(diseaseService.createDisease(patientId,diseaseDTO), HttpStatus.CREATED);
    }
    @Operation(summary = "Обновить заболевание",
            description = "Обновляет информацию о существующем заболевании по идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заболевание успешно обновлено"),
            @ApiResponse(responseCode = "404", description = "Заболевание не найдено")
    })
    @PutMapping("/{id}")
    public DiseaseDTO updateDisease(@PathVariable Long id,@RequestBody DiseaseDTO diseaseDTO) {
        return diseaseService.updateDisease(id, diseaseDTO);
    }
    @Operation(summary = "Удалить заболевание",
            description = "Удаляет заболевание по его идентификатору из системы.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Заболевание успешно удалено"),
            @ApiResponse(responseCode = "404", description = "Заболевание не найдено")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDisease(@PathVariable Long id) {
        diseaseService.deleteDisease(id);
        return ResponseEntity.noContent().build();
    }
}
