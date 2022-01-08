package bd.gov.banbeis.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import bd.gov.banbeis.IntegrationTest;
import bd.gov.banbeis.domain.Post;
import bd.gov.banbeis.repository.PostRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link PostResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class PostResourceIT {

    private static final LocalDate DEFAULT_POST_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_POST_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_BODY = "AAAAAAAAAA";
    private static final String UPDATED_BODY = "BBBBBBBBBB";

    private static final Boolean DEFAULT_PUBLISH = false;
    private static final Boolean UPDATED_PUBLISH = true;

    private static final Instant DEFAULT_PUBLISHED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PUBLISHED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/posts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Post post;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Post createEntity() {
        Post post = new Post()
            .postDate(DEFAULT_POST_DATE)
            .title(DEFAULT_TITLE)
            .body(DEFAULT_BODY)
            .publish(DEFAULT_PUBLISH)
            .publishedOn(DEFAULT_PUBLISHED_ON);
        return post;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Post createUpdatedEntity() {
        Post post = new Post()
            .postDate(UPDATED_POST_DATE)
            .title(UPDATED_TITLE)
            .body(UPDATED_BODY)
            .publish(UPDATED_PUBLISH)
            .publishedOn(UPDATED_PUBLISHED_ON);
        return post;
    }

    @BeforeEach
    public void initTest() {
        postRepository.deleteAll().block();
        post = createEntity();
    }

    @Test
    void createPost() throws Exception {
        int databaseSizeBeforeCreate = postRepository.findAll().collectList().block().size();
        // Create the Post
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(post))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll().collectList().block();
        assertThat(postList).hasSize(databaseSizeBeforeCreate + 1);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getPostDate()).isEqualTo(DEFAULT_POST_DATE);
        assertThat(testPost.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testPost.getBody()).isEqualTo(DEFAULT_BODY);
        assertThat(testPost.getPublish()).isEqualTo(DEFAULT_PUBLISH);
        assertThat(testPost.getPublishedOn()).isEqualTo(DEFAULT_PUBLISHED_ON);
    }

    @Test
    void createPostWithExistingId() throws Exception {
        // Create the Post with an existing ID
        post.setId("existing_id");

        int databaseSizeBeforeCreate = postRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(post))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll().collectList().block();
        assertThat(postList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPosts() {
        // Initialize the database
        postRepository.save(post).block();

        // Get all the postList
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
            .value(hasItem(post.getId()))
            .jsonPath("$.[*].postDate")
            .value(hasItem(DEFAULT_POST_DATE.toString()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].body")
            .value(hasItem(DEFAULT_BODY.toString()))
            .jsonPath("$.[*].publish")
            .value(hasItem(DEFAULT_PUBLISH.booleanValue()))
            .jsonPath("$.[*].publishedOn")
            .value(hasItem(DEFAULT_PUBLISHED_ON.toString()));
    }

    @Test
    void getPost() {
        // Initialize the database
        postRepository.save(post).block();

        // Get the post
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, post.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(post.getId()))
            .jsonPath("$.postDate")
            .value(is(DEFAULT_POST_DATE.toString()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.body")
            .value(is(DEFAULT_BODY.toString()))
            .jsonPath("$.publish")
            .value(is(DEFAULT_PUBLISH.booleanValue()))
            .jsonPath("$.publishedOn")
            .value(is(DEFAULT_PUBLISHED_ON.toString()));
    }

    @Test
    void getNonExistingPost() {
        // Get the post
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPost() throws Exception {
        // Initialize the database
        postRepository.save(post).block();

        int databaseSizeBeforeUpdate = postRepository.findAll().collectList().block().size();

        // Update the post
        Post updatedPost = postRepository.findById(post.getId()).block();
        updatedPost
            .postDate(UPDATED_POST_DATE)
            .title(UPDATED_TITLE)
            .body(UPDATED_BODY)
            .publish(UPDATED_PUBLISH)
            .publishedOn(UPDATED_PUBLISHED_ON);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPost.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPost))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll().collectList().block();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getPostDate()).isEqualTo(UPDATED_POST_DATE);
        assertThat(testPost.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testPost.getBody()).isEqualTo(UPDATED_BODY);
        assertThat(testPost.getPublish()).isEqualTo(UPDATED_PUBLISH);
        assertThat(testPost.getPublishedOn()).isEqualTo(UPDATED_PUBLISHED_ON);
    }

    @Test
    void putNonExistingPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().collectList().block().size();
        post.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, post.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(post))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll().collectList().block();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().collectList().block().size();
        post.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(post))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll().collectList().block();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().collectList().block().size();
        post.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(post))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll().collectList().block();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePostWithPatch() throws Exception {
        // Initialize the database
        postRepository.save(post).block();

        int databaseSizeBeforeUpdate = postRepository.findAll().collectList().block().size();

        // Update the post using partial update
        Post partialUpdatedPost = new Post();
        partialUpdatedPost.setId(post.getId());

        partialUpdatedPost.publish(UPDATED_PUBLISH);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPost.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPost))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll().collectList().block();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getPostDate()).isEqualTo(DEFAULT_POST_DATE);
        assertThat(testPost.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testPost.getBody()).isEqualTo(DEFAULT_BODY);
        assertThat(testPost.getPublish()).isEqualTo(UPDATED_PUBLISH);
        assertThat(testPost.getPublishedOn()).isEqualTo(DEFAULT_PUBLISHED_ON);
    }

    @Test
    void fullUpdatePostWithPatch() throws Exception {
        // Initialize the database
        postRepository.save(post).block();

        int databaseSizeBeforeUpdate = postRepository.findAll().collectList().block().size();

        // Update the post using partial update
        Post partialUpdatedPost = new Post();
        partialUpdatedPost.setId(post.getId());

        partialUpdatedPost
            .postDate(UPDATED_POST_DATE)
            .title(UPDATED_TITLE)
            .body(UPDATED_BODY)
            .publish(UPDATED_PUBLISH)
            .publishedOn(UPDATED_PUBLISHED_ON);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPost.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPost))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll().collectList().block();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getPostDate()).isEqualTo(UPDATED_POST_DATE);
        assertThat(testPost.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testPost.getBody()).isEqualTo(UPDATED_BODY);
        assertThat(testPost.getPublish()).isEqualTo(UPDATED_PUBLISH);
        assertThat(testPost.getPublishedOn()).isEqualTo(UPDATED_PUBLISHED_ON);
    }

    @Test
    void patchNonExistingPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().collectList().block().size();
        post.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, post.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(post))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll().collectList().block();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().collectList().block().size();
        post.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(post))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll().collectList().block();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().collectList().block().size();
        post.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(post))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll().collectList().block();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePost() {
        // Initialize the database
        postRepository.save(post).block();

        int databaseSizeBeforeDelete = postRepository.findAll().collectList().block().size();

        // Delete the post
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, post.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Post> postList = postRepository.findAll().collectList().block();
        assertThat(postList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
