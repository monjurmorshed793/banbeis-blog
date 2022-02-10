package bd.gov.banbeis.web.rest;

import bd.gov.banbeis.domain.PostPhoto;
import bd.gov.banbeis.repository.PostPhotoRepository;
import bd.gov.banbeis.service.PostPhotoService;
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
 * REST controller for managing {@link bd.gov.banbeis.domain.PostPhoto}.
 */
@RestController
@RequestMapping("/api")
public class PostPhotoResource {

    private final Logger log = LoggerFactory.getLogger(PostPhotoResource.class);

    private static final String ENTITY_NAME = "postPhoto";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PostPhotoService postPhotoService;

    private final PostPhotoRepository postPhotoRepository;

    public PostPhotoResource(PostPhotoService postPhotoService, PostPhotoRepository postPhotoRepository) {
        this.postPhotoService = postPhotoService;
        this.postPhotoRepository = postPhotoRepository;
    }

    /**
     * {@code POST  /post-photos} : Create a new postPhoto.
     *
     * @param postPhoto the postPhoto to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new postPhoto, or with status {@code 400 (Bad Request)} if the postPhoto has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/post-photos")
    public Mono<ResponseEntity<PostPhoto>> createPostPhoto(@Valid @RequestBody PostPhoto postPhoto) throws URISyntaxException {
        log.debug("REST request to save PostPhoto : {}", postPhoto);
        if (postPhoto.getId() != null) {
            throw new BadRequestAlertException("A new postPhoto cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return postPhotoService
            .save(postPhoto)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/post-photos/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /post-photos/:id} : Updates an existing postPhoto.
     *
     * @param id the id of the postPhoto to save.
     * @param postPhoto the postPhoto to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated postPhoto,
     * or with status {@code 400 (Bad Request)} if the postPhoto is not valid,
     * or with status {@code 500 (Internal Server Error)} if the postPhoto couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/post-photos/{id}")
    public Mono<ResponseEntity<PostPhoto>> updatePostPhoto(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody PostPhoto postPhoto
    ) throws URISyntaxException {
        log.debug("REST request to update PostPhoto : {}, {}", id, postPhoto);
        if (postPhoto.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, postPhoto.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return postPhotoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return postPhotoService
                    .save(postPhoto)
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
     * {@code PATCH  /post-photos/:id} : Partial updates given fields of an existing postPhoto, field will ignore if it is null
     *
     * @param id the id of the postPhoto to save.
     * @param postPhoto the postPhoto to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated postPhoto,
     * or with status {@code 400 (Bad Request)} if the postPhoto is not valid,
     * or with status {@code 404 (Not Found)} if the postPhoto is not found,
     * or with status {@code 500 (Internal Server Error)} if the postPhoto couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/post-photos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<PostPhoto>> partialUpdatePostPhoto(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody PostPhoto postPhoto
    ) throws URISyntaxException {
        log.debug("REST request to partial update PostPhoto partially : {}, {}", id, postPhoto);
        if (postPhoto.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, postPhoto.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return postPhotoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<PostPhoto> result = postPhotoService.partialUpdate(postPhoto);

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
     * {@code GET  /post-photos} : get all the postPhotos.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of postPhotos in body.
     */
    @GetMapping("/post-photos")
    public Mono<ResponseEntity<List<PostPhoto>>> getAllPostPhotos(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of PostPhotos");
        return postPhotoService
            .countAll()
            .zipWith(postPhotoService.findAll(pageable).collectList())
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
     * {@code GET  /post-photos/:id} : get the "id" postPhoto.
     *
     * @param id the id of the postPhoto to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the postPhoto, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/post-photos/{id}")
    public Mono<ResponseEntity<PostPhoto>> getPostPhoto(@PathVariable String id) {
        log.debug("REST request to get PostPhoto : {}", id);
        Mono<PostPhoto> postPhoto = postPhotoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(postPhoto);
    }

    /**
     * {@code DELETE  /post-photos/:id} : delete the "id" postPhoto.
     *
     * @param id the id of the postPhoto to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/post-photos/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deletePostPhoto(@PathVariable String id) {
        log.debug("REST request to delete PostPhoto : {}", id);
        return postPhotoService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build()
            );
    }
}
