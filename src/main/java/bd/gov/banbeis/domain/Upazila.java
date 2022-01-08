package bd.gov.banbeis.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Upazila.
 */
@Document(collection = "upazila")
public class Upazila implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("district_id")
    private String districtId;

    @Field("name")
    private String name;

    @Field("bn_name")
    private String bnName;

    @Field("url")
    private String url;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Upazila id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDistrictId() {
        return this.districtId;
    }

    public Upazila districtId(String districtId) {
        this.setDistrictId(districtId);
        return this;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getName() {
        return this.name;
    }

    public Upazila name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBnName() {
        return this.bnName;
    }

    public Upazila bnName(String bnName) {
        this.setBnName(bnName);
        return this;
    }

    public void setBnName(String bnName) {
        this.bnName = bnName;
    }

    public String getUrl() {
        return this.url;
    }

    public Upazila url(String url) {
        this.setUrl(url);
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Upazila)) {
            return false;
        }
        return id != null && id.equals(((Upazila) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Upazila{" +
            "id=" + getId() +
            ", districtId='" + getDistrictId() + "'" +
            ", name='" + getName() + "'" +
            ", bnName='" + getBnName() + "'" +
            ", url='" + getUrl() + "'" +
            "}";
    }
}
