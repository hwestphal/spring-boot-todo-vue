package io.github.hwestphal.todo;

import static io.github.hwestphal.todo.generated.tables.Todo.TODO;

import java.util.List;

import javax.validation.Valid;

import io.github.hwestphal.auditing.Create;
import io.github.hwestphal.auditing.Modify;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.ResultQuery;
import org.jooq.SelectForUpdateStep;
import org.jooq.impl.DSL;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

@Repository
@Validated
public class TodoRepository {

    private final DSLContext dsl;

    public TodoRepository(DSLContext dslContext) {
        this.dsl = dslContext;
    }

    public Todo findOne(Condition condition) {
        return oneOf(findAll(condition));
    }

    public Todo findOneForUpdate(Condition condition) {
        return oneOf(findAllForUpdate(condition));
    }

    private static Todo oneOf(List<Todo> todos) {
        if (todos.isEmpty()) {
            return null;
        }
        int size = todos.size();
        if (size > 1) {
            throw new IncorrectResultSizeDataAccessException(1, size);
        }
        return todos.get(0);
    }

    public List<Todo> findAll(Condition condition) {
        return findAll(condition, false);
    }

    private List<Todo> findAll(Condition condition, boolean update) {
        SelectForUpdateStep<?> queryStep = dsl
                .select(
                        TODO.ID,
                        TODO.VERSION,
                        TODO.CREATED,
                        TODO.CREATE_USER,
                        TODO.MODIFIED,
                        TODO.MODIFY_USER,
                        TODO.TITLE,
                        TODO.COMPLETED)
                .from(TODO)
                .where(condition)
                .orderBy(TODO.ID.asc());
        ResultQuery<?> query = update ? queryStep.forUpdate() : queryStep;
        return query.fetchInto(Todo.class);
    }

    public List<Todo> findAllForUpdate(Condition condition) {
        return findAll(condition, true);
    }

    @Create
    public long insert(@Valid Todo todo) {
        return dsl.insertInto(TODO)
                .set(TODO.CREATE_USER, todo.getCreateUser())
                .set(TODO.MODIFY_USER, todo.getModifyUser())
                .set(TODO.TITLE, todo.getTitle())
                .set(TODO.COMPLETED, todo.isCompleted())
                .returning(TODO.ID)
                .fetchOne()
                .getValue(TODO.ID);
    }

    @Modify
    public void update(@Valid Todo todo) {
        dsl.update(TODO)
                .set(TODO.VERSION, TODO.VERSION.add(1))
                .set(TODO.MODIFIED, DSL.currentLocalDateTime())
                .set(TODO.MODIFY_USER, todo.getModifyUser())
                .set(TODO.TITLE, todo.getTitle())
                .set(TODO.COMPLETED, todo.isCompleted())
                .where(TODO.ID.eq(todo.getId()))
                .execute();
    }

    public long deleteAll(Condition condition) {
        return dsl.delete(TODO).where(condition).execute();
    }

}
