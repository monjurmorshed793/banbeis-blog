package bd.gov.banbeis.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import bd.gov.banbeis.IntegrationTest;
import bd.gov.banbeis.domain.Division;
import bd.gov.banbeis.repository.DivisionRepository;
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
 * Integration tests for the {@link DivisionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class DivisionResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_BN_NAME = "AAAAAAAAAA";
    private static final String UPDATED_BN_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/divisions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private DivisionRepository divisionRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Division division;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Division createEntity() {
        Division division = new Division().name(DEFAULT_NAME).bnName(DEFAULT_BN_NAME).url(DEFAULT_URL);
        return division;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Division createUpdatedEntity() {
        Division division = new Division().name(UPDATED_NAME).bnName(UPDATED_BN_NAME).url(UPDATED_URL);
        return division;
    }

    @BeforeEach
    public void initTest() {
        divisionRepository.deleteAll().block();
        division = createEntity();
    }

    @Test
    void createDivision() throws Exception {
        int databaseSizeBeforeCreate = divisionRepository.findAll().collectList().block().size();
        // Create the Division
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(division))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Division in the database
        List<Division> divisionList = divisionRepository.findAll().collectList().block();
        assertThat(divisionList).hasSize(databaseSizeBeforeCreate + 1);
        Division testDivision = divisionList.get(divisionList.size() - 1);
        assertThat(testDivision.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDivision.getBnName()).isEqualTo(DEFAULT_BN_NAME);
        assertThat(testDivision.getUrl()).isEqualTo(DEFAULT_URL);
    }

    @Test
    void createDivisionWithExistingId() throws Exception {
        // Create the Division with an existing ID
        division.setId("existing_id");

        int databaseSizeBeforeCreate = divisionRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(division))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Division in the database
        List<Division> divisionList = divisionRepository.findAll().collectList().block();
        assertThat(divisionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllDivisions() {
        // Initialize the database
        division.setId(UUID.randomUUID().toString());
        divisionRepository.save(division).block();

        // Get all the divisionList
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
            .value(hasItem(division.getId()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].bnName")
            .value(hasItem(DEFAULT_BN_NAME))
            .jsonPath("$.[*].url")
            .value(hasItem(DEFAULT_URL));
    }

    @Test
    void getDivision() {
        // Initialize the database
        division.setId(UUID.randomUUID().toString());
        divisionRepository.save(division).block();

        // Get the division
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, division.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(division.getId()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.bnName")
            .value(is(DEFAULT_BN_NAME))
            .jsonPath("$.url")
            .value(is(DEFAULT_URL));
    }

    @Test
    void getNonExistingDivision() {
        // Get the division
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewDivision() throws Exception {
        // Initialize the database
        division.setId(UUID.randomUUID().toString());
        divisionRepository.save(division).block();

        int databaseSizeBeforeUpdate = divisionRepository.findAll().collectList().block().size();

        // Update the division
        Division updatedDivision = divisionRepository.findById(division.getId()).block();
        updatedDivision.name(UPDATED_NAME).bnName(UPDATED_BN_NAME).url(UPDATED_URL);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedDivision.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedDivision))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Division in the database
        List<Division> divisionList = divisionRepository.findAll().collectList().block();
        assertThat(divisionList).hasSize(databaseSizeBeforeUpdate);
        Division testDivision = divisionList.get(divisionList.size() - 1);
        assertThat(testDivision.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDivision.getBnName()).isEqualTo(UPDATED_BN_NAME);
        assertThat(testDivision.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    void putNonExistingDivision() throws Exception {
        int databaseSizeBeforeUpdate = divisionRepository.findAll().collectList().block().size();
        division.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, division.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(division))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Division in the database
        List<Division> divisionList = divisionRepository.findAll().collectList().block();
        assertThat(divisionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDivision() throws Exception {
        int databaseSizeBeforeUpdate = divisionRepository.findAll().collectList().block().size();
        division.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(division))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Division in the database
        List<Division> divisionList = divisionRepository.findAll().collectList().block();
        assertThat(divisionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDivision() throws Exception {
        int databaseSizeBeforeUpdate = divisionRepository.findAll().collectList().block().size();
        division.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(division))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Division in the database
        List<Division> divisionList = divisionRepository.findAll().collectList().block();
        assertThat(divisionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDivisionWithPatch() throws Exception {
        // Initialize the database
        division.setId(UUID.randomUUID().toString());
        divisionRepository.save(division).block();

        int databaseSizeBeforeUpdate = divisionRepository.findAll().collectList().block().size();

        // Update the division using partial update
        Division partialUpdatedDivision = new Division();
        partialUpdatedDivision.setId(division.getId());

        partialUpdatedDivision.bnName(UPDATED_BN_NAME).url(UPDATED_URL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDivision.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedDivision))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Division in the database
        List<Division> divisionList = divisionRepository.findAll().collectList().block();
        assertThat(divisionList).hasSize(databaseSizeBeforeUpdate);
        Division testDivision = divisionList.get(divisionList.size() - 1);
        assertThat(testDivision.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDivision.getBnName()).isEqualTo(UPDATED_BN_NAME);
        assertThat(testDivision.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    void fullUpdateDivisionWithPatch() throws Exception {
        // Initialize the database
        division.setId(UUID.randomUUID().toString());
        divisionRepository.save(division).block();

        int databaseSizeBeforeUpdate = divisionRepository.findAll().collectList().block().size();

        // Update the division using partial update
        Division partialUpdatedDivision = new Division();
        partialUpdatedDivision.setId(division.getId());

        partialUpdatedDivision.name(UPDATED_NAME).bnName(UPDATED_BN_NAME).url(UPDATED_URL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDivision.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedDivision))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Division in the database
        List<Division> divisionList = divisionRepository.findAll().collectList().block();
        assertThat(divisionList).hasSize(databaseSizeBeforeUpdate);
        Division testDivision = divisionList.get(divisionList.size() - 1);
        assertThat(testDivision.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDivision.getBnName()).isEqualTo(UPDATED_BN_NAME);
        assertThat(testDivision.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    void patchNonExistingDivision() throws Exception {
        int databaseSizeBeforeUpdate = divisionRepository.findAll().collectList().block().size();
        division.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, division.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(division))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Division in the database
        List<Division> divisionList = divisionRepository.findAll().collectList().block();
        assertThat(divisionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDivision() throws Exception {
        int databaseSizeBeforeUpdate = divisionRepository.findAll().collectList().block().size();
        division.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(division))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Division in the database
        List<Division> divisionList = divisionRepository.findAll().collectList().block();
        assertThat(divisionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDivision() throws Exception {
        int databaseSizeBeforeUpdate = divisionRepository.findAll().collectList().block().size();
        division.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(division))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Division in the database
        List<Division> divisionList = divisionRepository.findAll().collectList().block();
        assertThat(divisionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDivision() {
        // Initialize the database
        division.setId(UUID.randomUUID().toString());
        divisionRepository.save(division).block();

        int databaseSizeBeforeDelete = divisionRepository.findAll().collectList().block().size();

        // Delete the division
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, division.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Division> divisionList = divisionRepository.findAll().collectList().block();
        assertThat(divisionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
