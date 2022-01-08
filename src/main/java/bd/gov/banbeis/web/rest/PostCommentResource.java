package bd.gov.banbeis.web.rest;

import bd.gov.banbeis.domain.PostComment;
import bd.gov.banbeis.repository.PostCommentRepository;
import bd.gov.banbeis.service.PostCommentService;
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
 * REST controller for managing {@link bd.gov.banbeis.domain.PostComment}.
 */
@RestController
@RequestMapping("/api")
public class PostCommentResource {

    private final Logger log = LoggerFactory.getLogger(PostCommentResource.class);

    private static final String ENTITY_NAME = "postComment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PostCommentService postCommentService;

    private final PostCommentRepository postCommentRepository;

    public PostCommentResource(PostCommentService postCommentService, PostCommentRepository postCommentRepository) {
        this.postCommentService = postCommentService;
        this.postCommentRepository = postCommentRepository;
    }

    /**
     * {@code POST  /post-comments} : Create a new postComment.
     *
     * @param postComment the postComment to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new postComment, or with status {@code 400 (Bad Request)} if the postComment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/post-comments")
    public Mono<ResponseEntity<PostComment>> createPostComment(@RequestBody PostComment postComment) throws URISyntaxException {
        log.debug("REST request to save PostComment : {}", postComment);
        if (postComment.getId() != null) {
            throw new BadRequestAlertException("A new postComment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return postCommentService
            .save(postComment)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/post-comments/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /post-comments/:id} : Updates an existing postComment.
     *
     * @param id the id of the postComment to save.
     * @param postComment the postComment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated postComment,
     * or with status {@code 400 (Bad Request)} if the postComment is not valid,
     * or with status {@code 500 (Internal Server Error)} if the postComment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/post-comments/{id}")
    public Mono<ResponseEntity<PostComment>> updatePostComment(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody PostComment postComment
    ) throws URISyntaxException {
        log.debug("REST request to update PostComment : {}, {}", id, postComment);
        if (postComment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, postComment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return postCommentRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return postCommentService
                    .save(postComment)
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
     * {@code PATCH  /post-comments/:id} : Partial updates given fields of an existing postComment, field will ignore if it is null
     *
     * @param id the id of the postComment to save.
     * @param postComment the postComment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated postComment,
     * or with status {@code 400 (Bad Request)} if the postComment is not valid,
     * or with status {@code 404 (Not Found)} if the postComment is not found,
     * or with status {@code 500 (Internal Server Error)} if the postComment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/post-comments/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<PostComment>> partialUpdatePostComment(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody PostComment postComment
    ) throws URISyntaxException {
        log.debug("REST request to partial update PostComment partially : {}, {}", id, postComment);
        if (postComment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, postComment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return postCommentRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<PostComment> result = postCommentService.partialUpdate(postComment);

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
     * {@code GET  /post-comments} : get all the postComments.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of postComments in body.
     */
    @GetMapping("/post-comments")
    public Mono<ResponseEntity<List<PostComment>>> getAllPostComments(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of PostComments");
        return postCommentService
            .countAll()
            .zipWith(postCommentService.findAll(pageable).collectList())
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
     * {@code GET  /post-comments/:id} : get the "id" postComment.
     *
     * @param id the id of the postComment to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the postComment, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/post-comments/{id}")
    public Mono<ResponseEntity<PostComment>> getPostComment(@PathVariable String id) {
        log.debug("REST request to get PostComment : {}", id);
        Mono<PostComment> postComment = postCommentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(postComment);
    }

    /**
     * {@code DELETE  /post-comments/:id} : delete the "id" postComment.
     *
     * @param id the id of the postComment to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/post-comments/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deletePostComment(@PathVariable String id) {
        log.debug("REST request to delete PostComment : {}", id);
        return postCommentService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build()
            );
    }
}
