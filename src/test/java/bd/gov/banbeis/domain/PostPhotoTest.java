package bd.gov.banbeis.domain;

import static org.assertj.core.api.Assertions.assertThat;

import bd.gov.banbeis.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PostPhotoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PostPhoto.class);
        PostPhoto postPhoto1 = new PostPhoto();
        postPhoto1.setId("id1");
        PostPhoto postPhoto2 = new PostPhoto();
        postPhoto2.setId(postPhoto1.getId());
        assertThat(postPhoto1).isEqualTo(postPhoto2);
        postPhoto2.setId("id2");
        assertThat(postPhoto1).isNotEqualTo(postPhoto2);
        postPhoto1.setId(null);
        assertThat(postPhoto1).isNotEqualTo(postPhoto2);
    }
}
