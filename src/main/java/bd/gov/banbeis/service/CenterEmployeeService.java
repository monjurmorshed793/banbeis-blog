package bd.gov.banbeis.service;

import bd.gov.banbeis.domain.CenterEmployee;
import bd.gov.banbeis.repository.CenterEmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link CenterEmployee}.
 */
@Service
public class CenterEmployeeService {

    private final Logger log = LoggerFactory.getLogger(CenterEmployeeService.class);

    private final CenterEmployeeRepository centerEmployeeRepository;

    public CenterEmployeeService(CenterEmployeeRepository centerEmployeeRepository) {
        this.centerEmployeeRepository = centerEmployeeRepository;
    }

    /**
     * Save a centerEmployee.
     *
     * @param centerEmployee the entity to save.
     * @return the persisted entity.
     */
    public Mono<CenterEmployee> save(CenterEmployee centerEmployee) {
        log.debug("Request to save CenterEmployee : {}", centerEmployee);
        return centerEmployeeRepository.save(centerEmployee);
    }

    /**
     * Partially update a centerEmployee.
     *
     * @param centerEmployee the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<CenterEmployee> partialUpdate(CenterEmployee centerEmployee) {
        log.debug("Request to partially update CenterEmployee : {}", centerEmployee);

        return centerEmployeeRepository
            .findById(centerEmployee.getId())
            .map(existingCenterEmployee -> {
                if (centerEmployee.getDutyType() != null) {
                    existingCenterEmployee.setDutyType(centerEmployee.getDutyType());
                }
                if (centerEmployee.getJoiningDate() != null) {
                    existingCenterEmployee.setJoiningDate(centerEmployee.getJoiningDate());
                }
                if (centerEmployee.getReleaseDate() != null) {
                    existingCenterEmployee.setReleaseDate(centerEmployee.getReleaseDate());
                }
                if (centerEmployee.getMessage() != null) {
                    existingCenterEmployee.setMessage(centerEmployee.getMessage());
                }

                return existingCenterEmployee;
            })
            .flatMap(centerEmployeeRepository::save);
    }

    /**
     * Get all the centerEmployees.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Flux<CenterEmployee> findAll(Pageable pageable) {
        log.debug("Request to get all CenterEmployees");
        return centerEmployeeRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of centerEmployees available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return centerEmployeeRepository.count();
    }

    /**
     * Get one centerEmployee by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Mono<CenterEmployee> findOne(String id) {
        log.debug("Request to get CenterEmployee : {}", id);
        return centerEmployeeRepository.findById(id);
    }

    /**
     * Delete the centerEmployee by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(String id) {
        log.debug("Request to delete CenterEmployee : {}", id);
        return centerEmployeeRepository.deleteById(id);
    }
}
