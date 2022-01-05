package bd.gov.banbeis.repository;

import bd.gov.banbeis.domain.PostPhoto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring Data MongoDB reactive repository for the PostPhoto entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PostPhotoRepository extends ReactiveMongoRepository<PostPhoto, String> {
    Flux<PostPhoto> findAllBy(Pageable pageable);
}
