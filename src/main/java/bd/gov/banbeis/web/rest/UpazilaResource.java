package bd.gov.banbeis.web.rest;

import bd.gov.banbeis.domain.Upazila;
import bd.gov.banbeis.repository.UpazilaRepository;
import bd.gov.banbeis.service.UpazilaService;
import bd.gov.banbeis.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
 * REST controller for managing {@link bd.gov.banbeis.domain.Upazila}.
 */
@RestController
@RequestMapping("/api")
public class UpazilaResource {

    private final Logger log = LoggerFactory.getLogger(UpazilaResource.class);

    private static final String ENTITY_NAME = "upazila";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UpazilaService upazilaService;

    private final UpazilaRepository upazilaRepository;

    public UpazilaResource(UpazilaService upazilaService, UpazilaRepository upazilaRepository) {
        this.upazilaService = upazilaService;
        this.upazilaRepository = upazilaRepository;
    }

    /**
     * {@code POST  /upazilas} : Create a new upazila.
     *
     * @param upazila the upazila to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new upazila, or with status {@code 400 (Bad Request)} if the upazila has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/upazilas")
    public Mono<ResponseEntity<Upazila>> createUpazila(@RequestBody Upazila upazila) throws URISyntaxException {
        log.debug("REST request to save Upazila : {}", upazila);
        if (upazila.getId() != null) {
            throw new BadRequestAlertException("A new upazila cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return upazilaService
            .save(upazila)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/upazilas/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /upazilas/:id} : Updates an existing upazila.
     *
     * @param id the id of the upazila to save.
     * @param upazila the upazila to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated upazila,
     * or with status {@code 400 (Bad Request)} if the upazila is not valid,
     * or with status {@code 500 (Internal Server Error)} if the upazila couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/upazilas/{id}")
    public Mono<ResponseEntity<Upazila>> updateUpazila(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Upazila upazila
    ) throws URISyntaxException {
        log.debug("REST request to update Upazila : {}, {}", id, upazila);
        if (upazila.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, upazila.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return upazilaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return upazilaService
                    .save(upazila)
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
     * {@code PATCH  /upazilas/:id} : Partial updates given fields of an existing upazila, field will ignore if it is null
     *
     * @param id the id of the upazila to save.
     * @param upazila the upazila to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated upazila,
     * or with status {@code 400 (Bad Request)} if the upazila is not valid,
     * or with status {@code 404 (Not Found)} if the upazila is not found,
     * or with status {@code 500 (Internal Server Error)} if the upazila couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/upazilas/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Upazila>> partialUpdateUpazila(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Upazila upazila
    ) throws URISyntaxException {
        log.debug("REST request to partial update Upazila partially : {}, {}", id, upazila);
        if (upazila.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, upazila.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return upazilaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Upazila> result = upazilaService.partialUpdate(upazila);

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
     * {@code GET  /upazilas} : get all the upazilas.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of upazilas in body.
     */
    @GetMapping("/upazilas")
    public Mono<ResponseEntity<List<Upazila>>> getAllUpazilas(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Upazilas");
        return upazilaService
            .countAll()
            .zipWith(upazilaService.findAll(pageable).collectList())
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
     * {@code GET  /upazilas/:id} : get the "id" upazila.
     *
     * @param id the id of the upazila to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the upazila, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/upazilas/{id}")
    public Mono<ResponseEntity<Upazila>> getUpazila(@PathVariable String id) {
        log.debug("REST request to get Upazila : {}", id);
        Mono<Upazila> upazila = upazilaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(upazila);
    }

    /**
     * {@code DELETE  /upazilas/:id} : delete the "id" upazila.
     *
     * @param id the id of the upazila to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/upazilas/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteUpazila(@PathVariable String id) {
        log.debug("REST request to delete Upazila : {}", id);
        return upazilaService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build()
            );
    }
}
