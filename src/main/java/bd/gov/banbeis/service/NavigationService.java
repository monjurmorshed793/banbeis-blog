package bd.gov.banbeis.service;

import bd.gov.banbeis.domain.Navigation;
import bd.gov.banbeis.repository.NavigationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Navigation}.
 */
@Service
public class NavigationService {

    private final Logger log = LoggerFactory.getLogger(NavigationService.class);

    private final NavigationRepository navigationRepository;

    public NavigationService(NavigationRepository navigationRepository) {
        this.navigationRepository = navigationRepository;
    }

    /**
     * Save a navigation.
     *
     * @param navigation the entity to save.
     * @return the persisted entity.
     */
    public Mono<Navigation> save(Navigation navigation) {
        log.debug("Request to save Navigation : {}", navigation);
        return navigationRepository.save(navigation);
    }

    /**
     * Partially update a navigation.
     *
     * @param navigation the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Navigation> partialUpdate(Navigation navigation) {
        log.debug("Request to partially update Navigation : {}", navigation);

        return navigationRepository
            .findById(navigation.getId())
            .map(existingNavigation -> {
                if (navigation.getSequence() != null) {
                    existingNavigation.setSequence(navigation.getSequence());
                }
                if (navigation.getRoute() != null) {
                    existingNavigation.setRoute(navigation.getRoute());
                }
                if (navigation.getTitle() != null) {
                    existingNavigation.setTitle(navigation.getTitle());
                }
                if (navigation.getBreadCrumb() != null) {
                    existingNavigation.setBreadCrumb(navigation.getBreadCrumb());
                }
                if (navigation.getRoles() != null) {
                    existingNavigation.setRoles(navigation.getRoles());
                }

                return existingNavigation;
            })
            .flatMap(navigationRepository::save);
    }

    /**
     * Get all the navigations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Flux<Navigation> findAll(Pageable pageable) {
        log.debug("Request to get all Navigations");
        return navigationRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of navigations available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return navigationRepository.count();
    }

    /**
     * Get one navigation by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Mono<Navigation> findOne(String id) {
        log.debug("Request to get Navigation : {}", id);
        return navigationRepository.findById(id);
    }

    /**
     * Delete the navigation by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Navigation : {}", id);
        return navigationRepository.deleteById(id);
    }
}
