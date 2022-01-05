package bd.gov.banbeis.domain;

import bd.gov.banbeis.domain.enumeration.DutyType;
import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A CenterEmployee.
 */
@Document(collection = "center_employee")
public class CenterEmployee implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("duty_type")
    private DutyType dutyType;

    @Field("joining_date")
    private LocalDate joiningDate;

    @Field("release_date")
    private LocalDate releaseDate;

    @Field("message")
    private String message;

    @DBRef
    @Field("designation")
    private Designation designation;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public CenterEmployee id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DutyType getDutyType() {
        return this.dutyType;
    }

    public CenterEmployee dutyType(DutyType dutyType) {
        this.setDutyType(dutyType);
        return this;
    }

    public void setDutyType(DutyType dutyType) {
        this.dutyType = dutyType;
    }

    public LocalDate getJoiningDate() {
        return this.joiningDate;
    }

    public CenterEmployee joiningDate(LocalDate joiningDate) {
        this.setJoiningDate(joiningDate);
        return this;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public LocalDate getReleaseDate() {
        return this.releaseDate;
    }

    public CenterEmployee releaseDate(LocalDate releaseDate) {
        this.setReleaseDate(releaseDate);
        return this;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getMessage() {
        return this.message;
    }

    public CenterEmployee message(String message) {
        this.setMessage(message);
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Designation getDesignation() {
        return this.designation;
    }

    public void setDesignation(Designation designation) {
        this.designation = designation;
    }

    public CenterEmployee designation(Designation designation) {
        this.setDesignation(designation);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CenterEmployee)) {
            return false;
        }
        return id != null && id.equals(((CenterEmployee) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CenterEmployee{" +
            "id=" + getId() +
            ", dutyType='" + getDutyType() + "'" +
            ", joiningDate='" + getJoiningDate() + "'" +
            ", releaseDate='" + getReleaseDate() + "'" +
            ", message='" + getMessage() + "'" +
            "}";
    }
}
