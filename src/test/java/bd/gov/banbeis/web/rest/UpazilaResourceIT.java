package bd.gov.banbeis.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import bd.gov.banbeis.IntegrationTest;
import bd.gov.banbeis.domain.Upazila;
import bd.gov.banbeis.repository.UpazilaRepository;
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

/**
 * Integration tests for the {@link UpazilaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class UpazilaResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_BN_NAME = "AAAAAAAAAA";
    private static final String UPDATED_BN_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/upazilas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private UpazilaRepository upazilaRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Upazila upazila;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Upazila createEntity() {
        Upazila upazila = new Upazila().name(DEFAULT_NAME).bnName(DEFAULT_BN_NAME).url(DEFAULT_URL);
        return upazila;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Upazila createUpdatedEntity() {
        Upazila upazila = new Upazila().name(UPDATED_NAME).bnName(UPDATED_BN_NAME).url(UPDATED_URL);
        return upazila;
    }

    @BeforeEach
    public void initTest() {
        upazilaRepository.deleteAll().block();
        upazila = createEntity();
    }

    @Test
    void createUpazila() throws Exception {
        int databaseSizeBeforeCreate = upazilaRepository.findAll().collectList().block().size();
        // Create the Upazila
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(upazila))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Upazila in the database
        List<Upazila> upazilaList = upazilaRepository.findAll().collectList().block();
        assertThat(upazilaList).hasSize(databaseSizeBeforeCreate + 1);
        Upazila testUpazila = upazilaList.get(upazilaList.size() - 1);
        assertThat(testUpazila.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testUpazila.getBnName()).isEqualTo(DEFAULT_BN_NAME);
        assertThat(testUpazila.getUrl()).isEqualTo(DEFAULT_URL);
    }

    @Test
    void createUpazilaWithExistingId() throws Exception {
        // Create the Upazila with an existing ID
        upazila.setId("existing_id");

        int databaseSizeBeforeCreate = upazilaRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(upazila))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Upazila in the database
        List<Upazila> upazilaList = upazilaRepository.findAll().collectList().block();
        assertThat(upazilaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllUpazilas() {
        // Initialize the database
        upazila.setId(UUID.randomUUID().toString());
        upazilaRepository.save(upazila).block();

        // Get all the upazilaList
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
            .value(hasItem(upazila.getId()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].bnName")
            .value(hasItem(DEFAULT_BN_NAME))
            .jsonPath("$.[*].url")
            .value(hasItem(DEFAULT_URL));
    }

    @Test
    void getUpazila() {
        // Initialize the database
        upazila.setId(UUID.randomUUID().toString());
        upazilaRepository.save(upazila).block();

        // Get the upazila
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, upazila.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(upazila.getId()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.bnName")
            .value(is(DEFAULT_BN_NAME))
            .jsonPath("$.url")
            .value(is(DEFAULT_URL));
    }

    @Test
    void getNonExistingUpazila() {
        // Get the upazila
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewUpazila() throws Exception {
        // Initialize the database
        upazila.setId(UUID.randomUUID().toString());
        upazilaRepository.save(upazila).block();

        int databaseSizeBeforeUpdate = upazilaRepository.findAll().collectList().block().size();

        // Update the upazila
        Upazila updatedUpazila = upazilaRepository.findById(upazila.getId()).block();
        updatedUpazila.name(UPDATED_NAME).bnName(UPDATED_BN_NAME).url(UPDATED_URL);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedUpazila.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedUpazila))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Upazila in the database
        List<Upazila> upazilaList = upazilaRepository.findAll().collectList().block();
        assertThat(upazilaList).hasSize(databaseSizeBeforeUpdate);
        Upazila testUpazila = upazilaList.get(upazilaList.size() - 1);
        assertThat(testUpazila.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUpazila.getBnName()).isEqualTo(UPDATED_BN_NAME);
        assertThat(testUpazila.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    void putNonExistingUpazila() throws Exception {
        int databaseSizeBeforeUpdate = upazilaRepository.findAll().collectList().block().size();
        upazila.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, upazila.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(upazila))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Upazila in the database
        List<Upazila> upazilaList = upazilaRepository.findAll().collectList().block();
        assertThat(upazilaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchUpazila() throws Exception {
        int databaseSizeBeforeUpdate = upazilaRepository.findAll().collectList().block().size();
        upazila.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(upazila))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Upazila in the database
        List<Upazila> upazilaList = upazilaRepository.findAll().collectList().block();
        assertThat(upazilaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamUpazila() throws Exception {
        int databaseSizeBeforeUpdate = upazilaRepository.findAll().collectList().block().size();
        upazila.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(upazila))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Upazila in the database
        List<Upazila> upazilaList = upazilaRepository.findAll().collectList().block();
        assertThat(upazilaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateUpazilaWithPatch() throws Exception {
        // Initialize the database
        upazila.setId(UUID.randomUUID().toString());
        upazilaRepository.save(upazila).block();

        int databaseSizeBeforeUpdate = upazilaRepository.findAll().collectList().block().size();

        // Update the upazila using partial update
        Upazila partialUpdatedUpazila = new Upazila();
        partialUpdatedUpazila.setId(upazila.getId());

        partialUpdatedUpazila.name(UPDATED_NAME).url(UPDATED_URL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUpazila.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedUpazila))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Upazila in the database
        List<Upazila> upazilaList = upazilaRepository.findAll().collectList().block();
        assertThat(upazilaList).hasSize(databaseSizeBeforeUpdate);
        Upazila testUpazila = upazilaList.get(upazilaList.size() - 1);
        assertThat(testUpazila.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUpazila.getBnName()).isEqualTo(DEFAULT_BN_NAME);
        assertThat(testUpazila.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    void fullUpdateUpazilaWithPatch() throws Exception {
        // Initialize the database
        upazila.setId(UUID.randomUUID().toString());
        upazilaRepository.save(upazila).block();

        int databaseSizeBeforeUpdate = upazilaRepository.findAll().collectList().block().size();

        // Update the upazila using partial update
        Upazila partialUpdatedUpazila = new Upazila();
        partialUpdatedUpazila.setId(upazila.getId());

        partialUpdatedUpazila.name(UPDATED_NAME).bnName(UPDATED_BN_NAME).url(UPDATED_URL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUpazila.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedUpazila))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Upazila in the database
        List<Upazila> upazilaList = upazilaRepository.findAll().collectList().block();
        assertThat(upazilaList).hasSize(databaseSizeBeforeUpdate);
        Upazila testUpazila = upazilaList.get(upazilaList.size() - 1);
        assertThat(testUpazila.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUpazila.getBnName()).isEqualTo(UPDATED_BN_NAME);
        assertThat(testUpazila.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    void patchNonExistingUpazila() throws Exception {
        int databaseSizeBeforeUpdate = upazilaRepository.findAll().collectList().block().size();
        upazila.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, upazila.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(upazila))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Upazila in the database
        List<Upazila> upazilaList = upazilaRepository.findAll().collectList().block();
        assertThat(upazilaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchUpazila() throws Exception {
        int databaseSizeBeforeUpdate = upazilaRepository.findAll().collectList().block().size();
        upazila.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(upazila))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Upazila in the database
        List<Upazila> upazilaList = upazilaRepository.findAll().collectList().block();
        assertThat(upazilaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamUpazila() throws Exception {
        int databaseSizeBeforeUpdate = upazilaRepository.findAll().collectList().block().size();
        upazila.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(upazila))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Upazila in the database
        List<Upazila> upazilaList = upazilaRepository.findAll().collectList().block();
        assertThat(upazilaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteUpazila() {
        // Initialize the database
        upazila.setId(UUID.randomUUID().toString());
        upazilaRepository.save(upazila).block();

        int databaseSizeBeforeDelete = upazilaRepository.findAll().collectList().block().size();

        // Delete the upazila
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, upazila.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Upazila> upazilaList = upazilaRepository.findAll().collectList().block();
        assertThat(upazilaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
