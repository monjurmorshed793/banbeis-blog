package bd.gov.banbeis.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import bd.gov.banbeis.IntegrationTest;
import bd.gov.banbeis.domain.PostPhoto;
import bd.gov.banbeis.repository.PostPhotoRepository;
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
 * Integration tests for the {@link PostPhotoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class PostPhotoResourceIT {

    private static final Integer DEFAULT_SEQUENCE = 1;
    private static final Integer UPDATED_SEQUENCE = 2;

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final Instant DEFAULT_UPLOADED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPLOADED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/post-photos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private PostPhotoRepository postPhotoRepository;

    @Autowired
    private WebTestClient webTestClient;

    private PostPhoto postPhoto;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PostPhoto createEntity() {
        PostPhoto postPhoto = new PostPhoto()
            .sequence(DEFAULT_SEQUENCE)
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE)
            .uploadedOn(DEFAULT_UPLOADED_ON);
        return postPhoto;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PostPhoto createUpdatedEntity() {
        PostPhoto postPhoto = new PostPhoto()
            .sequence(UPDATED_SEQUENCE)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .uploadedOn(UPDATED_UPLOADED_ON);
        return postPhoto;
    }

    @BeforeEach
    public void initTest() {
        postPhotoRepository.deleteAll().block();
        postPhoto = createEntity();
    }

    @Test
    void createPostPhoto() throws Exception {
        int databaseSizeBeforeCreate = postPhotoRepository.findAll().collectList().block().size();
        // Create the PostPhoto
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(postPhoto))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the PostPhoto in the database
        List<PostPhoto> postPhotoList = postPhotoRepository.findAll().collectList().block();
        assertThat(postPhotoList).hasSize(databaseSizeBeforeCreate + 1);
        PostPhoto testPostPhoto = postPhotoList.get(postPhotoList.size() - 1);
        assertThat(testPostPhoto.getSequence()).isEqualTo(DEFAULT_SEQUENCE);
        assertThat(testPostPhoto.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testPostPhoto.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPostPhoto.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testPostPhoto.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testPostPhoto.getUploadedOn()).isEqualTo(DEFAULT_UPLOADED_ON);
    }

    @Test
    void createPostPhotoWithExistingId() throws Exception {
        // Create the PostPhoto with an existing ID
        postPhoto.setId("existing_id");

        int databaseSizeBeforeCreate = postPhotoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(postPhoto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PostPhoto in the database
        List<PostPhoto> postPhotoList = postPhotoRepository.findAll().collectList().block();
        assertThat(postPhotoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPostPhotos() {
        // Initialize the database
        postPhotoRepository.save(postPhoto).block();

        // Get all the postPhotoList
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
            .value(hasItem(postPhoto.getId()))
            .jsonPath("$.[*].sequence")
            .value(hasItem(DEFAULT_SEQUENCE))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].imageContentType")
            .value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE))
            .jsonPath("$.[*].image")
            .value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .jsonPath("$.[*].uploadedOn")
            .value(hasItem(DEFAULT_UPLOADED_ON.toString()));
    }

    @Test
    void getPostPhoto() {
        // Initialize the database
        postPhotoRepository.save(postPhoto).block();

        // Get the postPhoto
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, postPhoto.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(postPhoto.getId()))
            .jsonPath("$.sequence")
            .value(is(DEFAULT_SEQUENCE))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.imageContentType")
            .value(is(DEFAULT_IMAGE_CONTENT_TYPE))
            .jsonPath("$.image")
            .value(is(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .jsonPath("$.uploadedOn")
            .value(is(DEFAULT_UPLOADED_ON.toString()));
    }

    @Test
    void getNonExistingPostPhoto() {
        // Get the postPhoto
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPostPhoto() throws Exception {
        // Initialize the database
        postPhotoRepository.save(postPhoto).block();

        int databaseSizeBeforeUpdate = postPhotoRepository.findAll().collectList().block().size();

        // Update the postPhoto
        PostPhoto updatedPostPhoto = postPhotoRepository.findById(postPhoto.getId()).block();
        updatedPostPhoto
            .sequence(UPDATED_SEQUENCE)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .uploadedOn(UPDATED_UPLOADED_ON);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPostPhoto.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPostPhoto))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PostPhoto in the database
        List<PostPhoto> postPhotoList = postPhotoRepository.findAll().collectList().block();
        assertThat(postPhotoList).hasSize(databaseSizeBeforeUpdate);
        PostPhoto testPostPhoto = postPhotoList.get(postPhotoList.size() - 1);
        assertThat(testPostPhoto.getSequence()).isEqualTo(UPDATED_SEQUENCE);
        assertThat(testPostPhoto.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testPostPhoto.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPostPhoto.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testPostPhoto.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testPostPhoto.getUploadedOn()).isEqualTo(UPDATED_UPLOADED_ON);
    }

    @Test
    void putNonExistingPostPhoto() throws Exception {
        int databaseSizeBeforeUpdate = postPhotoRepository.findAll().collectList().block().size();
        postPhoto.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, postPhoto.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(postPhoto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PostPhoto in the database
        List<PostPhoto> postPhotoList = postPhotoRepository.findAll().collectList().block();
        assertThat(postPhotoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPostPhoto() throws Exception {
        int databaseSizeBeforeUpdate = postPhotoRepository.findAll().collectList().block().size();
        postPhoto.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(postPhoto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PostPhoto in the database
        List<PostPhoto> postPhotoList = postPhotoRepository.findAll().collectList().block();
        assertThat(postPhotoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPostPhoto() throws Exception {
        int databaseSizeBeforeUpdate = postPhotoRepository.findAll().collectList().block().size();
        postPhoto.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(postPhoto))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PostPhoto in the database
        List<PostPhoto> postPhotoList = postPhotoRepository.findAll().collectList().block();
        assertThat(postPhotoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePostPhotoWithPatch() throws Exception {
        // Initialize the database
        postPhotoRepository.save(postPhoto).block();

        int databaseSizeBeforeUpdate = postPhotoRepository.findAll().collectList().block().size();

        // Update the postPhoto using partial update
        PostPhoto partialUpdatedPostPhoto = new PostPhoto();
        partialUpdatedPostPhoto.setId(postPhoto.getId());

        partialUpdatedPostPhoto
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPostPhoto.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPostPhoto))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PostPhoto in the database
        List<PostPhoto> postPhotoList = postPhotoRepository.findAll().collectList().block();
        assertThat(postPhotoList).hasSize(databaseSizeBeforeUpdate);
        PostPhoto testPostPhoto = postPhotoList.get(postPhotoList.size() - 1);
        assertThat(testPostPhoto.getSequence()).isEqualTo(DEFAULT_SEQUENCE);
        assertThat(testPostPhoto.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testPostPhoto.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPostPhoto.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testPostPhoto.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testPostPhoto.getUploadedOn()).isEqualTo(DEFAULT_UPLOADED_ON);
    }

    @Test
    void fullUpdatePostPhotoWithPatch() throws Exception {
        // Initialize the database
        postPhotoRepository.save(postPhoto).block();

        int databaseSizeBeforeUpdate = postPhotoRepository.findAll().collectList().block().size();

        // Update the postPhoto using partial update
        PostPhoto partialUpdatedPostPhoto = new PostPhoto();
        partialUpdatedPostPhoto.setId(postPhoto.getId());

        partialUpdatedPostPhoto
            .sequence(UPDATED_SEQUENCE)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .uploadedOn(UPDATED_UPLOADED_ON);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPostPhoto.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPostPhoto))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PostPhoto in the database
        List<PostPhoto> postPhotoList = postPhotoRepository.findAll().collectList().block();
        assertThat(postPhotoList).hasSize(databaseSizeBeforeUpdate);
        PostPhoto testPostPhoto = postPhotoList.get(postPhotoList.size() - 1);
        assertThat(testPostPhoto.getSequence()).isEqualTo(UPDATED_SEQUENCE);
        assertThat(testPostPhoto.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testPostPhoto.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPostPhoto.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testPostPhoto.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testPostPhoto.getUploadedOn()).isEqualTo(UPDATED_UPLOADED_ON);
    }

    @Test
    void patchNonExistingPostPhoto() throws Exception {
        int databaseSizeBeforeUpdate = postPhotoRepository.findAll().collectList().block().size();
        postPhoto.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, postPhoto.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(postPhoto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PostPhoto in the database
        List<PostPhoto> postPhotoList = postPhotoRepository.findAll().collectList().block();
        assertThat(postPhotoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPostPhoto() throws Exception {
        int databaseSizeBeforeUpdate = postPhotoRepository.findAll().collectList().block().size();
        postPhoto.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(postPhoto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PostPhoto in the database
        List<PostPhoto> postPhotoList = postPhotoRepository.findAll().collectList().block();
        assertThat(postPhotoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPostPhoto() throws Exception {
        int databaseSizeBeforeUpdate = postPhotoRepository.findAll().collectList().block().size();
        postPhoto.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(postPhoto))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PostPhoto in the database
        List<PostPhoto> postPhotoList = postPhotoRepository.findAll().collectList().block();
        assertThat(postPhotoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePostPhoto() {
        // Initialize the database
        postPhotoRepository.save(postPhoto).block();

        int databaseSizeBeforeDelete = postPhotoRepository.findAll().collectList().block().size();

        // Delete the postPhoto
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, postPhoto.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<PostPhoto> postPhotoList = postPhotoRepository.findAll().collectList().block();
        assertThat(postPhotoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
