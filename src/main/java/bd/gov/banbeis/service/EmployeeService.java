package bd.gov.banbeis.service;

import bd.gov.banbeis.domain.Employee;
import bd.gov.banbeis.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Employee}.
 */
@Service
public class EmployeeService {

    private final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Save a employee.
     *
     * @param employee the entity to save.
     * @return the persisted entity.
     */
    public Mono<Employee> save(Employee employee) {
        log.debug("Request to save Employee : {}", employee);
        return employeeRepository.save(employee);
    }

    /**
     * Partially update a employee.
     *
     * @param employee the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Employee> partialUpdate(Employee employee) {
        log.debug("Request to partially update Employee : {}", employee);

        return employeeRepository
            .findById(employee.getId())
            .map(existingEmployee -> {
                if (employee.getFullName() != null) {
                    existingEmployee.setFullName(employee.getFullName());
                }
                if (employee.getBnFullName() != null) {
                    existingEmployee.setBnFullName(employee.getBnFullName());
                }
                if (employee.getMobile() != null) {
                    existingEmployee.setMobile(employee.getMobile());
                }
                if (employee.getEmail() != null) {
                    existingEmployee.setEmail(employee.getEmail());
                }
                if (employee.getPhotoUrl() != null) {
                    existingEmployee.setPhotoUrl(employee.getPhotoUrl());
                }
                if (employee.getPhoto() != null) {
                    existingEmployee.setPhoto(employee.getPhoto());
                }
                if (employee.getPhotoContentType() != null) {
                    existingEmployee.setPhotoContentType(employee.getPhotoContentType());
                }

                return existingEmployee;
            })
            .flatMap(employeeRepository::save);
    }

    /**
     * Get all the employees.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Flux<Employee> findAll(Pageable pageable) {
        log.debug("Request to get all Employees");
        return employeeRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of employees available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return employeeRepository.count();
    }

    /**
     * Get one employee by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Mono<Employee> findOne(String id) {
        log.debug("Request to get Employee : {}", id);
        return employeeRepository.findById(id);
    }

    /**
     * Delete the employee by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Employee : {}", id);
        return employeeRepository.deleteById(id);
    }
}
