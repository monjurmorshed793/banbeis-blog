package bd.gov.banbeis.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A PostPhoto.
 */
@Document(collection = "post_photo")
public class PostPhoto extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("sequence")
    private Integer sequence;

    @Field("title")
    private String title;

    @Field("description")
    private String description;

    @Field("image")
    private byte[] image;

    @Field("image_content_type")
    private String imageContentType;

    @Field("uploaded_on")
    private Instant uploadedOn;

    @DBRef
    @Field("post")
    @JsonIgnoreProperties(value = { "center", "employee" }, allowSetters = true)
    private Post post;

    @DBRef
    @Field("uploadedBy")
    @JsonIgnoreProperties(value = { "designation" }, allowSetters = true)
    private Employee uploadedBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public PostPhoto id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getSequence() {
        return this.sequence;
    }

    public PostPhoto sequence(Integer sequence) {
        this.setSequence(sequence);
        return this;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getTitle() {
        return this.title;
    }

    public PostPhoto title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public PostPhoto description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImage() {
        return this.image;
    }

    public PostPhoto image(byte[] image) {
        this.setImage(image);
        return this;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return this.imageContentType;
    }

    public PostPhoto imageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
        return this;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public Instant getUploadedOn() {
        return this.uploadedOn;
    }

    public PostPhoto uploadedOn(Instant uploadedOn) {
        this.setUploadedOn(uploadedOn);
        return this;
    }

    public void setUploadedOn(Instant uploadedOn) {
        this.uploadedOn = uploadedOn;
    }

    public Post getPost() {
        return this.post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public PostPhoto post(Post post) {
        this.setPost(post);
        return this;
    }

    public Employee getUploadedBy() {
        return this.uploadedBy;
    }

    public void setUploadedBy(Employee employee) {
        this.uploadedBy = employee;
    }

    public PostPhoto uploadedBy(Employee employee) {
        this.setUploadedBy(employee);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PostPhoto)) {
            return false;
        }
        return id != null && id.equals(((PostPhoto) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PostPhoto{" +
            "id=" + getId() +
            ", sequence=" + getSequence() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", image='" + getImage() + "'" +
            ", imageContentType='" + getImageContentType() + "'" +
            ", uploadedOn='" + getUploadedOn() + "'" +
            "}";
    }
}
