package bd.gov.banbeis.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import bd.gov.banbeis.IntegrationTest;
import bd.gov.banbeis.domain.CenterImages;
import bd.gov.banbeis.repository.CenterImagesRepository;
import java.time.Duration;
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
 * Integration tests for the {@link CenterImagesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class CenterImagesResourceIT {

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_SHOW = false;
    private static final Boolean UPDATED_SHOW = true;

    private static final String ENTITY_API_URL = "/api/center-images";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private CenterImagesRepository centerImagesRepository;

    @Autowired
    private WebTestClient webTestClient;

    private CenterImages centerImages;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CenterImages createEntity() {
        CenterImages centerImages = new CenterImages()
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE)
            .imageUrl(DEFAULT_IMAGE_URL)
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .show(DEFAULT_SHOW);
        return centerImages;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CenterImages createUpdatedEntity() {
        CenterImages centerImages = new CenterImages()
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .imageUrl(UPDATED_IMAGE_URL)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .show(UPDATED_SHOW);
        return centerImages;
    }

    @BeforeEach
    public void initTest() {
        centerImagesRepository.deleteAll().block();
        centerImages = createEntity();
    }

    @Test
    void createCenterImages() throws Exception {
        int databaseSizeBeforeCreate = centerImagesRepository.findAll().collectList().block().size();
        // Create the CenterImages
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centerImages))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the CenterImages in the database
        List<CenterImages> centerImagesList = centerImagesRepository.findAll().collectList().block();
        assertThat(centerImagesList).hasSize(databaseSizeBeforeCreate + 1);
        CenterImages testCenterImages = centerImagesList.get(centerImagesList.size() - 1);
        assertThat(testCenterImages.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testCenterImages.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testCenterImages.getImageUrl()).isEqualTo(DEFAULT_IMAGE_URL);
        assertThat(testCenterImages.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testCenterImages.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCenterImages.getShow()).isEqualTo(DEFAULT_SHOW);
    }

    @Test
    void createCenterImagesWithExistingId() throws Exception {
        // Create the CenterImages with an existing ID
        centerImages.setId("existing_id");

        int databaseSizeBeforeCreate = centerImagesRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centerImages))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CenterImages in the database
        List<CenterImages> centerImagesList = centerImagesRepository.findAll().collectList().block();
        assertThat(centerImagesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkImageUrlIsRequired() throws Exception {
        int databaseSizeBeforeTest = centerImagesRepository.findAll().collectList().block().size();
        // set the field null
        centerImages.setImageUrl(null);

        // Create the CenterImages, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centerImages))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<CenterImages> centerImagesList = centerImagesRepository.findAll().collectList().block();
        assertThat(centerImagesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = centerImagesRepository.findAll().collectList().block().size();
        // set the field null
        centerImages.setTitle(null);

        // Create the CenterImages, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centerImages))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<CenterImages> centerImagesList = centerImagesRepository.findAll().collectList().block();
        assertThat(centerImagesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllCenterImages() {
        // Initialize the database
        centerImagesRepository.save(centerImages).block();

        // Get all the centerImagesList
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
            .value(hasItem(centerImages.getId()))
            .jsonPath("$.[*].imageContentType")
            .value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE))
            .jsonPath("$.[*].image")
            .value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .jsonPath("$.[*].imageUrl")
            .value(hasItem(DEFAULT_IMAGE_URL))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].show")
            .value(hasItem(DEFAULT_SHOW.booleanValue()));
    }

    @Test
    void getCenterImages() {
        // Initialize the database
        centerImagesRepository.save(centerImages).block();

        // Get the centerImages
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, centerImages.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(centerImages.getId()))
            .jsonPath("$.imageContentType")
            .value(is(DEFAULT_IMAGE_CONTENT_TYPE))
            .jsonPath("$.image")
            .value(is(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .jsonPath("$.imageUrl")
            .value(is(DEFAULT_IMAGE_URL))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.show")
            .value(is(DEFAULT_SHOW.booleanValue()));
    }

    @Test
    void getNonExistingCenterImages() {
        // Get the centerImages
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCenterImages() throws Exception {
        // Initialize the database
        centerImagesRepository.save(centerImages).block();

        int databaseSizeBeforeUpdate = centerImagesRepository.findAll().collectList().block().size();

        // Update the centerImages
        CenterImages updatedCenterImages = centerImagesRepository.findById(centerImages.getId()).block();
        updatedCenterImages
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .imageUrl(UPDATED_IMAGE_URL)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .show(UPDATED_SHOW);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCenterImages.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCenterImages))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CenterImages in the database
        List<CenterImages> centerImagesList = centerImagesRepository.findAll().collectList().block();
        assertThat(centerImagesList).hasSize(databaseSizeBeforeUpdate);
        CenterImages testCenterImages = centerImagesList.get(centerImagesList.size() - 1);
        assertThat(testCenterImages.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testCenterImages.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testCenterImages.getImageUrl()).isEqualTo(UPDATED_IMAGE_URL);
        assertThat(testCenterImages.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testCenterImages.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCenterImages.getShow()).isEqualTo(UPDATED_SHOW);
    }

    @Test
    void putNonExistingCenterImages() throws Exception {
        int databaseSizeBeforeUpdate = centerImagesRepository.findAll().collectList().block().size();
        centerImages.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, centerImages.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centerImages))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CenterImages in the database
        List<CenterImages> centerImagesList = centerImagesRepository.findAll().collectList().block();
        assertThat(centerImagesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCenterImages() throws Exception {
        int databaseSizeBeforeUpdate = centerImagesRepository.findAll().collectList().block().size();
        centerImages.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centerImages))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CenterImages in the database
        List<CenterImages> centerImagesList = centerImagesRepository.findAll().collectList().block();
        assertThat(centerImagesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCenterImages() throws Exception {
        int databaseSizeBeforeUpdate = centerImagesRepository.findAll().collectList().block().size();
        centerImages.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centerImages))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CenterImages in the database
        List<CenterImages> centerImagesList = centerImagesRepository.findAll().collectList().block();
        assertThat(centerImagesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCenterImagesWithPatch() throws Exception {
        // Initialize the database
        centerImagesRepository.save(centerImages).block();

        int databaseSizeBeforeUpdate = centerImagesRepository.findAll().collectList().block().size();

        // Update the centerImages using partial update
        CenterImages partialUpdatedCenterImages = new CenterImages();
        partialUpdatedCenterImages.setId(centerImages.getId());

        partialUpdatedCenterImages.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCenterImages.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCenterImages))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CenterImages in the database
        List<CenterImages> centerImagesList = centerImagesRepository.findAll().collectList().block();
        assertThat(centerImagesList).hasSize(databaseSizeBeforeUpdate);
        CenterImages testCenterImages = centerImagesList.get(centerImagesList.size() - 1);
        assertThat(testCenterImages.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testCenterImages.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testCenterImages.getImageUrl()).isEqualTo(DEFAULT_IMAGE_URL);
        assertThat(testCenterImages.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testCenterImages.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCenterImages.getShow()).isEqualTo(DEFAULT_SHOW);
    }

    @Test
    void fullUpdateCenterImagesWithPatch() throws Exception {
        // Initialize the database
        centerImagesRepository.save(centerImages).block();

        int databaseSizeBeforeUpdate = centerImagesRepository.findAll().collectList().block().size();

        // Update the centerImages using partial update
        CenterImages partialUpdatedCenterImages = new CenterImages();
        partialUpdatedCenterImages.setId(centerImages.getId());

        partialUpdatedCenterImages
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .imageUrl(UPDATED_IMAGE_URL)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .show(UPDATED_SHOW);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCenterImages.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCenterImages))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CenterImages in the database
        List<CenterImages> centerImagesList = centerImagesRepository.findAll().collectList().block();
        assertThat(centerImagesList).hasSize(databaseSizeBeforeUpdate);
        CenterImages testCenterImages = centerImagesList.get(centerImagesList.size() - 1);
        assertThat(testCenterImages.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testCenterImages.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testCenterImages.getImageUrl()).isEqualTo(UPDATED_IMAGE_URL);
        assertThat(testCenterImages.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testCenterImages.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCenterImages.getShow()).isEqualTo(UPDATED_SHOW);
    }

    @Test
    void patchNonExistingCenterImages() throws Exception {
        int databaseSizeBeforeUpdate = centerImagesRepository.findAll().collectList().block().size();
        centerImages.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, centerImages.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(centerImages))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CenterImages in the database
        List<CenterImages> centerImagesList = centerImagesRepository.findAll().collectList().block();
        assertThat(centerImagesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCenterImages() throws Exception {
        int databaseSizeBeforeUpdate = centerImagesRepository.findAll().collectList().block().size();
        centerImages.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(centerImages))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CenterImages in the database
        List<CenterImages> centerImagesList = centerImagesRepository.findAll().collectList().block();
        assertThat(centerImagesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCenterImages() throws Exception {
        int databaseSizeBeforeUpdate = centerImagesRepository.findAll().collectList().block().size();
        centerImages.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(centerImages))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CenterImages in the database
        List<CenterImages> centerImagesList = centerImagesRepository.findAll().collectList().block();
        assertThat(centerImagesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCenterImages() {
        // Initialize the database
        centerImagesRepository.save(centerImages).block();

        int databaseSizeBeforeDelete = centerImagesRepository.findAll().collectList().block().size();

        // Delete the centerImages
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, centerImages.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<CenterImages> centerImagesList = centerImagesRepository.findAll().collectList().block();
        assertThat(centerImagesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
