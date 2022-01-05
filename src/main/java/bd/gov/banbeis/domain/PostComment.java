package bd.gov.banbeis.domain;

import bd.gov.banbeis.domain.enumeration.CommentType;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A PostComment.
 */
@Document(collection = "post_comment")
public class PostComment extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("commented_by")
    private String commentedBy;

    @Field("comment")
    private String comment;

    @Field("comment_type")
    private CommentType commentType;

    @Field("commented_on")
    private Instant commentedOn;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public PostComment id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommentedBy() {
        return this.commentedBy;
    }

    public PostComment commentedBy(String commentedBy) {
        this.setCommentedBy(commentedBy);
        return this;
    }

    public void setCommentedBy(String commentedBy) {
        this.commentedBy = commentedBy;
    }

    public String getComment() {
        return this.comment;
    }

    public PostComment comment(String comment) {
        this.setComment(comment);
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public CommentType getCommentType() {
        return this.commentType;
    }

    public PostComment commentType(CommentType commentType) {
        this.setCommentType(commentType);
        return this;
    }

    public void setCommentType(CommentType commentType) {
        this.commentType = commentType;
    }

    public Instant getCommentedOn() {
        return this.commentedOn;
    }

    public PostComment commentedOn(Instant commentedOn) {
        this.setCommentedOn(commentedOn);
        return this;
    }

    public void setCommentedOn(Instant commentedOn) {
        this.commentedOn = commentedOn;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PostComment)) {
            return false;
        }
        return id != null && id.equals(((PostComment) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PostComment{" +
            "id=" + getId() +
            ", commentedBy='" + getCommentedBy() + "'" +
            ", comment='" + getComment() + "'" +
            ", commentType='" + getCommentType() + "'" +
            ", commentedOn='" + getCommentedOn() + "'" +
            "}";
    }
}
