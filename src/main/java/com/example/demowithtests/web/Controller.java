package com.example.demowithtests.web;

import com.example.demowithtests.domain.Employee;
import com.example.demowithtests.domain.Gender;
import com.example.demowithtests.dto.employee.EmployeePutDto;
import com.example.demowithtests.dto.employee.EmployeeReadDto;
import com.example.demowithtests.dto.employee.EmployeeCreateDto;
import com.example.demowithtests.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Tag(name = "Employee", description = "Employee API")
public class Controller {
    private final EmployeeService employeeService;
//    private final EmployeeMapper mapper;

    //Операция сохранения юзера в базу данных
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "This is endpoint to add a new employee.", description = "Create request to add a new employee.", tags = {"Employee"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "CREATED. The new employee is successfully created and added to database."),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND. Specified employee request not found."),
            @ApiResponse(responseCode = "409", description = "Employee already exists")})
    public EmployeeReadDto createEmployee(@RequestBody @Valid EmployeeCreateDto createDto) {
//        var employee = mapper.employeeDTOToEmployee(createDto);
//        var employeeDto = mapper.employeeToEmployeeDTO(employeeService.create(employee));
        return employeeService.createEmployee(createDto);

    }

    //Получение списка юзеров
    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeReadDto> getAllUsers() {
        return employeeService.getAll();
    }

    @GetMapping("/users/p")
    @ResponseStatus(HttpStatus.OK)
    public Page<EmployeeReadDto> getPage(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "5") int size
    ) {
        Pageable paging = PageRequest.of(page, size);
        return employeeService.getAllWithPagination(paging);
    }

    //Получения юзера по id
    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This is endpoint returned a employee by his id.", description = "Create request to read a employee by id", tags = {"Employee"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "OK. pam pam param."),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND. Specified employee request not found."),
            @ApiResponse(responseCode = "409", description = "Employee already exists")})
    public EmployeeReadDto getEmployeeById(@PathVariable Integer id) {
        EmployeeReadDto employeeReadDto = employeeService.getById(id);
        return employeeReadDto;
    }

    //Обновление юзера
    @PutMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeReadDto refreshEmployee(@PathVariable("id") Integer id, @RequestBody EmployeePutDto putDto) {
        return employeeService.updateById(id, putDto);
    }

    //Удаление по id
    @PatchMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeEmployeeById(@PathVariable Integer id) {
        employeeService.removeById(id);
    }

    //Удаление всех юзеров
    @DeleteMapping("/users")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAllUsers() {
        employeeService.removeAll();
    }

    @GetMapping("/users/country")
    @ResponseStatus(HttpStatus.OK)
    public Page<Employee> findByCountry(@RequestParam(required = false) String country,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "3") int size,
                                        @RequestParam(defaultValue = "") List<String> sortList,
                                        @RequestParam(defaultValue = "DESC") Sort.Direction sortOrder) {
        //Pageable paging = PageRequest.of(page, size);
        //Pageable paging = PageRequest.of(page, size, Sort.by("name").ascending());
        return employeeService.findByCountryContaining(country, page, size, sortList, sortOrder.toString());
    }

    @GetMapping("/users/c")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getAllUsersC() {
        return employeeService.getAllEmployeeCountry();
    }

    @GetMapping("/users/s")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getAllUsersSort() {
        return employeeService.getSortCountry();
    }

    @GetMapping("/users/emails")
    @ResponseStatus(HttpStatus.OK)
    public Optional<String> getAllUsersSo() {
        return employeeService.findEmails();
    }

    @GetMapping("/users/byGenderAndCountry")
    @ResponseStatus(HttpStatus.OK)
    public List<Employee> readByGender(@RequestParam Gender gender, @RequestParam String country) {
        return employeeService.getByGender(gender, country);
    }

    @GetMapping("/users/has-active-address")
    @ResponseStatus(HttpStatus.OK)
    public Page<Employee> readActiveAddressesByCountry(@RequestParam String country,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        return employeeService.getActiveAddressesByCountry(country, pageable);
    }

    //---------------------------------------------------------------------------------------
    @GetMapping("/users/proc-is-deleted")
    @ResponseStatus(HttpStatus.OK)
    public List<Employee> handleEmployeesWithIsDeletedFieldIsNull() {
        return employeeService.handleEmployeesWithIsDeletedFieldIsNull();
    }

    @GetMapping("/users/proc-is-private")
    @ResponseStatus(HttpStatus.OK)
    public List<Employee> handleEmployeesWithIsPrivateFieldIsNull() {
        return employeeService.handleEmployeesWithIsPrivateFieldIsNull();
    }

    //hw-5
    //---------------------------------------------------------------------------------------
    @GetMapping("/users/active")
    @ResponseStatus(HttpStatus.OK)
    public Page<Employee> getAllActiveUsers(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        return employeeService.getAllActive(pageable);
    }

    @GetMapping("/users/deleted")
    @ResponseStatus(HttpStatus.OK)
    public Page<Employee> getAllDeletedUsers(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        return employeeService.getAllDeleted(pageable);
    }

    //    hw-6
    //---------------------------------------------------------------------------------------
    // Метод отправляет письмо на почту с подтверждением.
    // Из письма юзер должен дернуть метод confirm(id), который поменяет статус is_confirmed поля бд.
    @GetMapping("/users/{id}/confirm")  //@PatchMapping("/users/{id}/confirm")
    //Вопрос: если метод ничего не меняет в репозитории, но каждый раз при его вызове отправляется письмо клиенту,
    //он индемпотентный?
    //upd: походу, да. После 1го подтверждения (может быть отправлено из любого письма, в смысле по-порядку),
    //  мы меняем статус в бд. Последующие запросы на подтверждение состояние базы не меняют
    @ResponseStatus(HttpStatus.OK)
    public void sendConfirm(@PathVariable Integer id) {
        employeeService.sendMailConfirm(id);
    }

    @GetMapping("/users/{id}/confirmed")// Get - костыль, так из письма проще этот эндпоинт дергать.
    //    @PatchMapping("/users/{id}/confirmed")
    @ResponseStatus(HttpStatus.OK)
    public void confirm(@PathVariable Integer id) {
        employeeService.confirm(id);
    }

    //    hw-7
    //---------------------------------------------------------------------------------------
    // точка входа на массовую генерацию
    @PostMapping("/users/generate/{quantity}")
    @ResponseStatus(HttpStatus.CREATED)
    public Long generateEmployee(@PathVariable Integer quantity,
                                 @RequestParam(required = false,
                                         defaultValue = "false") Boolean clear) {
        LocalDateTime timeStart = LocalDateTime.now();
        log.info("Controller -> generateEmployee(Integer, Boolean) -> start: time={}", timeStart);

        employeeService.generateEntity(quantity, clear);

        LocalDateTime timeStop = LocalDateTime.now();
        log.info("Controller -> generateEmployee(Integer, Boolean) -> stop: time={}", timeStop);
        log.info("Controller -> generateEmployee(Integer, Boolean) -> method execution, ms: duration={}",
                Duration.between(timeStart, timeStop).toMillis());
        System.err.println(Duration.between(timeStart, timeStop).toMillis());
        return Duration.between(timeStart, timeStop).toMillis();
    }

    //---------------------------------------------------------------------------------------
    @PutMapping("/users/mass-test-update")
    @ResponseStatus(HttpStatus.OK)
    public Long employeeMassPutUpdate() {
        LocalDateTime timeStart = LocalDateTime.now();
        log.info("Controller -> employeeMassPutUpdate() method start: time={}", timeStart);

        employeeService.massTestUpdate();

        LocalDateTime timeStop = LocalDateTime.now();
        log.info("Controller -> employeeMassPutUpdate() method start: time={}", timeStop);
        log.info("Controller -> employeeMassPutUpdate() method execution, ms: duration={}",
                Duration.between(timeStart, timeStop).toMillis());
        System.err.println(Duration.between(timeStart, timeStop).toMillis());
        return Duration.between(timeStart, timeStop).toMillis();
    }

    //---------------------------------------------------------------------------------------
    @PatchMapping("/users/mass-test-update")
    @ResponseStatus(HttpStatus.OK)
    public Long employeeMassPatchUpdate() {
        LocalDateTime timeStart = LocalDateTime.now();
        log.info("Controller -> employeeMassPatchUpdate() method start: time={}", timeStart);

        employeeService.massTestUpdate();

        LocalDateTime timeStop = LocalDateTime.now();
        log.info("Controller -> employeeMassPatchUpdate() method start: time={}", timeStop);
        log.info("Controller -> employeeMassPatchUpdate() method execution, ms: duration={}",
                Duration.between(timeStart, timeStop).toMillis());
        System.err.println(Duration.between(timeStart, timeStop).toMillis());
        return Duration.between(timeStart, timeStop).toMillis();
    }
}
