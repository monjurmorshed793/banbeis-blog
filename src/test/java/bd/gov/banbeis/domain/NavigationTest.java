package bd.gov.banbeis.domain;

import static org.assertj.core.api.Assertions.assertThat;

import bd.gov.banbeis.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NavigationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Navigation.class);
        Navigation navigation1 = new Navigation();
        navigation1.setId("id1");
        Navigation navigation2 = new Navigation();
        navigation2.setId(navigation1.getId());
        assertThat(navigation1).isEqualTo(navigation2);
        navigation2.setId("id2");
        assertThat(navigation1).isNotEqualTo(navigation2);
        navigation1.setId(null);
        assertThat(navigation1).isNotEqualTo(navigation2);
    }
}
