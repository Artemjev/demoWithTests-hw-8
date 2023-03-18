package com.example.demowithtests.service;

import com.example.demowithtests.domain.Employee;
import com.example.demowithtests.domain.Gender;
import com.example.demowithtests.dto.employee.EmployeeCreateDto;
import com.example.demowithtests.dto.employee.EmployeePutDto;
import com.example.demowithtests.dto.employee.EmployeeReadDto;
import com.example.demowithtests.repository.EmployeeRepository;
import com.example.demowithtests.util.config.EmployeeMapper;
import com.example.demowithtests.util.exception.*;
import com.example.demowithtests.util.mail.SmtpMailer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
public class EmployeeServiceBean implements EmployeeService {
    private final EmployeeRepository employeeRepository;
//    private final EmployeeMapper mapper;
    private final SmtpMailer smtpMailer;

    @Override
    public EmployeeReadDto createEmployee(EmployeeCreateDto createDto) {
//        Employee employee = mapper.employeeCreateDtoToEmployee(createDto);
        Employee employee = EmployeeMapper.INSTANCE.employeeCreateDtoToEmployee(createDto);
//        return mapper.employeeToEmployeeReadDTO(
        return EmployeeMapper.INSTANCE.employeeToEmployeeReadDTO(
                employeeRepository.save(employee)
        );
    }

    @Override
    public List<EmployeeReadDto> getAll() {
//        log.info("getAll() Service - start:");
        var employees = employeeRepository.findAll();
        return employees.stream()
//                .map(e -> mapper.employeeToEmployeeReadDTO(e))
                .map(e -> EmployeeMapper.INSTANCE.employeeToEmployeeReadDTO(e))
                .collect(Collectors.toList());
//        log.info("setEmployeePrivateFields() Service - end:  = size {}", employees.size());

    }

    private EmployeeReadDto hideEmployeeDetails(EmployeeReadDto employee) {
        log.debug("setEmployeePrivateFields() Service - start: id = {}", employee.getId());
        if (employee.getIsPrivate() == Boolean.TRUE || employee.getIsPrivate() == null) {
            employee.setName("is hidden");
            employee.setEmail("is hidden");
            employee.setCountry("is hidden");
            employee.setAddresses(null);
            employee.setGender(null);
        }
        log.debug("setEmployeePrivateFields() Service - end:  = employee {}", employee);
        return employee;
    }

    @Override
    public Page<EmployeeReadDto> getAllWithPagination(Pageable pageable) {
        Page<Employee> employees = employeeRepository.findAll(pageable);
        Page<EmployeeReadDto> employeeReadDto = employees
//                .map(e -> mapper.employeeToEmployeeReadDTO(e))
//                .map(e -> mapper.INSTANCE)
                .map(e -> EmployeeMapper.INSTANCE.employeeToEmployeeReadDTO(e))
                .map(e -> hideEmployeeDetails(e));
        return employeeReadDto;
    }

    @Override
    public EmployeeReadDto getById(Integer id) {
        log.info("getById(Integer id) Service - start: id = {}", id);
        var employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchEmployeeException("There is no employee with ID = " + id + " in database"));

        changeActiveStatus(employee);
        changePrivateStatus(employee);

        if (employee.getIsDeleted()) throw new ResourceNotVisibleException();

        if (employee.getIsPrivate()) throw new ResourceIsPrivateException();

        if (!employee.getIsConfirmed()) throw new EmployeeUnconfirmedDataException(
                "Employee " + employee.getName() + " has to confirm data. Check " + employee.getEmail() + " mail please"
        );

        log.info("getById(Integer id) Service - end:  = employee {}", employee);
//        return mapper.employeeToEmployeeReadDTO(employee);
        return EmployeeMapper.INSTANCE.employeeToEmployeeReadDTO(employee);
    }

    private void changePrivateStatus(Employee employee) {
        log.info("changePrivateStatus() Service - start: id = {}", employee.getId());
        if (employee.getIsPrivate() == null) {
            employee.setIsPrivate(Boolean.TRUE);
            employeeRepository.save(employee);
        }
        log.info("changePrivateStatus() Service - end: IsPrivate = {}", employee.getIsPrivate());
    }

    //
    private void changeActiveStatus(Employee employee) {
        log.info("changeActiveStatus() Service - start: id = {}", employee.getId());
        if (employee.getIsDeleted() == null) {
            employee.setIsDeleted(Boolean.FALSE);
            employeeRepository.save(employee);
        }
        log.info("changeActiveStatus() Service - end: isVisible = {}", employee.getIsDeleted());
    }

    @Override
    public EmployeeReadDto updateById(Integer id, EmployeePutDto putDto) {
        return employeeRepository.findById(id)
                .map(entity -> {
                    entity.setName(putDto.getName());
                    entity.setEmail(putDto.getEmail());
                    entity.setCountry(putDto.getCountry());
//                    entity.setIsDeleted(putDto.getIsDeleted());
                    Employee employee = employeeRepository.save(entity);
//                    return mapper.employeeToEmployeeReadDTO(employee);
                    return EmployeeMapper.INSTANCE.employeeToEmployeeReadDTO(employee);

                })
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id = " + id));

    }

    @Override
    public void removeById(Integer id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        employee.setIsDeleted(Boolean.FALSE);
        employeeRepository.save(employee);
    }

    @Override
    public void removeAll() {
        employeeRepository.deleteAll();
    }

    @Override
    public Page<Employee> findByCountryContaining(String country, int page, int size, List<String> sortList, String
            sortOrder) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(createSortOrder(sortList, sortOrder)));
        return employeeRepository.findByCountryContaining(country, pageable);
    }

    private List<Sort.Order> createSortOrder(List<String> sortList, String sortDirection) {
        List<Sort.Order> sorts = new ArrayList<>();
        Sort.Direction direction;
        for (String sort : sortList) {
            if (sortDirection != null) {
                direction = Sort.Direction.fromString(sortDirection);
            } else {
                direction = Sort.Direction.DESC;
            }
            sorts.add(new Sort.Order(direction, sort));
        }
        return sorts;
    }

    @Override
    public List<String> getAllEmployeeCountry() {
        log.info("getAllEmployeeCountry() - start:");
        List<Employee> employeeList = employeeRepository.findAll();
        List<String> countries = employeeList.stream()
                .map(country -> country.getCountry())
                .collect(Collectors.toList());
        log.info("getAllEmployeeCountry() - end: countries = {}", countries);
        return countries;
    }

    @Override
    public List<String> getSortCountry() {
        List<Employee> employeeList = employeeRepository.findAll();
        return employeeList.stream()
                .map(Employee::getCountry)
                .filter(c -> c.startsWith("U"))
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<String> findEmails() {
        var employeeList = employeeRepository.findAll();

        var emails = employeeList.stream()
                .map(Employee::getEmail)
                .collect(Collectors.toList());

        var opt = emails.stream()
                .filter(s -> s.endsWith(".com"))
                .findFirst()
                .orElse("error?");
        return Optional.ofNullable(opt);
    }

    @Override
    public List<Employee> getByGender(Gender gender, String country) {
        /*System.err.println("service getByGender start: gender: " + gender + " country: " + country);*/
        var employees = employeeRepository.findByGender(gender.toString(), country);
        /*System.err.println("service getByGender end: " + employees.toString());*/
        return employees;
    }

    @Override
    public Page<Employee> getActiveAddressesByCountry(String country, Pageable pageable) {
        return employeeRepository.findAllWhereIsActiveAddressByCountry(country, pageable);
    }

    //---------------------------------------------------------------------------------------
    @Override
    public List<Employee> handleEmployeesWithIsDeletedFieldIsNull() {
        var employees = employeeRepository.queryEmployeeByIsDeletedIsNull();
        for (Employee employee : employees) employee.setIsDeleted(Boolean.FALSE);
        employeeRepository.saveAll(employees);
        return employeeRepository.queryEmployeeByIsDeletedIsNull();
    }

    @Override
    public List<Employee> handleEmployeesWithIsPrivateFieldIsNull() {
        var employees = employeeRepository.queryEmployeeByIsPrivateIsNull();
        employees.forEach(employee -> employee.setIsPrivate(Boolean.FALSE));
        employeeRepository.saveAll(employees);
        return employeeRepository.queryEmployeeByIsPrivateIsNull();
    }

    // hw-5
    //---------------------------------------------------------------------------------------
    @Override
    public Page<Employee> getAllActive(Pageable pageable) {
        return employeeRepository.findAllActive(pageable);
    }

    @Override
    public Page<Employee> getAllDeleted(Pageable pageable) {
        return employeeRepository.findAllDeleted(pageable);
    }

    // hw-6
    //---------------------------------------------------------------------------------------
    @Override
    public void sendMailConfirm(Integer id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        smtpMailer.send(employee);

    }

    @Override
    public void confirm(Integer id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        employee.setIsConfirmed(Boolean.TRUE);
        employeeRepository.save(employee);
    }

    // hw-7
    //---------------------------------------------------------------------------------------
    @Override
    public void generateEntity(Integer quantity, Boolean clear) {
        if (clear) employeeRepository.deleteAll();

        List<Employee> employees = new ArrayList<>(1000);

        for (int i = 0; i < quantity; i++) {
            employees.add(Employee.builder()
                    .name("Name" + i)
                    .email("artemjev.mih@gmail.com")
                    .build());
        }
        employeeRepository.saveAll(employees);
    }

    @Override
    public void massTestUpdate() {
        List<Employee> employees = employeeRepository.findAll();
        employees.forEach(employee -> employee.setName(LocalDateTime.now().toString()));
        employeeRepository.saveAll(employees);
    }

    //---------------------------------------------------------------------------------------
}



