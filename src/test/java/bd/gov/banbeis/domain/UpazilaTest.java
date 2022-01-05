package bd.gov.banbeis.domain;

import static org.assertj.core.api.Assertions.assertThat;

import bd.gov.banbeis.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UpazilaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Upazila.class);
        Upazila upazila1 = new Upazila();
        upazila1.setId("id1");
        Upazila upazila2 = new Upazila();
        upazila2.setId(upazila1.getId());
        assertThat(upazila1).isEqualTo(upazila2);
        upazila2.setId("id2");
        assertThat(upazila1).isNotEqualTo(upazila2);
        upazila1.setId(null);
        assertThat(upazila1).isNotEqualTo(upazila2);
    }
}
