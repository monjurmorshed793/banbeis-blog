package bd.gov.banbeis.web.rest;

import bd.gov.banbeis.domain.Navigation;
import bd.gov.banbeis.repository.NavigationRepository;
import bd.gov.banbeis.service.NavigationService;
import bd.gov.banbeis.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link bd.gov.banbeis.domain.Navigation}.
 */
@RestController
@RequestMapping("/api")
public class NavigationResource {

    private final Logger log = LoggerFactory.getLogger(NavigationResource.class);

    private static final String ENTITY_NAME = "navigation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NavigationService navigationService;

    private final NavigationRepository navigationRepository;

    public NavigationResource(NavigationService navigationService, NavigationRepository navigationRepository) {
        this.navigationService = navigationService;
        this.navigationRepository = navigationRepository;
    }

    /**
     * {@code POST  /navigations} : Create a new navigation.
     *
     * @param navigation the navigation to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new navigation, or with status {@code 400 (Bad Request)} if the navigation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/navigations")
    public Mono<ResponseEntity<Navigation>> createNavigation(@Valid @RequestBody Navigation navigation) throws URISyntaxException {
        log.debug("REST request to save Navigation : {}", navigation);
        if (navigation.getId() != null) {
            throw new BadRequestAlertException("A new navigation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return navigationService
            .save(navigation)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/navigations/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /navigations/:id} : Updates an existing navigation.
     *
     * @param id the id of the navigation to save.
     * @param navigation the navigation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated navigation,
     * or with status {@code 400 (Bad Request)} if the navigation is not valid,
     * or with status {@code 500 (Internal Server Error)} if the navigation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/navigations/{id}")
    public Mono<ResponseEntity<Navigation>> updateNavigation(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody Navigation navigation
    ) throws URISyntaxException {
        log.debug("REST request to update Navigation : {}, {}", id, navigation);
        if (navigation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, navigation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return navigationRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return navigationService
                    .save(navigation)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /navigations/:id} : Partial updates given fields of an existing navigation, field will ignore if it is null
     *
     * @param id the id of the navigation to save.
     * @param navigation the navigation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated navigation,
     * or with status {@code 400 (Bad Request)} if the navigation is not valid,
     * or with status {@code 404 (Not Found)} if the navigation is not found,
     * or with status {@code 500 (Internal Server Error)} if the navigation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/navigations/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Navigation>> partialUpdateNavigation(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody Navigation navigation
    ) throws URISyntaxException {
        log.debug("REST request to partial update Navigation partially : {}, {}", id, navigation);
        if (navigation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, navigation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return navigationRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Navigation> result = navigationService.partialUpdate(navigation);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /navigations} : get all the navigations.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of navigations in body.
     */
    @GetMapping("/navigations")
    public Mono<ResponseEntity<List<Navigation>>> getAllNavigations(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Navigations");
        return navigationService
            .countAll()
            .zipWith(navigationService.findAll(pageable).collectList())
            .map(countWithEntities -> {
                return ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2());
            });
    }

    /**
     * {@code GET  /navigations/:id} : get the "id" navigation.
     *
     * @param id the id of the navigation to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the navigation, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/navigations/{id}")
    public Mono<ResponseEntity<Navigation>> getNavigation(@PathVariable String id) {
        log.debug("REST request to get Navigation : {}", id);
        Mono<Navigation> navigation = navigationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(navigation);
    }

    /**
     * {@code DELETE  /navigations/:id} : delete the "id" navigation.
     *
     * @param id the id of the navigation to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/navigations/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteNavigation(@PathVariable String id) {
        log.debug("REST request to delete Navigation : {}", id);
        return navigationService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build()
            );
    }
}
