package bd.gov.banbeis.repository;

import bd.gov.banbeis.domain.Division;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring Data MongoDB reactive repository for the Division entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DivisionRepository extends ReactiveMongoRepository<Division, String> {
    Flux<Division> findAllBy(Pageable pageable);
}
