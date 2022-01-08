package bd.gov.banbeis.web.rest;

import bd.gov.banbeis.domain.District;
import bd.gov.banbeis.repository.DistrictRepository;
import bd.gov.banbeis.service.DistrictService;
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
 * REST controller for managing {@link bd.gov.banbeis.domain.District}.
 */
@RestController
@RequestMapping("/api")
public class DistrictResource {

    private final Logger log = LoggerFactory.getLogger(DistrictResource.class);

    private static final String ENTITY_NAME = "district";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DistrictService districtService;

    private final DistrictRepository districtRepository;

    public DistrictResource(DistrictService districtService, DistrictRepository districtRepository) {
        this.districtService = districtService;
        this.districtRepository = districtRepository;
    }

    /**
     * {@code POST  /districts} : Create a new district.
     *
     * @param district the district to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new district, or with status {@code 400 (Bad Request)} if the district has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/districts")
    public Mono<ResponseEntity<District>> createDistrict(@RequestBody District district) throws URISyntaxException {
        log.debug("REST request to save District : {}", district);
        if (district.getId() != null) {
            throw new BadRequestAlertException("A new district cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return districtService
            .save(district)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/districts/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /districts/:id} : Updates an existing district.
     *
     * @param id the id of the district to save.
     * @param district the district to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated district,
     * or with status {@code 400 (Bad Request)} if the district is not valid,
     * or with status {@code 500 (Internal Server Error)} if the district couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/districts/{id}")
    public Mono<ResponseEntity<District>> updateDistrict(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody District district
    ) throws URISyntaxException {
        log.debug("REST request to update District : {}, {}", id, district);
        if (district.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, district.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return districtRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return districtService
                    .save(district)
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
     * {@code PATCH  /districts/:id} : Partial updates given fields of an existing district, field will ignore if it is null
     *
     * @param id the id of the district to save.
     * @param district the district to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated district,
     * or with status {@code 400 (Bad Request)} if the district is not valid,
     * or with status {@code 404 (Not Found)} if the district is not found,
     * or with status {@code 500 (Internal Server Error)} if the district couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/districts/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<District>> partialUpdateDistrict(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody District district
    ) throws URISyntaxException {
        log.debug("REST request to partial update District partially : {}, {}", id, district);
        if (district.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, district.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return districtRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<District> result = districtService.partialUpdate(district);

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
     * {@code GET  /districts} : get all the districts.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of districts in body.
     */
    @GetMapping("/districts")
    public Mono<ResponseEntity<List<District>>> getAllDistricts(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Districts");
        return districtService
            .countAll()
            .zipWith(districtService.findAll(pageable).collectList())
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
     * {@code GET  /districts/:id} : get the "id" district.
     *
     * @param id the id of the district to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the district, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/districts/{id}")
    public Mono<ResponseEntity<District>> getDistrict(@PathVariable String id) {
        log.debug("REST request to get District : {}", id);
        Mono<District> district = districtService.findOne(id);
        return ResponseUtil.wrapOrNotFound(district);
    }

    /**
     * {@code DELETE  /districts/:id} : delete the "id" district.
     *
     * @param id the id of the district to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/districts/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteDistrict(@PathVariable String id) {
        log.debug("REST request to delete District : {}", id);
        return districtService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build()
            );
    }
}
