package bd.gov.banbeis.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Center.
 */
@Document(collection = "center")
public class Center implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("address_line")
    private String addressLine;

    @Field("image")
    private byte[] image;

    @Field("image_content_type")
    private String imageContentType;

    @DBRef
    @Field("division")
    private Division division;

    @DBRef
    @Field("district")
    private District district;

    @DBRef
    @Field("upazila")
    private Upazila upazila;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Center id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Center name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddressLine() {
        return this.addressLine;
    }

    public Center addressLine(String addressLine) {
        this.setAddressLine(addressLine);
        return this;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public byte[] getImage() {
        return this.image;
    }

    public Center image(byte[] image) {
        this.setImage(image);
        return this;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return this.imageContentType;
    }

    public Center imageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
        return this;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public Division getDivision() {
        return this.division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public Center division(Division division) {
        this.setDivision(division);
        return this;
    }

    public District getDistrict() {
        return this.district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public Center district(District district) {
        this.setDistrict(district);
        return this;
    }

    public Upazila getUpazila() {
        return this.upazila;
    }

    public void setUpazila(Upazila upazila) {
        this.upazila = upazila;
    }

    public Center upazila(Upazila upazila) {
        this.setUpazila(upazila);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Center)) {
            return false;
        }
        return id != null && id.equals(((Center) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Center{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", addressLine='" + getAddressLine() + "'" +
            ", image='" + getImage() + "'" +
            ", imageContentType='" + getImageContentType() + "'" +
            "}";
    }
}
