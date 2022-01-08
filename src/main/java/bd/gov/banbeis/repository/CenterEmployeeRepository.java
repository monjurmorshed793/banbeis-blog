package bd.gov.banbeis.repository;

import bd.gov.banbeis.domain.CenterEmployee;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring Data MongoDB reactive repository for the CenterEmployee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CenterEmployeeRepository extends ReactiveMongoRepository<CenterEmployee, String> {
    Flux<CenterEmployee> findAllBy(Pageable pageable);
}
