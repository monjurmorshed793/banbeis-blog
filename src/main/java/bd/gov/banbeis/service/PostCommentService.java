package bd.gov.banbeis.service;

import bd.gov.banbeis.domain.PostComment;
import bd.gov.banbeis.repository.PostCommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link PostComment}.
 */
@Service
public class PostCommentService {

    private final Logger log = LoggerFactory.getLogger(PostCommentService.class);

    private final PostCommentRepository postCommentRepository;

    public PostCommentService(PostCommentRepository postCommentRepository) {
        this.postCommentRepository = postCommentRepository;
    }

    /**
     * Save a postComment.
     *
     * @param postComment the entity to save.
     * @return the persisted entity.
     */
    public Mono<PostComment> save(PostComment postComment) {
        log.debug("Request to save PostComment : {}", postComment);
        return postCommentRepository.save(postComment);
    }

    /**
     * Partially update a postComment.
     *
     * @param postComment the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<PostComment> partialUpdate(PostComment postComment) {
        log.debug("Request to partially update PostComment : {}", postComment);

        return postCommentRepository
            .findById(postComment.getId())
            .map(existingPostComment -> {
                if (postComment.getCommentedBy() != null) {
                    existingPostComment.setCommentedBy(postComment.getCommentedBy());
                }
                if (postComment.getComment() != null) {
                    existingPostComment.setComment(postComment.getComment());
                }
                if (postComment.getCommentType() != null) {
                    existingPostComment.setCommentType(postComment.getCommentType());
                }
                if (postComment.getCommentedOn() != null) {
                    existingPostComment.setCommentedOn(postComment.getCommentedOn());
                }

                return existingPostComment;
            })
            .flatMap(postCommentRepository::save);
    }

    /**
     * Get all the postComments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Flux<PostComment> findAll(Pageable pageable) {
        log.debug("Request to get all PostComments");
        return postCommentRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of postComments available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return postCommentRepository.count();
    }

    /**
     * Get one postComment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Mono<PostComment> findOne(String id) {
        log.debug("Request to get PostComment : {}", id);
        return postCommentRepository.findById(id);
    }

    /**
     * Delete the postComment by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(String id) {
        log.debug("Request to delete PostComment : {}", id);
        return postCommentRepository.deleteById(id);
    }
}
