package bd.gov.banbeis.web.rest;

import bd.gov.banbeis.domain.Center;
import bd.gov.banbeis.repository.CenterRepository;
import bd.gov.banbeis.service.CenterService;
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
 * REST controller for managing {@link bd.gov.banbeis.domain.Center}.
 */
@RestController
@RequestMapping("/api")
public class CenterResource {

    private final Logger log = LoggerFactory.getLogger(CenterResource.class);

    private static final String ENTITY_NAME = "center";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CenterService centerService;

    private final CenterRepository centerRepository;

    public CenterResource(CenterService centerService, CenterRepository centerRepository) {
        this.centerService = centerService;
        this.centerRepository = centerRepository;
    }

    /**
     * {@code POST  /centers} : Create a new center.
     *
     * @param center the center to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new center, or with status {@code 400 (Bad Request)} if the center has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/centers")
    public Mono<ResponseEntity<Center>> createCenter(@RequestBody Center center) throws URISyntaxException {
        log.debug("REST request to save Center : {}", center);
        if (center.getId() != null) {
            throw new BadRequestAlertException("A new center cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return centerService
            .save(center)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/centers/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /centers/:id} : Updates an existing center.
     *
     * @param id the id of the center to save.
     * @param center the center to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated center,
     * or with status {@code 400 (Bad Request)} if the center is not valid,
     * or with status {@code 500 (Internal Server Error)} if the center couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/centers/{id}")
    public Mono<ResponseEntity<Center>> updateCenter(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Center center
    ) throws URISyntaxException {
        log.debug("REST request to update Center : {}, {}", id, center);
        if (center.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, center.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return centerRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return centerService
                    .save(center)
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
     * {@code PATCH  /centers/:id} : Partial updates given fields of an existing center, field will ignore if it is null
     *
     * @param id the id of the center to save.
     * @param center the center to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated center,
     * or with status {@code 400 (Bad Request)} if the center is not valid,
     * or with status {@code 404 (Not Found)} if the center is not found,
     * or with status {@code 500 (Internal Server Error)} if the center couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/centers/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Center>> partialUpdateCenter(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Center center
    ) throws URISyntaxException {
        log.debug("REST request to partial update Center partially : {}, {}", id, center);
        if (center.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, center.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return centerRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Center> result = centerService.partialUpdate(center);

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
     * {@code GET  /centers} : get all the centers.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of centers in body.
     */
    @GetMapping("/centers")
    public Mono<ResponseEntity<List<Center>>> getAllCenters(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Centers");
        return centerService
            .countAll()
            .zipWith(centerService.findAll(pageable).collectList())
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
     * {@code GET  /centers/:id} : get the "id" center.
     *
     * @param id the id of the center to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the center, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/centers/{id}")
    public Mono<ResponseEntity<Center>> getCenter(@PathVariable String id) {
        log.debug("REST request to get Center : {}", id);
        Mono<Center> center = centerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(center);
    }

    /**
     * {@code DELETE  /centers/:id} : delete the "id" center.
     *
     * @param id the id of the center to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/centers/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCenter(@PathVariable String id) {
        log.debug("REST request to delete Center : {}", id);
        return centerService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build()
            );
    }
}
