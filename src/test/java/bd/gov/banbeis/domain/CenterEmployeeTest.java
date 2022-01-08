package bd.gov.banbeis.domain;

import static org.assertj.core.api.Assertions.assertThat;

import bd.gov.banbeis.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CenterEmployeeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CenterEmployee.class);
        CenterEmployee centerEmployee1 = new CenterEmployee();
        centerEmployee1.setId("id1");
        CenterEmployee centerEmployee2 = new CenterEmployee();
        centerEmployee2.setId(centerEmployee1.getId());
        assertThat(centerEmployee1).isEqualTo(centerEmployee2);
        centerEmployee2.setId("id2");
        assertThat(centerEmployee1).isNotEqualTo(centerEmployee2);
        centerEmployee1.setId(null);
        assertThat(centerEmployee1).isNotEqualTo(centerEmployee2);
    }
}
