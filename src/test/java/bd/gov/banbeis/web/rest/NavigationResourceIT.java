package bd.gov.banbeis.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import bd.gov.banbeis.IntegrationTest;
import bd.gov.banbeis.domain.Navigation;
import bd.gov.banbeis.repository.NavigationRepository;
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
 * Integration tests for the {@link NavigationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class NavigationResourceIT {

    private static final Integer DEFAULT_SEQUENCE = 1;
    private static final Integer UPDATED_SEQUENCE = 2;

    private static final String DEFAULT_ROUTE = "AAAAAAAAAA";
    private static final String UPDATED_ROUTE = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_BREAD_CRUMB = "AAAAAAAAAA";
    private static final String UPDATED_BREAD_CRUMB = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/navigations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private NavigationRepository navigationRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Navigation navigation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Navigation createEntity() {
        Navigation navigation = new Navigation()
            .sequence(DEFAULT_SEQUENCE)
            .route(DEFAULT_ROUTE)
            .title(DEFAULT_TITLE)
            .breadCrumb(DEFAULT_BREAD_CRUMB);
        return navigation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Navigation createUpdatedEntity() {
        Navigation navigation = new Navigation()
            .sequence(UPDATED_SEQUENCE)
            .route(UPDATED_ROUTE)
            .title(UPDATED_TITLE)
            .breadCrumb(UPDATED_BREAD_CRUMB);
        return navigation;
    }

    @BeforeEach
    public void initTest() {
        navigationRepository.deleteAll().block();
        navigation = createEntity();
    }

    @Test
    void createNavigation() throws Exception {
        int databaseSizeBeforeCreate = navigationRepository.findAll().collectList().block().size();
        // Create the Navigation
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(navigation))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Navigation in the database
        List<Navigation> navigationList = navigationRepository.findAll().collectList().block();
        assertThat(navigationList).hasSize(databaseSizeBeforeCreate + 1);
        Navigation testNavigation = navigationList.get(navigationList.size() - 1);
        assertThat(testNavigation.getSequence()).isEqualTo(DEFAULT_SEQUENCE);
        assertThat(testNavigation.getRoute()).isEqualTo(DEFAULT_ROUTE);
        assertThat(testNavigation.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testNavigation.getBreadCrumb()).isEqualTo(DEFAULT_BREAD_CRUMB);
    }

    @Test
    void createNavigationWithExistingId() throws Exception {
        // Create the Navigation with an existing ID
        navigation.setId("existing_id");

        int databaseSizeBeforeCreate = navigationRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(navigation))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Navigation in the database
        List<Navigation> navigationList = navigationRepository.findAll().collectList().block();
        assertThat(navigationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkRouteIsRequired() throws Exception {
        int databaseSizeBeforeTest = navigationRepository.findAll().collectList().block().size();
        // set the field null
        navigation.setRoute(null);

        // Create the Navigation, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(navigation))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Navigation> navigationList = navigationRepository.findAll().collectList().block();
        assertThat(navigationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = navigationRepository.findAll().collectList().block().size();
        // set the field null
        navigation.setTitle(null);

        // Create the Navigation, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(navigation))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Navigation> navigationList = navigationRepository.findAll().collectList().block();
        assertThat(navigationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllNavigations() {
        // Initialize the database
        navigationRepository.save(navigation).block();

        // Get all the navigationList
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
            .value(hasItem(navigation.getId()))
            .jsonPath("$.[*].sequence")
            .value(hasItem(DEFAULT_SEQUENCE))
            .jsonPath("$.[*].route")
            .value(hasItem(DEFAULT_ROUTE))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].breadCrumb")
            .value(hasItem(DEFAULT_BREAD_CRUMB));
    }

    @Test
    void getNavigation() {
        // Initialize the database
        navigationRepository.save(navigation).block();

        // Get the navigation
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, navigation.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(navigation.getId()))
            .jsonPath("$.sequence")
            .value(is(DEFAULT_SEQUENCE))
            .jsonPath("$.route")
            .value(is(DEFAULT_ROUTE))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.breadCrumb")
            .value(is(DEFAULT_BREAD_CRUMB));
    }

    @Test
    void getNonExistingNavigation() {
        // Get the navigation
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewNavigation() throws Exception {
        // Initialize the database
        navigationRepository.save(navigation).block();

        int databaseSizeBeforeUpdate = navigationRepository.findAll().collectList().block().size();

        // Update the navigation
        Navigation updatedNavigation = navigationRepository.findById(navigation.getId()).block();
        updatedNavigation.sequence(UPDATED_SEQUENCE).route(UPDATED_ROUTE).title(UPDATED_TITLE).breadCrumb(UPDATED_BREAD_CRUMB);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedNavigation.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedNavigation))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Navigation in the database
        List<Navigation> navigationList = navigationRepository.findAll().collectList().block();
        assertThat(navigationList).hasSize(databaseSizeBeforeUpdate);
        Navigation testNavigation = navigationList.get(navigationList.size() - 1);
        assertThat(testNavigation.getSequence()).isEqualTo(UPDATED_SEQUENCE);
        assertThat(testNavigation.getRoute()).isEqualTo(UPDATED_ROUTE);
        assertThat(testNavigation.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testNavigation.getBreadCrumb()).isEqualTo(UPDATED_BREAD_CRUMB);
    }

    @Test
    void putNonExistingNavigation() throws Exception {
        int databaseSizeBeforeUpdate = navigationRepository.findAll().collectList().block().size();
        navigation.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, navigation.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(navigation))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Navigation in the database
        List<Navigation> navigationList = navigationRepository.findAll().collectList().block();
        assertThat(navigationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchNavigation() throws Exception {
        int databaseSizeBeforeUpdate = navigationRepository.findAll().collectList().block().size();
        navigation.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(navigation))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Navigation in the database
        List<Navigation> navigationList = navigationRepository.findAll().collectList().block();
        assertThat(navigationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamNavigation() throws Exception {
        int databaseSizeBeforeUpdate = navigationRepository.findAll().collectList().block().size();
        navigation.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(navigation))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Navigation in the database
        List<Navigation> navigationList = navigationRepository.findAll().collectList().block();
        assertThat(navigationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateNavigationWithPatch() throws Exception {
        // Initialize the database
        navigationRepository.save(navigation).block();

        int databaseSizeBeforeUpdate = navigationRepository.findAll().collectList().block().size();

        // Update the navigation using partial update
        Navigation partialUpdatedNavigation = new Navigation();
        partialUpdatedNavigation.setId(navigation.getId());

        partialUpdatedNavigation.sequence(UPDATED_SEQUENCE).breadCrumb(UPDATED_BREAD_CRUMB);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedNavigation.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedNavigation))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Navigation in the database
        List<Navigation> navigationList = navigationRepository.findAll().collectList().block();
        assertThat(navigationList).hasSize(databaseSizeBeforeUpdate);
        Navigation testNavigation = navigationList.get(navigationList.size() - 1);
        assertThat(testNavigation.getSequence()).isEqualTo(UPDATED_SEQUENCE);
        assertThat(testNavigation.getRoute()).isEqualTo(DEFAULT_ROUTE);
        assertThat(testNavigation.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testNavigation.getBreadCrumb()).isEqualTo(UPDATED_BREAD_CRUMB);
    }

    @Test
    void fullUpdateNavigationWithPatch() throws Exception {
        // Initialize the database
        navigationRepository.save(navigation).block();

        int databaseSizeBeforeUpdate = navigationRepository.findAll().collectList().block().size();

        // Update the navigation using partial update
        Navigation partialUpdatedNavigation = new Navigation();
        partialUpdatedNavigation.setId(navigation.getId());

        partialUpdatedNavigation.sequence(UPDATED_SEQUENCE).route(UPDATED_ROUTE).title(UPDATED_TITLE).breadCrumb(UPDATED_BREAD_CRUMB);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedNavigation.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedNavigation))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Navigation in the database
        List<Navigation> navigationList = navigationRepository.findAll().collectList().block();
        assertThat(navigationList).hasSize(databaseSizeBeforeUpdate);
        Navigation testNavigation = navigationList.get(navigationList.size() - 1);
        assertThat(testNavigation.getSequence()).isEqualTo(UPDATED_SEQUENCE);
        assertThat(testNavigation.getRoute()).isEqualTo(UPDATED_ROUTE);
        assertThat(testNavigation.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testNavigation.getBreadCrumb()).isEqualTo(UPDATED_BREAD_CRUMB);
    }

    @Test
    void patchNonExistingNavigation() throws Exception {
        int databaseSizeBeforeUpdate = navigationRepository.findAll().collectList().block().size();
        navigation.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, navigation.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(navigation))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Navigation in the database
        List<Navigation> navigationList = navigationRepository.findAll().collectList().block();
        assertThat(navigationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchNavigation() throws Exception {
        int databaseSizeBeforeUpdate = navigationRepository.findAll().collectList().block().size();
        navigation.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(navigation))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Navigation in the database
        List<Navigation> navigationList = navigationRepository.findAll().collectList().block();
        assertThat(navigationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamNavigation() throws Exception {
        int databaseSizeBeforeUpdate = navigationRepository.findAll().collectList().block().size();
        navigation.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(navigation))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Navigation in the database
        List<Navigation> navigationList = navigationRepository.findAll().collectList().block();
        assertThat(navigationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteNavigation() {
        // Initialize the database
        navigationRepository.save(navigation).block();

        int databaseSizeBeforeDelete = navigationRepository.findAll().collectList().block().size();

        // Delete the navigation
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, navigation.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Navigation> navigationList = navigationRepository.findAll().collectList().block();
        assertThat(navigationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
