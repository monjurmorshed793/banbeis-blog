package bd.gov.banbeis.service;

import bd.gov.banbeis.domain.CenterImages;
import bd.gov.banbeis.repository.CenterImagesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link CenterImages}.
 */
@Service
public class CenterImagesService {

    private final Logger log = LoggerFactory.getLogger(CenterImagesService.class);

    private final CenterImagesRepository centerImagesRepository;

    public CenterImagesService(CenterImagesRepository centerImagesRepository) {
        this.centerImagesRepository = centerImagesRepository;
    }

    /**
     * Save a centerImages.
     *
     * @param centerImages the entity to save.
     * @return the persisted entity.
     */
    public Mono<CenterImages> save(CenterImages centerImages) {
        log.debug("Request to save CenterImages : {}", centerImages);
        return centerImagesRepository.save(centerImages);
    }

    /**
     * Partially update a centerImages.
     *
     * @param centerImages the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<CenterImages> partialUpdate(CenterImages centerImages) {
        log.debug("Request to partially update CenterImages : {}", centerImages);

        return centerImagesRepository
            .findById(centerImages.getId())
            .map(existingCenterImages -> {
                if (centerImages.getImage() != null) {
                    existingCenterImages.setImage(centerImages.getImage());
                }
                if (centerImages.getImageContentType() != null) {
                    existingCenterImages.setImageContentType(centerImages.getImageContentType());
                }
                if (centerImages.getTitle() != null) {
                    existingCenterImages.setTitle(centerImages.getTitle());
                }
                if (centerImages.getDescription() != null) {
                    existingCenterImages.setDescription(centerImages.getDescription());
                }
                if (centerImages.getShow() != null) {
                    existingCenterImages.setShow(centerImages.getShow());
                }

                return existingCenterImages;
            })
            .flatMap(centerImagesRepository::save);
    }

    /**
     * Get all the centerImages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Flux<CenterImages> findAll(Pageable pageable) {
        log.debug("Request to get all CenterImages");
        return centerImagesRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of centerImages available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return centerImagesRepository.count();
    }

    /**
     * Get one centerImages by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Mono<CenterImages> findOne(String id) {
        log.debug("Request to get CenterImages : {}", id);
        return centerImagesRepository.findById(id);
    }

    /**
     * Delete the centerImages by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(String id) {
        log.debug("Request to delete CenterImages : {}", id);
        return centerImagesRepository.deleteById(id);
    }
}
