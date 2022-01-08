package bd.gov.banbeis.domain;

import static org.assertj.core.api.Assertions.assertThat;

import bd.gov.banbeis.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DesignationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Designation.class);
        Designation designation1 = new Designation();
        designation1.setId("id1");
        Designation designation2 = new Designation();
        designation2.setId(designation1.getId());
        assertThat(designation1).isEqualTo(designation2);
        designation2.setId("id2");
        assertThat(designation1).isNotEqualTo(designation2);
        designation1.setId(null);
        assertThat(designation1).isNotEqualTo(designation2);
    }
}
