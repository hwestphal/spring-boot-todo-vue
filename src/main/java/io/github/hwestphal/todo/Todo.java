package io.github.hwestphal.todo;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@ToString
public class Todo {

    private static final String SEQUENCE = "TODO_ID_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE)
    @SequenceGenerator(name = SEQUENCE, sequenceName = SEQUENCE, allocationSize = 1)
    @Getter
    @Setter
    private Long id;

    @Version
    @Getter
    @Setter
    private Long version;

    @CreatedDate
    private LocalDateTime created;

    @CreatedBy
    private String createUser;

    @LastModifiedDate
    private LocalDateTime modified;

    @LastModifiedBy
    private String modifyUser;

    @NotNull
    @Size(min = 4)
    @Getter
    @Setter
    private String title;

    @Type(type = "yes_no")
    @Getter
    @Setter
    private boolean completed;

    @Override
    public int hashCode() {
        return id == null ? super.hashCode() : 31 + id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Todo)) {
            return false;
        }
        if (id == null) {
            return false;
        }
        Todo other = (Todo) obj;
        return id.equals(other.id);
    }

}
