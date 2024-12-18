package producer.kafka.patient_card.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import producer.kafka.patient_card.dto.PatientDTO;
import producer.kafka.patient_card.service.Service;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final Service service;

    public PatientController(Service service) {
        this.service = service;
    }
    @Operation(summary = "Получить информацию о пациенте по идентификатору",description = "Найдите пациента по его идентификатору" )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description ="Успешно извлеченный пациент"),
            @ApiResponse(responseCode = "404",description = "Пациент не найден ")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatient(@PathVariable Long id) {
        return ResponseEntity.ok(service.getPatient(id));
    }
    @Operation(summary = "Создайте нового пациента",description = "Add a new patient to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",description ="Успешно создание пациента"),
            @ApiResponse(responseCode = "404",description = " данные пациент неправильне ")
    })
    @PostMapping
    public ResponseEntity<PatientDTO> createPatient(@RequestBody PatientDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createPatient(dto));
    }
    @Operation(summary = "Обновлять информацию о пациенте",description = "Обновите существующие данные пациента по идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description ="Успешно обанлён пациента"),
            @ApiResponse(responseCode = "404",description = " данные пациент не найдены  ")
    })

    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> updatePatient(@PathVariable Long id,
                                                    @RequestBody PatientDTO dto) {
        return ResponseEntity.ok(service.updatePatient(id, dto));
    }
    @Operation(summary = "Удаление пациента",description = "Удаление пациента из системы по идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",description ="Успешно удалён пациент"),
            @ApiResponse(responseCode = "404",description = " данные пациент не найдены  ")
    })

    @DeleteMapping("/{id}")
    public void deletePatient(@PathVariable Long id) {
        service.deletePatient(id);
    }
}
