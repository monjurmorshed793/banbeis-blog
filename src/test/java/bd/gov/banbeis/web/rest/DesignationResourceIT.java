package bd.gov.banbeis.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import bd.gov.banbeis.IntegrationTest;
import bd.gov.banbeis.domain.Designation;
import bd.gov.banbeis.repository.DesignationRepository;
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
 * Integration tests for the {@link DesignationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class DesignationResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SORT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SORT_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_GRADE = 1;
    private static final Integer UPDATED_GRADE = 2;

    private static final String ENTITY_API_URL = "/api/designations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private DesignationRepository designationRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Designation designation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Designation createEntity() {
        Designation designation = new Designation().name(DEFAULT_NAME).sortName(DEFAULT_SORT_NAME).grade(DEFAULT_GRADE);
        return designation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Designation createUpdatedEntity() {
        Designation designation = new Designation().name(UPDATED_NAME).sortName(UPDATED_SORT_NAME).grade(UPDATED_GRADE);
        return designation;
    }

    @BeforeEach
    public void initTest() {
        designationRepository.deleteAll().block();
        designation = createEntity();
    }

    @Test
    void createDesignation() throws Exception {
        int databaseSizeBeforeCreate = designationRepository.findAll().collectList().block().size();
        // Create the Designation
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(designation))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Designation in the database
        List<Designation> designationList = designationRepository.findAll().collectList().block();
        assertThat(designationList).hasSize(databaseSizeBeforeCreate + 1);
        Designation testDesignation = designationList.get(designationList.size() - 1);
        assertThat(testDesignation.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDesignation.getSortName()).isEqualTo(DEFAULT_SORT_NAME);
        assertThat(testDesignation.getGrade()).isEqualTo(DEFAULT_GRADE);
    }

    @Test
    void createDesignationWithExistingId() throws Exception {
        // Create the Designation with an existing ID
        designation.setId("existing_id");

        int databaseSizeBeforeCreate = designationRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(designation))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Designation in the database
        List<Designation> designationList = designationRepository.findAll().collectList().block();
        assertThat(designationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = designationRepository.findAll().collectList().block().size();
        // set the field null
        designation.setName(null);

        // Create the Designation, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(designation))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Designation> designationList = designationRepository.findAll().collectList().block();
        assertThat(designationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkSortNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = designationRepository.findAll().collectList().block().size();
        // set the field null
        designation.setSortName(null);

        // Create the Designation, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(designation))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Designation> designationList = designationRepository.findAll().collectList().block();
        assertThat(designationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllDesignations() {
        // Initialize the database
        designationRepository.save(designation).block();

        // Get all the designationList
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
            .value(hasItem(designation.getId()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].sortName")
            .value(hasItem(DEFAULT_SORT_NAME))
            .jsonPath("$.[*].grade")
            .value(hasItem(DEFAULT_GRADE));
    }

    @Test
    void getDesignation() {
        // Initialize the database
        designationRepository.save(designation).block();

        // Get the designation
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, designation.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(designation.getId()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.sortName")
            .value(is(DEFAULT_SORT_NAME))
            .jsonPath("$.grade")
            .value(is(DEFAULT_GRADE));
    }

    @Test
    void getNonExistingDesignation() {
        // Get the designation
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewDesignation() throws Exception {
        // Initialize the database
        designationRepository.save(designation).block();

        int databaseSizeBeforeUpdate = designationRepository.findAll().collectList().block().size();

        // Update the designation
        Designation updatedDesignation = designationRepository.findById(designation.getId()).block();
        updatedDesignation.name(UPDATED_NAME).sortName(UPDATED_SORT_NAME).grade(UPDATED_GRADE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedDesignation.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedDesignation))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Designation in the database
        List<Designation> designationList = designationRepository.findAll().collectList().block();
        assertThat(designationList).hasSize(databaseSizeBeforeUpdate);
        Designation testDesignation = designationList.get(designationList.size() - 1);
        assertThat(testDesignation.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDesignation.getSortName()).isEqualTo(UPDATED_SORT_NAME);
        assertThat(testDesignation.getGrade()).isEqualTo(UPDATED_GRADE);
    }

    @Test
    void putNonExistingDesignation() throws Exception {
        int databaseSizeBeforeUpdate = designationRepository.findAll().collectList().block().size();
        designation.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, designation.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(designation))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Designation in the database
        List<Designation> designationList = designationRepository.findAll().collectList().block();
        assertThat(designationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDesignation() throws Exception {
        int databaseSizeBeforeUpdate = designationRepository.findAll().collectList().block().size();
        designation.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(designation))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Designation in the database
        List<Designation> designationList = designationRepository.findAll().collectList().block();
        assertThat(designationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDesignation() throws Exception {
        int databaseSizeBeforeUpdate = designationRepository.findAll().collectList().block().size();
        designation.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(designation))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Designation in the database
        List<Designation> designationList = designationRepository.findAll().collectList().block();
        assertThat(designationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDesignationWithPatch() throws Exception {
        // Initialize the database
        designationRepository.save(designation).block();

        int databaseSizeBeforeUpdate = designationRepository.findAll().collectList().block().size();

        // Update the designation using partial update
        Designation partialUpdatedDesignation = new Designation();
        partialUpdatedDesignation.setId(designation.getId());

        partialUpdatedDesignation.name(UPDATED_NAME).sortName(UPDATED_SORT_NAME).grade(UPDATED_GRADE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDesignation.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedDesignation))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Designation in the database
        List<Designation> designationList = designationRepository.findAll().collectList().block();
        assertThat(designationList).hasSize(databaseSizeBeforeUpdate);
        Designation testDesignation = designationList.get(designationList.size() - 1);
        assertThat(testDesignation.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDesignation.getSortName()).isEqualTo(UPDATED_SORT_NAME);
        assertThat(testDesignation.getGrade()).isEqualTo(UPDATED_GRADE);
    }

    @Test
    void fullUpdateDesignationWithPatch() throws Exception {
        // Initialize the database
        designationRepository.save(designation).block();

        int databaseSizeBeforeUpdate = designationRepository.findAll().collectList().block().size();

        // Update the designation using partial update
        Designation partialUpdatedDesignation = new Designation();
        partialUpdatedDesignation.setId(designation.getId());

        partialUpdatedDesignation.name(UPDATED_NAME).sortName(UPDATED_SORT_NAME).grade(UPDATED_GRADE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDesignation.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedDesignation))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Designation in the database
        List<Designation> designationList = designationRepository.findAll().collectList().block();
        assertThat(designationList).hasSize(databaseSizeBeforeUpdate);
        Designation testDesignation = designationList.get(designationList.size() - 1);
        assertThat(testDesignation.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDesignation.getSortName()).isEqualTo(UPDATED_SORT_NAME);
        assertThat(testDesignation.getGrade()).isEqualTo(UPDATED_GRADE);
    }

    @Test
    void patchNonExistingDesignation() throws Exception {
        int databaseSizeBeforeUpdate = designationRepository.findAll().collectList().block().size();
        designation.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, designation.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(designation))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Designation in the database
        List<Designation> designationList = designationRepository.findAll().collectList().block();
        assertThat(designationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDesignation() throws Exception {
        int databaseSizeBeforeUpdate = designationRepository.findAll().collectList().block().size();
        designation.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(designation))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Designation in the database
        List<Designation> designationList = designationRepository.findAll().collectList().block();
        assertThat(designationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDesignation() throws Exception {
        int databaseSizeBeforeUpdate = designationRepository.findAll().collectList().block().size();
        designation.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(designation))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Designation in the database
        List<Designation> designationList = designationRepository.findAll().collectList().block();
        assertThat(designationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDesignation() {
        // Initialize the database
        designationRepository.save(designation).block();

        int databaseSizeBeforeDelete = designationRepository.findAll().collectList().block().size();

        // Delete the designation
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, designation.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Designation> designationList = designationRepository.findAll().collectList().block();
        assertThat(designationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
