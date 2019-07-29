package jworkspace.weather;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

/**
 * @author <a href="mailto:SDebalchuk@wiley.com">Stanislav Debalchuk</a>
 * @since 2013-03-01
 *
 * @param <PKType> primary key type
 */
@MappedSuperclass
public abstract class BaseEntity<PKType extends Serializable> implements Serializable {

    private static final StandardToStringStyle TO_STRING_STYLE = new StandardToStringStyle();

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false, updatable = false)
    @Getter
    @Setter
    private PKType id;

    @Override
    // CHECKSTYLE:OFF NeedBraces
    public boolean equals(Object o) {
        if (o == null) return false;
        if (this == o) return true;
        if (o.getClass() != getClass()
                && !o.getClass().isAssignableFrom(getClass())
                && !getClass().isAssignableFrom(o.getClass()))
            return false;
        final BaseEntity entity = (BaseEntity) o;

        EqualsBuilder equalsBuilder = new EqualsBuilder().append(getId(), entity.getId());
        appendToEqualsBuilder(equalsBuilder, entity);
        return equalsBuilder.isEquals();
    }
    // CHECKSTYLE:ON NeedBraces

    protected void appendToEqualsBuilder(EqualsBuilder equalsBuilder, BaseEntity o) {
    }

    @Override
    public int hashCode() {
        return (getId() != null) ? getId().hashCode() : System.identityHashCode(this);
    }

    /**
     * Returns a string representation of the object to simplify log analysis.
     *
     * @return  a string representation of the object.
     */
    @Override
    public String toString() {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this, TO_STRING_STYLE);
        toStringBuilder.append("id", getId());
        appendToString(toStringBuilder);
        return toStringBuilder.toString();
    }

    protected void appendToString(ToStringBuilder toStringBuilder) {
    }
}
