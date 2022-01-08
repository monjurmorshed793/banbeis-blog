package bd.gov.banbeis.repository;

import bd.gov.banbeis.domain.Designation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring Data MongoDB reactive repository for the Designation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DesignationRepository extends ReactiveMongoRepository<Designation, String> {
    Flux<Designation> findAllBy(Pageable pageable);
}
