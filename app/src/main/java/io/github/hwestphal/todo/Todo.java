package io.github.hwestphal.todo;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Todo {

    private Long id;
    private Long version;

    private LocalDateTime created;

    @CreatedBy
    private String createUser;

    private LocalDateTime modified;

    @LastModifiedBy
    private String modifyUser;

    @NotNull
    @Size(min = 4)
    private String title;

    private boolean completed;

}
