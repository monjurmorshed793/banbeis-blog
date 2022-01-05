package bd.gov.banbeis.service;

import bd.gov.banbeis.domain.Upazila;
import bd.gov.banbeis.repository.UpazilaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Upazila}.
 */
@Service
public class UpazilaService {

    private final Logger log = LoggerFactory.getLogger(UpazilaService.class);

    private final UpazilaRepository upazilaRepository;

    public UpazilaService(UpazilaRepository upazilaRepository) {
        this.upazilaRepository = upazilaRepository;
    }

    /**
     * Save a upazila.
     *
     * @param upazila the entity to save.
     * @return the persisted entity.
     */
    public Mono<Upazila> save(Upazila upazila) {
        log.debug("Request to save Upazila : {}", upazila);
        return upazilaRepository.save(upazila);
    }

    /**
     * Partially update a upazila.
     *
     * @param upazila the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Upazila> partialUpdate(Upazila upazila) {
        log.debug("Request to partially update Upazila : {}", upazila);

        return upazilaRepository
            .findById(upazila.getId())
            .map(existingUpazila -> {
                if (upazila.getDistrictId() != null) {
                    existingUpazila.setDistrictId(upazila.getDistrictId());
                }
                if (upazila.getName() != null) {
                    existingUpazila.setName(upazila.getName());
                }
                if (upazila.getBnName() != null) {
                    existingUpazila.setBnName(upazila.getBnName());
                }
                if (upazila.getUrl() != null) {
                    existingUpazila.setUrl(upazila.getUrl());
                }

                return existingUpazila;
            })
            .flatMap(upazilaRepository::save);
    }

    /**
     * Get all the upazilas.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Flux<Upazila> findAll(Pageable pageable) {
        log.debug("Request to get all Upazilas");
        return upazilaRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of upazilas available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return upazilaRepository.count();
    }

    /**
     * Get one upazila by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Mono<Upazila> findOne(String id) {
        log.debug("Request to get Upazila : {}", id);
        return upazilaRepository.findById(id);
    }

    /**
     * Delete the upazila by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Upazila : {}", id);
        return upazilaRepository.deleteById(id);
    }
}
