package io.github.hwestphal.todo;

import java.util.List;

import javax.validation.Valid;

import io.github.hwestphal.auditing.Create;
import io.github.hwestphal.auditing.Modify;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.validation.annotation.Validated;

@Mapper
@Validated
public interface TodoRepository {

    Todo findById(long id);

    Todo findByIdAndVersionForUpdate(@Param("id") long id, @Param("version") long version);

    List<Todo> findAll();

    List<Todo> findAllForUpdate();

    int deleteById(long id);

    void deleteAll();

    @Create
    void insert(@Valid Todo todo);

    @Modify
    void update(@Valid Todo todo);

}
