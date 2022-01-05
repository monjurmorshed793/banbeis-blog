package bd.gov.banbeis.service;

import bd.gov.banbeis.domain.Division;
import bd.gov.banbeis.repository.DivisionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Division}.
 */
@Service
public class DivisionService {

    private final Logger log = LoggerFactory.getLogger(DivisionService.class);

    private final DivisionRepository divisionRepository;

    public DivisionService(DivisionRepository divisionRepository) {
        this.divisionRepository = divisionRepository;
    }

    /**
     * Save a division.
     *
     * @param division the entity to save.
     * @return the persisted entity.
     */
    public Mono<Division> save(Division division) {
        log.debug("Request to save Division : {}", division);
        return divisionRepository.save(division);
    }

    /**
     * Partially update a division.
     *
     * @param division the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Division> partialUpdate(Division division) {
        log.debug("Request to partially update Division : {}", division);

        return divisionRepository
            .findById(division.getId())
            .map(existingDivision -> {
                if (division.getName() != null) {
                    existingDivision.setName(division.getName());
                }
                if (division.getBnName() != null) {
                    existingDivision.setBnName(division.getBnName());
                }
                if (division.getUrl() != null) {
                    existingDivision.setUrl(division.getUrl());
                }

                return existingDivision;
            })
            .flatMap(divisionRepository::save);
    }

    /**
     * Get all the divisions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Flux<Division> findAll(Pageable pageable) {
        log.debug("Request to get all Divisions");
        return divisionRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of divisions available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return divisionRepository.count();
    }

    /**
     * Get one division by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Mono<Division> findOne(String id) {
        log.debug("Request to get Division : {}", id);
        return divisionRepository.findById(id);
    }

    /**
     * Delete the division by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Division : {}", id);
        return divisionRepository.deleteById(id);
    }
}
