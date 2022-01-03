package bd.gov.banbeis.repository;

import bd.gov.banbeis.domain.Navigation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring Data MongoDB reactive repository for the Navigation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NavigationRepository extends ReactiveMongoRepository<Navigation, String> {
    Flux<Navigation> findAllBy(Pageable pageable);
}
