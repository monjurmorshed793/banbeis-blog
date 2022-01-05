package bd.gov.banbeis.service;

import bd.gov.banbeis.domain.Center;
import bd.gov.banbeis.repository.CenterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Center}.
 */
@Service
public class CenterService {

    private final Logger log = LoggerFactory.getLogger(CenterService.class);

    private final CenterRepository centerRepository;

    public CenterService(CenterRepository centerRepository) {
        this.centerRepository = centerRepository;
    }

    /**
     * Save a center.
     *
     * @param center the entity to save.
     * @return the persisted entity.
     */
    public Mono<Center> save(Center center) {
        log.debug("Request to save Center : {}", center);
        return centerRepository.save(center);
    }

    /**
     * Partially update a center.
     *
     * @param center the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Center> partialUpdate(Center center) {
        log.debug("Request to partially update Center : {}", center);

        return centerRepository
            .findById(center.getId())
            .map(existingCenter -> {
                if (center.getName() != null) {
                    existingCenter.setName(center.getName());
                }
                if (center.getAddressLine() != null) {
                    existingCenter.setAddressLine(center.getAddressLine());
                }
                if (center.getImage() != null) {
                    existingCenter.setImage(center.getImage());
                }
                if (center.getImageContentType() != null) {
                    existingCenter.setImageContentType(center.getImageContentType());
                }

                return existingCenter;
            })
            .flatMap(centerRepository::save);
    }

    /**
     * Get all the centers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Flux<Center> findAll(Pageable pageable) {
        log.debug("Request to get all Centers");
        return centerRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of centers available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return centerRepository.count();
    }

    /**
     * Get one center by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Mono<Center> findOne(String id) {
        log.debug("Request to get Center : {}", id);
        return centerRepository.findById(id);
    }

    /**
     * Delete the center by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Center : {}", id);
        return centerRepository.deleteById(id);
    }
}
