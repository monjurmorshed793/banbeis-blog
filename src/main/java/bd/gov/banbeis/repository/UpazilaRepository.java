package bd.gov.banbeis.repository;

import bd.gov.banbeis.domain.Upazila;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring Data MongoDB reactive repository for the Upazila entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UpazilaRepository extends ReactiveMongoRepository<Upazila, String> {
    Flux<Upazila> findAllBy(Pageable pageable);
}
