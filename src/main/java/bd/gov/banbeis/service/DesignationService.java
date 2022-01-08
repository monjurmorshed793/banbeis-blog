package bd.gov.banbeis.service;

import bd.gov.banbeis.domain.Designation;
import bd.gov.banbeis.repository.DesignationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Designation}.
 */
@Service
public class DesignationService {

    private final Logger log = LoggerFactory.getLogger(DesignationService.class);

    private final DesignationRepository designationRepository;

    public DesignationService(DesignationRepository designationRepository) {
        this.designationRepository = designationRepository;
    }

    /**
     * Save a designation.
     *
     * @param designation the entity to save.
     * @return the persisted entity.
     */
    public Mono<Designation> save(Designation designation) {
        log.debug("Request to save Designation : {}", designation);
        return designationRepository.save(designation);
    }

    /**
     * Partially update a designation.
     *
     * @param designation the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Designation> partialUpdate(Designation designation) {
        log.debug("Request to partially update Designation : {}", designation);

        return designationRepository
            .findById(designation.getId())
            .map(existingDesignation -> {
                if (designation.getName() != null) {
                    existingDesignation.setName(designation.getName());
                }
                if (designation.getSortName() != null) {
                    existingDesignation.setSortName(designation.getSortName());
                }
                if (designation.getGrade() != null) {
                    existingDesignation.setGrade(designation.getGrade());
                }

                return existingDesignation;
            })
            .flatMap(designationRepository::save);
    }

    /**
     * Get all the designations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Flux<Designation> findAll(Pageable pageable) {
        log.debug("Request to get all Designations");
        return designationRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of designations available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return designationRepository.count();
    }

    /**
     * Get one designation by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Mono<Designation> findOne(String id) {
        log.debug("Request to get Designation : {}", id);
        return designationRepository.findById(id);
    }

    /**
     * Delete the designation by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Designation : {}", id);
        return designationRepository.deleteById(id);
    }
}
