package bd.gov.banbeis.web.rest;

import bd.gov.banbeis.domain.Division;
import bd.gov.banbeis.repository.DivisionRepository;
import bd.gov.banbeis.service.DivisionService;
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
 * REST controller for managing {@link bd.gov.banbeis.domain.Division}.
 */
@RestController
@RequestMapping("/api")
public class DivisionResource {

    private final Logger log = LoggerFactory.getLogger(DivisionResource.class);

    private static final String ENTITY_NAME = "division";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DivisionService divisionService;

    private final DivisionRepository divisionRepository;

    public DivisionResource(DivisionService divisionService, DivisionRepository divisionRepository) {
        this.divisionService = divisionService;
        this.divisionRepository = divisionRepository;
    }

    /**
     * {@code POST  /divisions} : Create a new division.
     *
     * @param division the division to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new division, or with status {@code 400 (Bad Request)} if the division has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/divisions")
    public Mono<ResponseEntity<Division>> createDivision(@RequestBody Division division) throws URISyntaxException {
        log.debug("REST request to save Division : {}", division);
        if (division.getId() != null) {
            throw new BadRequestAlertException("A new division cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return divisionService
            .save(division)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/divisions/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /divisions/:id} : Updates an existing division.
     *
     * @param id the id of the division to save.
     * @param division the division to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated division,
     * or with status {@code 400 (Bad Request)} if the division is not valid,
     * or with status {@code 500 (Internal Server Error)} if the division couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/divisions/{id}")
    public Mono<ResponseEntity<Division>> updateDivision(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Division division
    ) throws URISyntaxException {
        log.debug("REST request to update Division : {}, {}", id, division);
        if (division.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, division.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return divisionRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return divisionService
                    .save(division)
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
     * {@code PATCH  /divisions/:id} : Partial updates given fields of an existing division, field will ignore if it is null
     *
     * @param id the id of the division to save.
     * @param division the division to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated division,
     * or with status {@code 400 (Bad Request)} if the division is not valid,
     * or with status {@code 404 (Not Found)} if the division is not found,
     * or with status {@code 500 (Internal Server Error)} if the division couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/divisions/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Division>> partialUpdateDivision(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Division division
    ) throws URISyntaxException {
        log.debug("REST request to partial update Division partially : {}, {}", id, division);
        if (division.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, division.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return divisionRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Division> result = divisionService.partialUpdate(division);

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
     * {@code GET  /divisions} : get all the divisions.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of divisions in body.
     */
    @GetMapping("/divisions")
    public Mono<ResponseEntity<List<Division>>> getAllDivisions(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Divisions");
        return divisionService
            .countAll()
            .zipWith(divisionService.findAll(pageable).collectList())
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
     * {@code GET  /divisions/:id} : get the "id" division.
     *
     * @param id the id of the division to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the division, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/divisions/{id}")
    public Mono<ResponseEntity<Division>> getDivision(@PathVariable String id) {
        log.debug("REST request to get Division : {}", id);
        Mono<Division> division = divisionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(division);
    }

    /**
     * {@code DELETE  /divisions/:id} : delete the "id" division.
     *
     * @param id the id of the division to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/divisions/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteDivision(@PathVariable String id) {
        log.debug("REST request to delete Division : {}", id);
        return divisionService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build()
            );
    }
}
