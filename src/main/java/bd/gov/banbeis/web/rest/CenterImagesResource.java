package bd.gov.banbeis.web.rest;

import bd.gov.banbeis.domain.CenterImages;
import bd.gov.banbeis.repository.CenterImagesRepository;
import bd.gov.banbeis.service.CenterImagesService;
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
 * REST controller for managing {@link bd.gov.banbeis.domain.CenterImages}.
 */
@RestController
@RequestMapping("/api")
public class CenterImagesResource {

    private final Logger log = LoggerFactory.getLogger(CenterImagesResource.class);

    private static final String ENTITY_NAME = "centerImages";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CenterImagesService centerImagesService;

    private final CenterImagesRepository centerImagesRepository;

    public CenterImagesResource(CenterImagesService centerImagesService, CenterImagesRepository centerImagesRepository) {
        this.centerImagesService = centerImagesService;
        this.centerImagesRepository = centerImagesRepository;
    }

    /**
     * {@code POST  /center-images} : Create a new centerImages.
     *
     * @param centerImages the centerImages to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new centerImages, or with status {@code 400 (Bad Request)} if the centerImages has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/center-images")
    public Mono<ResponseEntity<CenterImages>> createCenterImages(@Valid @RequestBody CenterImages centerImages) throws URISyntaxException {
        log.debug("REST request to save CenterImages : {}", centerImages);
        if (centerImages.getId() != null) {
            throw new BadRequestAlertException("A new centerImages cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return centerImagesService
            .save(centerImages)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/center-images/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /center-images/:id} : Updates an existing centerImages.
     *
     * @param id the id of the centerImages to save.
     * @param centerImages the centerImages to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated centerImages,
     * or with status {@code 400 (Bad Request)} if the centerImages is not valid,
     * or with status {@code 500 (Internal Server Error)} if the centerImages couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/center-images/{id}")
    public Mono<ResponseEntity<CenterImages>> updateCenterImages(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody CenterImages centerImages
    ) throws URISyntaxException {
        log.debug("REST request to update CenterImages : {}, {}", id, centerImages);
        if (centerImages.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, centerImages.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return centerImagesRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return centerImagesService
                    .save(centerImages)
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
     * {@code PATCH  /center-images/:id} : Partial updates given fields of an existing centerImages, field will ignore if it is null
     *
     * @param id the id of the centerImages to save.
     * @param centerImages the centerImages to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated centerImages,
     * or with status {@code 400 (Bad Request)} if the centerImages is not valid,
     * or with status {@code 404 (Not Found)} if the centerImages is not found,
     * or with status {@code 500 (Internal Server Error)} if the centerImages couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/center-images/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<CenterImages>> partialUpdateCenterImages(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody CenterImages centerImages
    ) throws URISyntaxException {
        log.debug("REST request to partial update CenterImages partially : {}, {}", id, centerImages);
        if (centerImages.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, centerImages.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return centerImagesRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<CenterImages> result = centerImagesService.partialUpdate(centerImages);

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
     * {@code GET  /center-images} : get all the centerImages.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of centerImages in body.
     */
    @GetMapping("/center-images")
    public Mono<ResponseEntity<List<CenterImages>>> getAllCenterImages(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of CenterImages");
        return centerImagesService
            .countAll()
            .zipWith(centerImagesService.findAll(pageable).collectList())
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
     * {@code GET  /center-images/:id} : get the "id" centerImages.
     *
     * @param id the id of the centerImages to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the centerImages, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/center-images/{id}")
    public Mono<ResponseEntity<CenterImages>> getCenterImages(@PathVariable String id) {
        log.debug("REST request to get CenterImages : {}", id);
        Mono<CenterImages> centerImages = centerImagesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(centerImages);
    }

    /**
     * {@code DELETE  /center-images/:id} : delete the "id" centerImages.
     *
     * @param id the id of the centerImages to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/center-images/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCenterImages(@PathVariable String id) {
        log.debug("REST request to delete CenterImages : {}", id);
        return centerImagesService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build()
            );
    }
}
