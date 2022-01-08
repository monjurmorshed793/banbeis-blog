package bd.gov.banbeis.service;

import bd.gov.banbeis.domain.Post;
import bd.gov.banbeis.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Post}.
 */
@Service
public class PostService {

    private final Logger log = LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /**
     * Save a post.
     *
     * @param post the entity to save.
     * @return the persisted entity.
     */
    public Mono<Post> save(Post post) {
        log.debug("Request to save Post : {}", post);
        return postRepository.save(post);
    }

    /**
     * Partially update a post.
     *
     * @param post the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Post> partialUpdate(Post post) {
        log.debug("Request to partially update Post : {}", post);

        return postRepository
            .findById(post.getId())
            .map(existingPost -> {
                if (post.getPostDate() != null) {
                    existingPost.setPostDate(post.getPostDate());
                }
                if (post.getTitle() != null) {
                    existingPost.setTitle(post.getTitle());
                }
                if (post.getBody() != null) {
                    existingPost.setBody(post.getBody());
                }
                if (post.getPublish() != null) {
                    existingPost.setPublish(post.getPublish());
                }
                if (post.getPublishedOn() != null) {
                    existingPost.setPublishedOn(post.getPublishedOn());
                }

                return existingPost;
            })
            .flatMap(postRepository::save);
    }

    /**
     * Get all the posts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Flux<Post> findAll(Pageable pageable) {
        log.debug("Request to get all Posts");
        return postRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of posts available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return postRepository.count();
    }

    /**
     * Get one post by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Mono<Post> findOne(String id) {
        log.debug("Request to get Post : {}", id);
        return postRepository.findById(id);
    }

    /**
     * Delete the post by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Post : {}", id);
        return postRepository.deleteById(id);
    }
}
