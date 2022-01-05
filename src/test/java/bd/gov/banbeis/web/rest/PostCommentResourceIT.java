package bd.gov.banbeis.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import bd.gov.banbeis.IntegrationTest;
import bd.gov.banbeis.domain.PostComment;
import bd.gov.banbeis.domain.enumeration.CommentType;
import bd.gov.banbeis.repository.PostCommentRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link PostCommentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class PostCommentResourceIT {

    private static final String DEFAULT_COMMENTED_BY = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final CommentType DEFAULT_COMMENT_TYPE = CommentType.INITIAL_COMMENT;
    private static final CommentType UPDATED_COMMENT_TYPE = CommentType.REPLY;

    private static final Instant DEFAULT_COMMENTED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_COMMENTED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/post-comments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private WebTestClient webTestClient;

    private PostComment postComment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PostComment createEntity() {
        PostComment postComment = new PostComment()
            .commentedBy(DEFAULT_COMMENTED_BY)
            .comment(DEFAULT_COMMENT)
            .commentType(DEFAULT_COMMENT_TYPE)
            .commentedOn(DEFAULT_COMMENTED_ON);
        return postComment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PostComment createUpdatedEntity() {
        PostComment postComment = new PostComment()
            .commentedBy(UPDATED_COMMENTED_BY)
            .comment(UPDATED_COMMENT)
            .commentType(UPDATED_COMMENT_TYPE)
            .commentedOn(UPDATED_COMMENTED_ON);
        return postComment;
    }

    @BeforeEach
    public void initTest() {
        postCommentRepository.deleteAll().block();
        postComment = createEntity();
    }

    @Test
    void createPostComment() throws Exception {
        int databaseSizeBeforeCreate = postCommentRepository.findAll().collectList().block().size();
        // Create the PostComment
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(postComment))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the PostComment in the database
        List<PostComment> postCommentList = postCommentRepository.findAll().collectList().block();
        assertThat(postCommentList).hasSize(databaseSizeBeforeCreate + 1);
        PostComment testPostComment = postCommentList.get(postCommentList.size() - 1);
        assertThat(testPostComment.getCommentedBy()).isEqualTo(DEFAULT_COMMENTED_BY);
        assertThat(testPostComment.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testPostComment.getCommentType()).isEqualTo(DEFAULT_COMMENT_TYPE);
        assertThat(testPostComment.getCommentedOn()).isEqualTo(DEFAULT_COMMENTED_ON);
    }

    @Test
    void createPostCommentWithExistingId() throws Exception {
        // Create the PostComment with an existing ID
        postComment.setId("existing_id");

        int databaseSizeBeforeCreate = postCommentRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(postComment))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PostComment in the database
        List<PostComment> postCommentList = postCommentRepository.findAll().collectList().block();
        assertThat(postCommentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPostComments() {
        // Initialize the database
        postCommentRepository.save(postComment).block();

        // Get all the postCommentList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(postComment.getId()))
            .jsonPath("$.[*].commentedBy")
            .value(hasItem(DEFAULT_COMMENTED_BY))
            .jsonPath("$.[*].comment")
            .value(hasItem(DEFAULT_COMMENT.toString()))
            .jsonPath("$.[*].commentType")
            .value(hasItem(DEFAULT_COMMENT_TYPE.toString()))
            .jsonPath("$.[*].commentedOn")
            .value(hasItem(DEFAULT_COMMENTED_ON.toString()));
    }

    @Test
    void getPostComment() {
        // Initialize the database
        postCommentRepository.save(postComment).block();

        // Get the postComment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, postComment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(postComment.getId()))
            .jsonPath("$.commentedBy")
            .value(is(DEFAULT_COMMENTED_BY))
            .jsonPath("$.comment")
            .value(is(DEFAULT_COMMENT.toString()))
            .jsonPath("$.commentType")
            .value(is(DEFAULT_COMMENT_TYPE.toString()))
            .jsonPath("$.commentedOn")
            .value(is(DEFAULT_COMMENTED_ON.toString()));
    }

    @Test
    void getNonExistingPostComment() {
        // Get the postComment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPostComment() throws Exception {
        // Initialize the database
        postCommentRepository.save(postComment).block();

        int databaseSizeBeforeUpdate = postCommentRepository.findAll().collectList().block().size();

        // Update the postComment
        PostComment updatedPostComment = postCommentRepository.findById(postComment.getId()).block();
        updatedPostComment
            .commentedBy(UPDATED_COMMENTED_BY)
            .comment(UPDATED_COMMENT)
            .commentType(UPDATED_COMMENT_TYPE)
            .commentedOn(UPDATED_COMMENTED_ON);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPostComment.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPostComment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PostComment in the database
        List<PostComment> postCommentList = postCommentRepository.findAll().collectList().block();
        assertThat(postCommentList).hasSize(databaseSizeBeforeUpdate);
        PostComment testPostComment = postCommentList.get(postCommentList.size() - 1);
        assertThat(testPostComment.getCommentedBy()).isEqualTo(UPDATED_COMMENTED_BY);
        assertThat(testPostComment.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testPostComment.getCommentType()).isEqualTo(UPDATED_COMMENT_TYPE);
        assertThat(testPostComment.getCommentedOn()).isEqualTo(UPDATED_COMMENTED_ON);
    }

    @Test
    void putNonExistingPostComment() throws Exception {
        int databaseSizeBeforeUpdate = postCommentRepository.findAll().collectList().block().size();
        postComment.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, postComment.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(postComment))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PostComment in the database
        List<PostComment> postCommentList = postCommentRepository.findAll().collectList().block();
        assertThat(postCommentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPostComment() throws Exception {
        int databaseSizeBeforeUpdate = postCommentRepository.findAll().collectList().block().size();
        postComment.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(postComment))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PostComment in the database
        List<PostComment> postCommentList = postCommentRepository.findAll().collectList().block();
        assertThat(postCommentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPostComment() throws Exception {
        int databaseSizeBeforeUpdate = postCommentRepository.findAll().collectList().block().size();
        postComment.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(postComment))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PostComment in the database
        List<PostComment> postCommentList = postCommentRepository.findAll().collectList().block();
        assertThat(postCommentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePostCommentWithPatch() throws Exception {
        // Initialize the database
        postCommentRepository.save(postComment).block();

        int databaseSizeBeforeUpdate = postCommentRepository.findAll().collectList().block().size();

        // Update the postComment using partial update
        PostComment partialUpdatedPostComment = new PostComment();
        partialUpdatedPostComment.setId(postComment.getId());

        partialUpdatedPostComment.commentedBy(UPDATED_COMMENTED_BY).commentType(UPDATED_COMMENT_TYPE).commentedOn(UPDATED_COMMENTED_ON);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPostComment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPostComment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PostComment in the database
        List<PostComment> postCommentList = postCommentRepository.findAll().collectList().block();
        assertThat(postCommentList).hasSize(databaseSizeBeforeUpdate);
        PostComment testPostComment = postCommentList.get(postCommentList.size() - 1);
        assertThat(testPostComment.getCommentedBy()).isEqualTo(UPDATED_COMMENTED_BY);
        assertThat(testPostComment.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testPostComment.getCommentType()).isEqualTo(UPDATED_COMMENT_TYPE);
        assertThat(testPostComment.getCommentedOn()).isEqualTo(UPDATED_COMMENTED_ON);
    }

    @Test
    void fullUpdatePostCommentWithPatch() throws Exception {
        // Initialize the database
        postCommentRepository.save(postComment).block();

        int databaseSizeBeforeUpdate = postCommentRepository.findAll().collectList().block().size();

        // Update the postComment using partial update
        PostComment partialUpdatedPostComment = new PostComment();
        partialUpdatedPostComment.setId(postComment.getId());

        partialUpdatedPostComment
            .commentedBy(UPDATED_COMMENTED_BY)
            .comment(UPDATED_COMMENT)
            .commentType(UPDATED_COMMENT_TYPE)
            .commentedOn(UPDATED_COMMENTED_ON);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPostComment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPostComment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PostComment in the database
        List<PostComment> postCommentList = postCommentRepository.findAll().collectList().block();
        assertThat(postCommentList).hasSize(databaseSizeBeforeUpdate);
        PostComment testPostComment = postCommentList.get(postCommentList.size() - 1);
        assertThat(testPostComment.getCommentedBy()).isEqualTo(UPDATED_COMMENTED_BY);
        assertThat(testPostComment.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testPostComment.getCommentType()).isEqualTo(UPDATED_COMMENT_TYPE);
        assertThat(testPostComment.getCommentedOn()).isEqualTo(UPDATED_COMMENTED_ON);
    }

    @Test
    void patchNonExistingPostComment() throws Exception {
        int databaseSizeBeforeUpdate = postCommentRepository.findAll().collectList().block().size();
        postComment.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, postComment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(postComment))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PostComment in the database
        List<PostComment> postCommentList = postCommentRepository.findAll().collectList().block();
        assertThat(postCommentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPostComment() throws Exception {
        int databaseSizeBeforeUpdate = postCommentRepository.findAll().collectList().block().size();
        postComment.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(postComment))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PostComment in the database
        List<PostComment> postCommentList = postCommentRepository.findAll().collectList().block();
        assertThat(postCommentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPostComment() throws Exception {
        int databaseSizeBeforeUpdate = postCommentRepository.findAll().collectList().block().size();
        postComment.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(postComment))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PostComment in the database
        List<PostComment> postCommentList = postCommentRepository.findAll().collectList().block();
        assertThat(postCommentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePostComment() {
        // Initialize the database
        postCommentRepository.save(postComment).block();

        int databaseSizeBeforeDelete = postCommentRepository.findAll().collectList().block().size();

        // Delete the postComment
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, postComment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<PostComment> postCommentList = postCommentRepository.findAll().collectList().block();
        assertThat(postCommentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
