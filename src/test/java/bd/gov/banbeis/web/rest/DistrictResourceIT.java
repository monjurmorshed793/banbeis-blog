package bd.gov.banbeis.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import bd.gov.banbeis.IntegrationTest;
import bd.gov.banbeis.domain.District;
import bd.gov.banbeis.repository.DistrictRepository;
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
 * Integration tests for the {@link DistrictResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class DistrictResourceIT {

    private static final String DEFAULT_DIVISION_ID = "AAAAAAAAAA";
    private static final String UPDATED_DIVISION_ID = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_BN_NAME = "AAAAAAAAAA";
    private static final String UPDATED_BN_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAT = "AAAAAAAAAA";
    private static final String UPDATED_LAT = "BBBBBBBBBB";

    private static final String DEFAULT_LON = "AAAAAAAAAA";
    private static final String UPDATED_LON = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/districts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private WebTestClient webTestClient;

    private District district;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static District createEntity() {
        District district = new District()
            .divisionId(DEFAULT_DIVISION_ID)
            .name(DEFAULT_NAME)
            .bnName(DEFAULT_BN_NAME)
            .lat(DEFAULT_LAT)
            .lon(DEFAULT_LON)
            .url(DEFAULT_URL);
        return district;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static District createUpdatedEntity() {
        District district = new District()
            .divisionId(UPDATED_DIVISION_ID)
            .name(UPDATED_NAME)
            .bnName(UPDATED_BN_NAME)
            .lat(UPDATED_LAT)
            .lon(UPDATED_LON)
            .url(UPDATED_URL);
        return district;
    }

    @BeforeEach
    public void initTest() {
        districtRepository.deleteAll().block();
        district = createEntity();
    }

    @Test
    void createDistrict() throws Exception {
        int databaseSizeBeforeCreate = districtRepository.findAll().collectList().block().size();
        // Create the District
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(district))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the District in the database
        List<District> districtList = districtRepository.findAll().collectList().block();
        assertThat(districtList).hasSize(databaseSizeBeforeCreate + 1);
        District testDistrict = districtList.get(districtList.size() - 1);
        assertThat(testDistrict.getDivisionId()).isEqualTo(DEFAULT_DIVISION_ID);
        assertThat(testDistrict.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDistrict.getBnName()).isEqualTo(DEFAULT_BN_NAME);
        assertThat(testDistrict.getLat()).isEqualTo(DEFAULT_LAT);
        assertThat(testDistrict.getLon()).isEqualTo(DEFAULT_LON);
        assertThat(testDistrict.getUrl()).isEqualTo(DEFAULT_URL);
    }

    @Test
    void createDistrictWithExistingId() throws Exception {
        // Create the District with an existing ID
        district.setId("existing_id");

        int databaseSizeBeforeCreate = districtRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(district))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the District in the database
        List<District> districtList = districtRepository.findAll().collectList().block();
        assertThat(districtList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllDistricts() {
        // Initialize the database
        district.setId(UUID.randomUUID().toString());
        districtRepository.save(district).block();

        // Get all the districtList
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
            .value(hasItem(district.getId()))
            .jsonPath("$.[*].divisionId")
            .value(hasItem(DEFAULT_DIVISION_ID))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].bnName")
            .value(hasItem(DEFAULT_BN_NAME))
            .jsonPath("$.[*].lat")
            .value(hasItem(DEFAULT_LAT))
            .jsonPath("$.[*].lon")
            .value(hasItem(DEFAULT_LON))
            .jsonPath("$.[*].url")
            .value(hasItem(DEFAULT_URL));
    }

    @Test
    void getDistrict() {
        // Initialize the database
        district.setId(UUID.randomUUID().toString());
        districtRepository.save(district).block();

        // Get the district
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, district.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(district.getId()))
            .jsonPath("$.divisionId")
            .value(is(DEFAULT_DIVISION_ID))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.bnName")
            .value(is(DEFAULT_BN_NAME))
            .jsonPath("$.lat")
            .value(is(DEFAULT_LAT))
            .jsonPath("$.lon")
            .value(is(DEFAULT_LON))
            .jsonPath("$.url")
            .value(is(DEFAULT_URL));
    }

    @Test
    void getNonExistingDistrict() {
        // Get the district
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewDistrict() throws Exception {
        // Initialize the database
        district.setId(UUID.randomUUID().toString());
        districtRepository.save(district).block();

        int databaseSizeBeforeUpdate = districtRepository.findAll().collectList().block().size();

        // Update the district
        District updatedDistrict = districtRepository.findById(district.getId()).block();
        updatedDistrict
            .divisionId(UPDATED_DIVISION_ID)
            .name(UPDATED_NAME)
            .bnName(UPDATED_BN_NAME)
            .lat(UPDATED_LAT)
            .lon(UPDATED_LON)
            .url(UPDATED_URL);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedDistrict.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedDistrict))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the District in the database
        List<District> districtList = districtRepository.findAll().collectList().block();
        assertThat(districtList).hasSize(databaseSizeBeforeUpdate);
        District testDistrict = districtList.get(districtList.size() - 1);
        assertThat(testDistrict.getDivisionId()).isEqualTo(UPDATED_DIVISION_ID);
        assertThat(testDistrict.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDistrict.getBnName()).isEqualTo(UPDATED_BN_NAME);
        assertThat(testDistrict.getLat()).isEqualTo(UPDATED_LAT);
        assertThat(testDistrict.getLon()).isEqualTo(UPDATED_LON);
        assertThat(testDistrict.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    void putNonExistingDistrict() throws Exception {
        int databaseSizeBeforeUpdate = districtRepository.findAll().collectList().block().size();
        district.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, district.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(district))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the District in the database
        List<District> districtList = districtRepository.findAll().collectList().block();
        assertThat(districtList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDistrict() throws Exception {
        int databaseSizeBeforeUpdate = districtRepository.findAll().collectList().block().size();
        district.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(district))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the District in the database
        List<District> districtList = districtRepository.findAll().collectList().block();
        assertThat(districtList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDistrict() throws Exception {
        int databaseSizeBeforeUpdate = districtRepository.findAll().collectList().block().size();
        district.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(district))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the District in the database
        List<District> districtList = districtRepository.findAll().collectList().block();
        assertThat(districtList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDistrictWithPatch() throws Exception {
        // Initialize the database
        district.setId(UUID.randomUUID().toString());
        districtRepository.save(district).block();

        int databaseSizeBeforeUpdate = districtRepository.findAll().collectList().block().size();

        // Update the district using partial update
        District partialUpdatedDistrict = new District();
        partialUpdatedDistrict.setId(district.getId());

        partialUpdatedDistrict.divisionId(UPDATED_DIVISION_ID).name(UPDATED_NAME).bnName(UPDATED_BN_NAME).url(UPDATED_URL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDistrict.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedDistrict))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the District in the database
        List<District> districtList = districtRepository.findAll().collectList().block();
        assertThat(districtList).hasSize(databaseSizeBeforeUpdate);
        District testDistrict = districtList.get(districtList.size() - 1);
        assertThat(testDistrict.getDivisionId()).isEqualTo(UPDATED_DIVISION_ID);
        assertThat(testDistrict.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDistrict.getBnName()).isEqualTo(UPDATED_BN_NAME);
        assertThat(testDistrict.getLat()).isEqualTo(DEFAULT_LAT);
        assertThat(testDistrict.getLon()).isEqualTo(DEFAULT_LON);
        assertThat(testDistrict.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    void fullUpdateDistrictWithPatch() throws Exception {
        // Initialize the database
        district.setId(UUID.randomUUID().toString());
        districtRepository.save(district).block();

        int databaseSizeBeforeUpdate = districtRepository.findAll().collectList().block().size();

        // Update the district using partial update
        District partialUpdatedDistrict = new District();
        partialUpdatedDistrict.setId(district.getId());

        partialUpdatedDistrict
            .divisionId(UPDATED_DIVISION_ID)
            .name(UPDATED_NAME)
            .bnName(UPDATED_BN_NAME)
            .lat(UPDATED_LAT)
            .lon(UPDATED_LON)
            .url(UPDATED_URL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDistrict.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedDistrict))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the District in the database
        List<District> districtList = districtRepository.findAll().collectList().block();
        assertThat(districtList).hasSize(databaseSizeBeforeUpdate);
        District testDistrict = districtList.get(districtList.size() - 1);
        assertThat(testDistrict.getDivisionId()).isEqualTo(UPDATED_DIVISION_ID);
        assertThat(testDistrict.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDistrict.getBnName()).isEqualTo(UPDATED_BN_NAME);
        assertThat(testDistrict.getLat()).isEqualTo(UPDATED_LAT);
        assertThat(testDistrict.getLon()).isEqualTo(UPDATED_LON);
        assertThat(testDistrict.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    void patchNonExistingDistrict() throws Exception {
        int databaseSizeBeforeUpdate = districtRepository.findAll().collectList().block().size();
        district.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, district.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(district))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the District in the database
        List<District> districtList = districtRepository.findAll().collectList().block();
        assertThat(districtList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDistrict() throws Exception {
        int databaseSizeBeforeUpdate = districtRepository.findAll().collectList().block().size();
        district.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(district))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the District in the database
        List<District> districtList = districtRepository.findAll().collectList().block();
        assertThat(districtList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDistrict() throws Exception {
        int databaseSizeBeforeUpdate = districtRepository.findAll().collectList().block().size();
        district.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(district))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the District in the database
        List<District> districtList = districtRepository.findAll().collectList().block();
        assertThat(districtList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDistrict() {
        // Initialize the database
        district.setId(UUID.randomUUID().toString());
        districtRepository.save(district).block();

        int databaseSizeBeforeDelete = districtRepository.findAll().collectList().block().size();

        // Delete the district
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, district.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<District> districtList = districtRepository.findAll().collectList().block();
        assertThat(districtList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
