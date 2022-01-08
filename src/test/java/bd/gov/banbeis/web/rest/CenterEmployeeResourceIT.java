package bd.gov.banbeis.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import bd.gov.banbeis.IntegrationTest;
import bd.gov.banbeis.domain.CenterEmployee;
import bd.gov.banbeis.domain.enumeration.DutyType;
import bd.gov.banbeis.repository.CenterEmployeeRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link CenterEmployeeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class CenterEmployeeResourceIT {

    private static final DutyType DEFAULT_DUTY_TYPE = DutyType.MAIN;
    private static final DutyType UPDATED_DUTY_TYPE = DutyType.ADDITIONAL;

    private static final LocalDate DEFAULT_JOINING_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_JOINING_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_RELEASE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_RELEASE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/center-employees";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private CenterEmployeeRepository centerEmployeeRepository;

    @Autowired
    private WebTestClient webTestClient;

    private CenterEmployee centerEmployee;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CenterEmployee createEntity() {
        CenterEmployee centerEmployee = new CenterEmployee()
            .dutyType(DEFAULT_DUTY_TYPE)
            .joiningDate(DEFAULT_JOINING_DATE)
            .releaseDate(DEFAULT_RELEASE_DATE)
            .message(DEFAULT_MESSAGE);
        return centerEmployee;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CenterEmployee createUpdatedEntity() {
        CenterEmployee centerEmployee = new CenterEmployee()
            .dutyType(UPDATED_DUTY_TYPE)
            .joiningDate(UPDATED_JOINING_DATE)
            .releaseDate(UPDATED_RELEASE_DATE)
            .message(UPDATED_MESSAGE);
        return centerEmployee;
    }

    @BeforeEach
    public void initTest() {
        centerEmployeeRepository.deleteAll().block();
        centerEmployee = createEntity();
    }

    @Test
    void createCenterEmployee() throws Exception {
        int databaseSizeBeforeCreate = centerEmployeeRepository.findAll().collectList().block().size();
        // Create the CenterEmployee
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centerEmployee))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the CenterEmployee in the database
        List<CenterEmployee> centerEmployeeList = centerEmployeeRepository.findAll().collectList().block();
        assertThat(centerEmployeeList).hasSize(databaseSizeBeforeCreate + 1);
        CenterEmployee testCenterEmployee = centerEmployeeList.get(centerEmployeeList.size() - 1);
        assertThat(testCenterEmployee.getDutyType()).isEqualTo(DEFAULT_DUTY_TYPE);
        assertThat(testCenterEmployee.getJoiningDate()).isEqualTo(DEFAULT_JOINING_DATE);
        assertThat(testCenterEmployee.getReleaseDate()).isEqualTo(DEFAULT_RELEASE_DATE);
        assertThat(testCenterEmployee.getMessage()).isEqualTo(DEFAULT_MESSAGE);
    }

    @Test
    void createCenterEmployeeWithExistingId() throws Exception {
        // Create the CenterEmployee with an existing ID
        centerEmployee.setId("existing_id");

        int databaseSizeBeforeCreate = centerEmployeeRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centerEmployee))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CenterEmployee in the database
        List<CenterEmployee> centerEmployeeList = centerEmployeeRepository.findAll().collectList().block();
        assertThat(centerEmployeeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllCenterEmployees() {
        // Initialize the database
        centerEmployeeRepository.save(centerEmployee).block();

        // Get all the centerEmployeeList
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
            .value(hasItem(centerEmployee.getId()))
            .jsonPath("$.[*].dutyType")
            .value(hasItem(DEFAULT_DUTY_TYPE.toString()))
            .jsonPath("$.[*].joiningDate")
            .value(hasItem(DEFAULT_JOINING_DATE.toString()))
            .jsonPath("$.[*].releaseDate")
            .value(hasItem(DEFAULT_RELEASE_DATE.toString()))
            .jsonPath("$.[*].message")
            .value(hasItem(DEFAULT_MESSAGE.toString()));
    }

    @Test
    void getCenterEmployee() {
        // Initialize the database
        centerEmployeeRepository.save(centerEmployee).block();

        // Get the centerEmployee
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, centerEmployee.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(centerEmployee.getId()))
            .jsonPath("$.dutyType")
            .value(is(DEFAULT_DUTY_TYPE.toString()))
            .jsonPath("$.joiningDate")
            .value(is(DEFAULT_JOINING_DATE.toString()))
            .jsonPath("$.releaseDate")
            .value(is(DEFAULT_RELEASE_DATE.toString()))
            .jsonPath("$.message")
            .value(is(DEFAULT_MESSAGE.toString()));
    }

    @Test
    void getNonExistingCenterEmployee() {
        // Get the centerEmployee
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCenterEmployee() throws Exception {
        // Initialize the database
        centerEmployeeRepository.save(centerEmployee).block();

        int databaseSizeBeforeUpdate = centerEmployeeRepository.findAll().collectList().block().size();

        // Update the centerEmployee
        CenterEmployee updatedCenterEmployee = centerEmployeeRepository.findById(centerEmployee.getId()).block();
        updatedCenterEmployee
            .dutyType(UPDATED_DUTY_TYPE)
            .joiningDate(UPDATED_JOINING_DATE)
            .releaseDate(UPDATED_RELEASE_DATE)
            .message(UPDATED_MESSAGE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCenterEmployee.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCenterEmployee))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CenterEmployee in the database
        List<CenterEmployee> centerEmployeeList = centerEmployeeRepository.findAll().collectList().block();
        assertThat(centerEmployeeList).hasSize(databaseSizeBeforeUpdate);
        CenterEmployee testCenterEmployee = centerEmployeeList.get(centerEmployeeList.size() - 1);
        assertThat(testCenterEmployee.getDutyType()).isEqualTo(UPDATED_DUTY_TYPE);
        assertThat(testCenterEmployee.getJoiningDate()).isEqualTo(UPDATED_JOINING_DATE);
        assertThat(testCenterEmployee.getReleaseDate()).isEqualTo(UPDATED_RELEASE_DATE);
        assertThat(testCenterEmployee.getMessage()).isEqualTo(UPDATED_MESSAGE);
    }

    @Test
    void putNonExistingCenterEmployee() throws Exception {
        int databaseSizeBeforeUpdate = centerEmployeeRepository.findAll().collectList().block().size();
        centerEmployee.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, centerEmployee.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centerEmployee))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CenterEmployee in the database
        List<CenterEmployee> centerEmployeeList = centerEmployeeRepository.findAll().collectList().block();
        assertThat(centerEmployeeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCenterEmployee() throws Exception {
        int databaseSizeBeforeUpdate = centerEmployeeRepository.findAll().collectList().block().size();
        centerEmployee.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centerEmployee))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CenterEmployee in the database
        List<CenterEmployee> centerEmployeeList = centerEmployeeRepository.findAll().collectList().block();
        assertThat(centerEmployeeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCenterEmployee() throws Exception {
        int databaseSizeBeforeUpdate = centerEmployeeRepository.findAll().collectList().block().size();
        centerEmployee.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centerEmployee))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CenterEmployee in the database
        List<CenterEmployee> centerEmployeeList = centerEmployeeRepository.findAll().collectList().block();
        assertThat(centerEmployeeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCenterEmployeeWithPatch() throws Exception {
        // Initialize the database
        centerEmployeeRepository.save(centerEmployee).block();

        int databaseSizeBeforeUpdate = centerEmployeeRepository.findAll().collectList().block().size();

        // Update the centerEmployee using partial update
        CenterEmployee partialUpdatedCenterEmployee = new CenterEmployee();
        partialUpdatedCenterEmployee.setId(centerEmployee.getId());

        partialUpdatedCenterEmployee.joiningDate(UPDATED_JOINING_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCenterEmployee.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCenterEmployee))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CenterEmployee in the database
        List<CenterEmployee> centerEmployeeList = centerEmployeeRepository.findAll().collectList().block();
        assertThat(centerEmployeeList).hasSize(databaseSizeBeforeUpdate);
        CenterEmployee testCenterEmployee = centerEmployeeList.get(centerEmployeeList.size() - 1);
        assertThat(testCenterEmployee.getDutyType()).isEqualTo(DEFAULT_DUTY_TYPE);
        assertThat(testCenterEmployee.getJoiningDate()).isEqualTo(UPDATED_JOINING_DATE);
        assertThat(testCenterEmployee.getReleaseDate()).isEqualTo(DEFAULT_RELEASE_DATE);
        assertThat(testCenterEmployee.getMessage()).isEqualTo(DEFAULT_MESSAGE);
    }

    @Test
    void fullUpdateCenterEmployeeWithPatch() throws Exception {
        // Initialize the database
        centerEmployeeRepository.save(centerEmployee).block();

        int databaseSizeBeforeUpdate = centerEmployeeRepository.findAll().collectList().block().size();

        // Update the centerEmployee using partial update
        CenterEmployee partialUpdatedCenterEmployee = new CenterEmployee();
        partialUpdatedCenterEmployee.setId(centerEmployee.getId());

        partialUpdatedCenterEmployee
            .dutyType(UPDATED_DUTY_TYPE)
            .joiningDate(UPDATED_JOINING_DATE)
            .releaseDate(UPDATED_RELEASE_DATE)
            .message(UPDATED_MESSAGE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCenterEmployee.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCenterEmployee))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CenterEmployee in the database
        List<CenterEmployee> centerEmployeeList = centerEmployeeRepository.findAll().collectList().block();
        assertThat(centerEmployeeList).hasSize(databaseSizeBeforeUpdate);
        CenterEmployee testCenterEmployee = centerEmployeeList.get(centerEmployeeList.size() - 1);
        assertThat(testCenterEmployee.getDutyType()).isEqualTo(UPDATED_DUTY_TYPE);
        assertThat(testCenterEmployee.getJoiningDate()).isEqualTo(UPDATED_JOINING_DATE);
        assertThat(testCenterEmployee.getReleaseDate()).isEqualTo(UPDATED_RELEASE_DATE);
        assertThat(testCenterEmployee.getMessage()).isEqualTo(UPDATED_MESSAGE);
    }

    @Test
    void patchNonExistingCenterEmployee() throws Exception {
        int databaseSizeBeforeUpdate = centerEmployeeRepository.findAll().collectList().block().size();
        centerEmployee.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, centerEmployee.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(centerEmployee))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CenterEmployee in the database
        List<CenterEmployee> centerEmployeeList = centerEmployeeRepository.findAll().collectList().block();
        assertThat(centerEmployeeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCenterEmployee() throws Exception {
        int databaseSizeBeforeUpdate = centerEmployeeRepository.findAll().collectList().block().size();
        centerEmployee.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(centerEmployee))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CenterEmployee in the database
        List<CenterEmployee> centerEmployeeList = centerEmployeeRepository.findAll().collectList().block();
        assertThat(centerEmployeeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCenterEmployee() throws Exception {
        int databaseSizeBeforeUpdate = centerEmployeeRepository.findAll().collectList().block().size();
        centerEmployee.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(centerEmployee))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CenterEmployee in the database
        List<CenterEmployee> centerEmployeeList = centerEmployeeRepository.findAll().collectList().block();
        assertThat(centerEmployeeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCenterEmployee() {
        // Initialize the database
        centerEmployeeRepository.save(centerEmployee).block();

        int databaseSizeBeforeDelete = centerEmployeeRepository.findAll().collectList().block().size();

        // Delete the centerEmployee
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, centerEmployee.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<CenterEmployee> centerEmployeeList = centerEmployeeRepository.findAll().collectList().block();
        assertThat(centerEmployeeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
