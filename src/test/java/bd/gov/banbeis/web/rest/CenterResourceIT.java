package bd.gov.banbeis.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import bd.gov.banbeis.IntegrationTest;
import bd.gov.banbeis.domain.Center;
import bd.gov.banbeis.repository.CenterRepository;
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
 * Integration tests for the {@link CenterResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class CenterResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_LINE = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_LINE = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/centers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private CenterRepository centerRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Center center;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Center createEntity() {
        Center center = new Center()
            .name(DEFAULT_NAME)
            .addressLine(DEFAULT_ADDRESS_LINE)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE);
        return center;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Center createUpdatedEntity() {
        Center center = new Center()
            .name(UPDATED_NAME)
            .addressLine(UPDATED_ADDRESS_LINE)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);
        return center;
    }

    @BeforeEach
    public void initTest() {
        centerRepository.deleteAll().block();
        center = createEntity();
    }

    @Test
    void createCenter() throws Exception {
        int databaseSizeBeforeCreate = centerRepository.findAll().collectList().block().size();
        // Create the Center
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(center))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Center in the database
        List<Center> centerList = centerRepository.findAll().collectList().block();
        assertThat(centerList).hasSize(databaseSizeBeforeCreate + 1);
        Center testCenter = centerList.get(centerList.size() - 1);
        assertThat(testCenter.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCenter.getAddressLine()).isEqualTo(DEFAULT_ADDRESS_LINE);
        assertThat(testCenter.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testCenter.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
    }

    @Test
    void createCenterWithExistingId() throws Exception {
        // Create the Center with an existing ID
        center.setId("existing_id");

        int databaseSizeBeforeCreate = centerRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(center))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Center in the database
        List<Center> centerList = centerRepository.findAll().collectList().block();
        assertThat(centerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllCenters() {
        // Initialize the database
        centerRepository.save(center).block();

        // Get all the centerList
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
            .value(hasItem(center.getId()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].addressLine")
            .value(hasItem(DEFAULT_ADDRESS_LINE.toString()))
            .jsonPath("$.[*].imageContentType")
            .value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE))
            .jsonPath("$.[*].image")
            .value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE)));
    }

    @Test
    void getCenter() {
        // Initialize the database
        centerRepository.save(center).block();

        // Get the center
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, center.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(center.getId()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.addressLine")
            .value(is(DEFAULT_ADDRESS_LINE.toString()))
            .jsonPath("$.imageContentType")
            .value(is(DEFAULT_IMAGE_CONTENT_TYPE))
            .jsonPath("$.image")
            .value(is(Base64Utils.encodeToString(DEFAULT_IMAGE)));
    }

    @Test
    void getNonExistingCenter() {
        // Get the center
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCenter() throws Exception {
        // Initialize the database
        centerRepository.save(center).block();

        int databaseSizeBeforeUpdate = centerRepository.findAll().collectList().block().size();

        // Update the center
        Center updatedCenter = centerRepository.findById(center.getId()).block();
        updatedCenter
            .name(UPDATED_NAME)
            .addressLine(UPDATED_ADDRESS_LINE)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCenter.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCenter))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Center in the database
        List<Center> centerList = centerRepository.findAll().collectList().block();
        assertThat(centerList).hasSize(databaseSizeBeforeUpdate);
        Center testCenter = centerList.get(centerList.size() - 1);
        assertThat(testCenter.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCenter.getAddressLine()).isEqualTo(UPDATED_ADDRESS_LINE);
        assertThat(testCenter.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testCenter.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
    }

    @Test
    void putNonExistingCenter() throws Exception {
        int databaseSizeBeforeUpdate = centerRepository.findAll().collectList().block().size();
        center.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, center.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(center))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Center in the database
        List<Center> centerList = centerRepository.findAll().collectList().block();
        assertThat(centerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCenter() throws Exception {
        int databaseSizeBeforeUpdate = centerRepository.findAll().collectList().block().size();
        center.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(center))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Center in the database
        List<Center> centerList = centerRepository.findAll().collectList().block();
        assertThat(centerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCenter() throws Exception {
        int databaseSizeBeforeUpdate = centerRepository.findAll().collectList().block().size();
        center.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(center))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Center in the database
        List<Center> centerList = centerRepository.findAll().collectList().block();
        assertThat(centerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCenterWithPatch() throws Exception {
        // Initialize the database
        centerRepository.save(center).block();

        int databaseSizeBeforeUpdate = centerRepository.findAll().collectList().block().size();

        // Update the center using partial update
        Center partialUpdatedCenter = new Center();
        partialUpdatedCenter.setId(center.getId());

        partialUpdatedCenter.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCenter.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCenter))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Center in the database
        List<Center> centerList = centerRepository.findAll().collectList().block();
        assertThat(centerList).hasSize(databaseSizeBeforeUpdate);
        Center testCenter = centerList.get(centerList.size() - 1);
        assertThat(testCenter.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCenter.getAddressLine()).isEqualTo(DEFAULT_ADDRESS_LINE);
        assertThat(testCenter.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testCenter.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
    }

    @Test
    void fullUpdateCenterWithPatch() throws Exception {
        // Initialize the database
        centerRepository.save(center).block();

        int databaseSizeBeforeUpdate = centerRepository.findAll().collectList().block().size();

        // Update the center using partial update
        Center partialUpdatedCenter = new Center();
        partialUpdatedCenter.setId(center.getId());

        partialUpdatedCenter
            .name(UPDATED_NAME)
            .addressLine(UPDATED_ADDRESS_LINE)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCenter.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCenter))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Center in the database
        List<Center> centerList = centerRepository.findAll().collectList().block();
        assertThat(centerList).hasSize(databaseSizeBeforeUpdate);
        Center testCenter = centerList.get(centerList.size() - 1);
        assertThat(testCenter.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCenter.getAddressLine()).isEqualTo(UPDATED_ADDRESS_LINE);
        assertThat(testCenter.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testCenter.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
    }

    @Test
    void patchNonExistingCenter() throws Exception {
        int databaseSizeBeforeUpdate = centerRepository.findAll().collectList().block().size();
        center.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, center.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(center))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Center in the database
        List<Center> centerList = centerRepository.findAll().collectList().block();
        assertThat(centerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCenter() throws Exception {
        int databaseSizeBeforeUpdate = centerRepository.findAll().collectList().block().size();
        center.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(center))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Center in the database
        List<Center> centerList = centerRepository.findAll().collectList().block();
        assertThat(centerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCenter() throws Exception {
        int databaseSizeBeforeUpdate = centerRepository.findAll().collectList().block().size();
        center.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(center))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Center in the database
        List<Center> centerList = centerRepository.findAll().collectList().block();
        assertThat(centerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCenter() {
        // Initialize the database
        centerRepository.save(center).block();

        int databaseSizeBeforeDelete = centerRepository.findAll().collectList().block().size();

        // Delete the center
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, center.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Center> centerList = centerRepository.findAll().collectList().block();
        assertThat(centerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
