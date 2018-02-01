package io.github.hwestphal.todo;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

@Getter
@Setter
@ToString
public class Todo {

    private Long id;
    private Long version;

    @Getter(onMethod = @__(@JsonIgnore))
    @Setter
    private LocalDateTime created;

    @Getter(onMethod = @__(@JsonIgnore))
    @Setter
    @CreatedBy
    private String createUser;

    @Getter(onMethod = @__(@JsonIgnore))
    @Setter
    private LocalDateTime modified;

    @Getter(onMethod = @__(@JsonIgnore))
    @Setter
    @LastModifiedBy
    private String modifyUser;

    @NotNull
    @Size(min = 4)
    private String title;

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
