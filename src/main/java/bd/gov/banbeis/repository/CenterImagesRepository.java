package bd.gov.banbeis.repository;

import bd.gov.banbeis.domain.CenterImages;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring Data MongoDB reactive repository for the CenterImages entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CenterImagesRepository extends ReactiveMongoRepository<CenterImages, String> {
    Flux<CenterImages> findAllBy(Pageable pageable);
}
