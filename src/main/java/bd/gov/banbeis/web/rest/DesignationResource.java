package bd.gov.banbeis.web.rest;

import bd.gov.banbeis.domain.Designation;
import bd.gov.banbeis.repository.DesignationRepository;
import bd.gov.banbeis.service.DesignationService;
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
 * REST controller for managing {@link bd.gov.banbeis.domain.Designation}.
 */
@RestController
@RequestMapping("/api")
public class DesignationResource {

    private final Logger log = LoggerFactory.getLogger(DesignationResource.class);

    private static final String ENTITY_NAME = "designation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DesignationService designationService;

    private final DesignationRepository designationRepository;

    public DesignationResource(DesignationService designationService, DesignationRepository designationRepository) {
        this.designationService = designationService;
        this.designationRepository = designationRepository;
    }

    /**
     * {@code POST  /designations} : Create a new designation.
     *
     * @param designation the designation to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new designation, or with status {@code 400 (Bad Request)} if the designation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/designations")
    public Mono<ResponseEntity<Designation>> createDesignation(@Valid @RequestBody Designation designation) throws URISyntaxException {
        log.debug("REST request to save Designation : {}", designation);
        if (designation.getId() != null) {
            throw new BadRequestAlertException("A new designation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return designationService
            .save(designation)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/designations/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /designations/:id} : Updates an existing designation.
     *
     * @param id the id of the designation to save.
     * @param designation the designation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated designation,
     * or with status {@code 400 (Bad Request)} if the designation is not valid,
     * or with status {@code 500 (Internal Server Error)} if the designation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/designations/{id}")
    public Mono<ResponseEntity<Designation>> updateDesignation(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody Designation designation
    ) throws URISyntaxException {
        log.debug("REST request to update Designation : {}, {}", id, designation);
        if (designation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, designation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return designationRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return designationService
                    .save(designation)
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
     * {@code PATCH  /designations/:id} : Partial updates given fields of an existing designation, field will ignore if it is null
     *
     * @param id the id of the designation to save.
     * @param designation the designation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated designation,
     * or with status {@code 400 (Bad Request)} if the designation is not valid,
     * or with status {@code 404 (Not Found)} if the designation is not found,
     * or with status {@code 500 (Internal Server Error)} if the designation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/designations/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Designation>> partialUpdateDesignation(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody Designation designation
    ) throws URISyntaxException {
        log.debug("REST request to partial update Designation partially : {}, {}", id, designation);
        if (designation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, designation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return designationRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Designation> result = designationService.partialUpdate(designation);

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
     * {@code GET  /designations} : get all the designations.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of designations in body.
     */
    @GetMapping("/designations")
    public Mono<ResponseEntity<List<Designation>>> getAllDesignations(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Designations");
        return designationService
            .countAll()
            .zipWith(designationService.findAll(pageable).collectList())
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
     * {@code GET  /designations/:id} : get the "id" designation.
     *
     * @param id the id of the designation to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the designation, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/designations/{id}")
    public Mono<ResponseEntity<Designation>> getDesignation(@PathVariable String id) {
        log.debug("REST request to get Designation : {}", id);
        Mono<Designation> designation = designationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(designation);
    }

    /**
     * {@code DELETE  /designations/:id} : delete the "id" designation.
     *
     * @param id the id of the designation to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/designations/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteDesignation(@PathVariable String id) {
        log.debug("REST request to delete Designation : {}", id);
        return designationService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build()
            );
    }
}
