package io.github.hwestphal.todo;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@QueryEntity
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

}
