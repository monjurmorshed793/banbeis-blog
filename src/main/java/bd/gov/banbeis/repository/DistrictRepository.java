package bd.gov.banbeis.repository;

import bd.gov.banbeis.domain.District;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring Data MongoDB reactive repository for the District entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DistrictRepository extends ReactiveMongoRepository<District, String> {
    Flux<District> findAllBy(Pageable pageable);
}
