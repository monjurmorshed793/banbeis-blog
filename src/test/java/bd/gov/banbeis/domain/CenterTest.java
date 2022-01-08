package bd.gov.banbeis.domain;

import static org.assertj.core.api.Assertions.assertThat;

import bd.gov.banbeis.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CenterTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Center.class);
        Center center1 = new Center();
        center1.setId("id1");
        Center center2 = new Center();
        center2.setId(center1.getId());
        assertThat(center1).isEqualTo(center2);
        center2.setId("id2");
        assertThat(center1).isNotEqualTo(center2);
        center1.setId(null);
        assertThat(center1).isNotEqualTo(center2);
    }
}
