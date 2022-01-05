package bd.gov.banbeis.domain;

import static org.assertj.core.api.Assertions.assertThat;

import bd.gov.banbeis.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CenterImagesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CenterImages.class);
        CenterImages centerImages1 = new CenterImages();
        centerImages1.setId("id1");
        CenterImages centerImages2 = new CenterImages();
        centerImages2.setId(centerImages1.getId());
        assertThat(centerImages1).isEqualTo(centerImages2);
        centerImages2.setId("id2");
        assertThat(centerImages1).isNotEqualTo(centerImages2);
        centerImages1.setId(null);
        assertThat(centerImages1).isNotEqualTo(centerImages2);
    }
}
