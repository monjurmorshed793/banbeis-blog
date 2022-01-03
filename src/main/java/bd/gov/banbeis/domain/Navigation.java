package bd.gov.banbeis.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Navigation.
 */
@Document(collection = "navigation")
public class Navigation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("sequence")
    private Integer sequence;

    @NotNull(message = "must not be null")
    @Field("route")
    private String route;

    @NotNull(message = "must not be null")
    @Field("title")
    private String title;

    @Field("bread_crumb")
    private String breadCrumb;

    @DBRef
    @Field("parent")
    @JsonIgnoreProperties(value = { "parent" }, allowSetters = true)
    private Navigation parent;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Navigation id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getSequence() {
        return this.sequence;
    }

    public Navigation sequence(Integer sequence) {
        this.setSequence(sequence);
        return this;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getRoute() {
        return this.route;
    }

    public Navigation route(String route) {
        this.setRoute(route);
        return this;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getTitle() {
        return this.title;
    }

    public Navigation title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBreadCrumb() {
        return this.breadCrumb;
    }

    public Navigation breadCrumb(String breadCrumb) {
        this.setBreadCrumb(breadCrumb);
        return this;
    }

    public void setBreadCrumb(String breadCrumb) {
        this.breadCrumb = breadCrumb;
    }

    public Navigation getParent() {
        return this.parent;
    }

    public void setParent(Navigation navigation) {
        this.parent = navigation;
    }

    public Navigation parent(Navigation navigation) {
        this.setParent(navigation);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Navigation)) {
            return false;
        }
        return id != null && id.equals(((Navigation) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Navigation{" +
            "id=" + getId() +
            ", sequence=" + getSequence() +
            ", route='" + getRoute() + "'" +
            ", title='" + getTitle() + "'" +
            ", breadCrumb='" + getBreadCrumb() + "'" +
            "}";
    }
}
