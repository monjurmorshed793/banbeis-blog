package bd.gov.banbeis.service;

import bd.gov.banbeis.domain.PostPhoto;
import bd.gov.banbeis.repository.PostPhotoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link PostPhoto}.
 */
@Service
public class PostPhotoService {

    private final Logger log = LoggerFactory.getLogger(PostPhotoService.class);

    private final PostPhotoRepository postPhotoRepository;

    public PostPhotoService(PostPhotoRepository postPhotoRepository) {
        this.postPhotoRepository = postPhotoRepository;
    }

    /**
     * Save a postPhoto.
     *
     * @param postPhoto the entity to save.
     * @return the persisted entity.
     */
    public Mono<PostPhoto> save(PostPhoto postPhoto) {
        log.debug("Request to save PostPhoto : {}", postPhoto);
        return postPhotoRepository.save(postPhoto);
    }

    /**
     * Partially update a postPhoto.
     *
     * @param postPhoto the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<PostPhoto> partialUpdate(PostPhoto postPhoto) {
        log.debug("Request to partially update PostPhoto : {}", postPhoto);

        return postPhotoRepository
            .findById(postPhoto.getId())
            .map(existingPostPhoto -> {
                if (postPhoto.getSequence() != null) {
                    existingPostPhoto.setSequence(postPhoto.getSequence());
                }
                if (postPhoto.getTitle() != null) {
                    existingPostPhoto.setTitle(postPhoto.getTitle());
                }
                if (postPhoto.getDescription() != null) {
                    existingPostPhoto.setDescription(postPhoto.getDescription());
                }
                if (postPhoto.getImage() != null) {
                    existingPostPhoto.setImage(postPhoto.getImage());
                }
                if (postPhoto.getImageContentType() != null) {
                    existingPostPhoto.setImageContentType(postPhoto.getImageContentType());
                }
                if (postPhoto.getImageUrl() != null) {
                    existingPostPhoto.setImageUrl(postPhoto.getImageUrl());
                }
                if (postPhoto.getUploadedOn() != null) {
                    existingPostPhoto.setUploadedOn(postPhoto.getUploadedOn());
                }

                return existingPostPhoto;
            })
            .flatMap(postPhotoRepository::save);
    }

    /**
     * Get all the postPhotos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Flux<PostPhoto> findAll(Pageable pageable) {
        log.debug("Request to get all PostPhotos");
        return postPhotoRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of postPhotos available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return postPhotoRepository.count();
    }

    /**
     * Get one postPhoto by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Mono<PostPhoto> findOne(String id) {
        log.debug("Request to get PostPhoto : {}", id);
        return postPhotoRepository.findById(id);
    }

    /**
     * Delete the postPhoto by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(String id) {
        log.debug("Request to delete PostPhoto : {}", id);
        return postPhotoRepository.deleteById(id);
    }
}
