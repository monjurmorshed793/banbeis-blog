package bd.gov.banbeis.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A District.
 */
@Document(collection = "district")
public class District implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("division_id")
    private String divisionId;

    @Field("name")
    private String name;

    @Field("bn_name")
    private String bnName;

    @Field("lat")
    private String lat;

    @Field("lon")
    private String lon;

    @Field("url")
    private String url;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public District id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDivisionId() {
        return this.divisionId;
    }

    public District divisionId(String divisionId) {
        this.setDivisionId(divisionId);
        return this;
    }

    public void setDivisionId(String divisionId) {
        this.divisionId = divisionId;
    }

    public String getName() {
        return this.name;
    }

    public District name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBnName() {
        return this.bnName;
    }

    public District bnName(String bnName) {
        this.setBnName(bnName);
        return this;
    }

    public void setBnName(String bnName) {
        this.bnName = bnName;
    }

    public String getLat() {
        return this.lat;
    }

    public District lat(String lat) {
        this.setLat(lat);
        return this;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return this.lon;
    }

    public District lon(String lon) {
        this.setLon(lon);
        return this;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getUrl() {
        return this.url;
    }

    public District url(String url) {
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
        if (!(o instanceof District)) {
            return false;
        }
        return id != null && id.equals(((District) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "District{" +
            "id=" + getId() +
            ", divisionId='" + getDivisionId() + "'" +
            ", name='" + getName() + "'" +
            ", bnName='" + getBnName() + "'" +
            ", lat='" + getLat() + "'" +
            ", lon='" + getLon() + "'" +
            ", url='" + getUrl() + "'" +
            "}";
    }
}
