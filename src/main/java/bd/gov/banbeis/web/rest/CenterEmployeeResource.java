package bd.gov.banbeis.web.rest;

import bd.gov.banbeis.domain.CenterEmployee;
import bd.gov.banbeis.repository.CenterEmployeeRepository;
import bd.gov.banbeis.service.CenterEmployeeService;
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
 * REST controller for managing {@link bd.gov.banbeis.domain.CenterEmployee}.
 */
@RestController
@RequestMapping("/api")
public class CenterEmployeeResource {

    private final Logger log = LoggerFactory.getLogger(CenterEmployeeResource.class);

    private static final String ENTITY_NAME = "centerEmployee";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CenterEmployeeService centerEmployeeService;

    private final CenterEmployeeRepository centerEmployeeRepository;

    public CenterEmployeeResource(CenterEmployeeService centerEmployeeService, CenterEmployeeRepository centerEmployeeRepository) {
        this.centerEmployeeService = centerEmployeeService;
        this.centerEmployeeRepository = centerEmployeeRepository;
    }

    /**
     * {@code POST  /center-employees} : Create a new centerEmployee.
     *
     * @param centerEmployee the centerEmployee to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new centerEmployee, or with status {@code 400 (Bad Request)} if the centerEmployee has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/center-employees")
    public Mono<ResponseEntity<CenterEmployee>> createCenterEmployee(@RequestBody CenterEmployee centerEmployee) throws URISyntaxException {
        log.debug("REST request to save CenterEmployee : {}", centerEmployee);
        if (centerEmployee.getId() != null) {
            throw new BadRequestAlertException("A new centerEmployee cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return centerEmployeeService
            .save(centerEmployee)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/center-employees/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /center-employees/:id} : Updates an existing centerEmployee.
     *
     * @param id the id of the centerEmployee to save.
     * @param centerEmployee the centerEmployee to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated centerEmployee,
     * or with status {@code 400 (Bad Request)} if the centerEmployee is not valid,
     * or with status {@code 500 (Internal Server Error)} if the centerEmployee couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/center-employees/{id}")
    public Mono<ResponseEntity<CenterEmployee>> updateCenterEmployee(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody CenterEmployee centerEmployee
    ) throws URISyntaxException {
        log.debug("REST request to update CenterEmployee : {}, {}", id, centerEmployee);
        if (centerEmployee.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, centerEmployee.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return centerEmployeeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return centerEmployeeService
                    .save(centerEmployee)
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
     * {@code PATCH  /center-employees/:id} : Partial updates given fields of an existing centerEmployee, field will ignore if it is null
     *
     * @param id the id of the centerEmployee to save.
     * @param centerEmployee the centerEmployee to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated centerEmployee,
     * or with status {@code 400 (Bad Request)} if the centerEmployee is not valid,
     * or with status {@code 404 (Not Found)} if the centerEmployee is not found,
     * or with status {@code 500 (Internal Server Error)} if the centerEmployee couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/center-employees/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<CenterEmployee>> partialUpdateCenterEmployee(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody CenterEmployee centerEmployee
    ) throws URISyntaxException {
        log.debug("REST request to partial update CenterEmployee partially : {}, {}", id, centerEmployee);
        if (centerEmployee.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, centerEmployee.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return centerEmployeeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<CenterEmployee> result = centerEmployeeService.partialUpdate(centerEmployee);

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
     * {@code GET  /center-employees} : get all the centerEmployees.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of centerEmployees in body.
     */
    @GetMapping("/center-employees")
    public Mono<ResponseEntity<List<CenterEmployee>>> getAllCenterEmployees(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of CenterEmployees");
        return centerEmployeeService
            .countAll()
            .zipWith(centerEmployeeService.findAll(pageable).collectList())
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
     * {@code GET  /center-employees/:id} : get the "id" centerEmployee.
     *
     * @param id the id of the centerEmployee to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the centerEmployee, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/center-employees/{id}")
    public Mono<ResponseEntity<CenterEmployee>> getCenterEmployee(@PathVariable String id) {
        log.debug("REST request to get CenterEmployee : {}", id);
        Mono<CenterEmployee> centerEmployee = centerEmployeeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(centerEmployee);
    }

    /**
     * {@code DELETE  /center-employees/:id} : delete the "id" centerEmployee.
     *
     * @param id the id of the centerEmployee to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/center-employees/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCenterEmployee(@PathVariable String id) {
        log.debug("REST request to delete CenterEmployee : {}", id);
        return centerEmployeeService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build()
            );
    }
}
