package bd.gov.banbeis.repository;

import bd.gov.banbeis.domain.PostComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring Data MongoDB reactive repository for the PostComment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PostCommentRepository extends ReactiveMongoRepository<PostComment, String> {
    Flux<PostComment> findAllBy(Pageable pageable);
}
