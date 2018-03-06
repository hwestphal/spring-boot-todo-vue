package io.github.hwestphal.todo;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import io.github.hwestphal.auditing.Create;
import io.github.hwestphal.auditing.Modify;
import io.github.hwestphal.todo.generated.QTodo;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

@Repository
@Validated
public class TodoRepository {

    private final SQLQueryFactory queryFactory;

    public TodoRepository(SQLQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public Todo findOne(Predicate predicate) {
        return oneOf(findAll(predicate));
    }

    public Todo findOneForUpdate(Predicate predicate) {
        return oneOf(findAllForUpdate(predicate));
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

    public List<Todo> findAll(Predicate predicate) {
        return findAll(predicate, false);
    }

    private List<Todo> findAll(Predicate predicate, boolean update) {
        QTodo q = QTodo.todo;
        SQLQuery<Todo> query = queryFactory.select(
                Projections.bean(
                        Todo.class,
                        q.id,
                        q.version,
                        q.created,
                        q.createUser,
                        q.modified,
                        q.modifyUser,
                        q.title,
                        q.completed))
                .from(q)
                .where(predicate)
                .orderBy(q.id.asc());
        if (update) {
            query = query.forUpdate();
        }
        return query.fetch();
    }

    public List<Todo> findAllForUpdate(Predicate predicate) {
        return findAll(predicate, true);
    }

    @Create
    public long insert(@Valid Todo todo) {
        QTodo q = QTodo.todo;
        return queryFactory.insert(q)
                .set(q.createUser, todo.getCreateUser())
                .set(q.modifyUser, todo.getModifyUser())
                .set(q.title, todo.getTitle())
                .set(q.completed, todo.isCompleted())
                .executeWithKey(q.id);
    }

    @Modify
    public void update(@Valid Todo todo) {
        QTodo q = QTodo.todo;
        queryFactory.update(q)
                .set(q.version, q.version.add(Expressions.ONE))
                .set(q.modified, DateTimeExpression.currentTimestamp(LocalDateTime.class))
                .set(q.modifyUser, todo.getModifyUser())
                .set(q.title, todo.getTitle())
                .set(q.completed, todo.isCompleted())
                .where(q.id.eq(todo.getId()))
                .execute();
    }

    public long deleteAll(Predicate predicate) {
        return queryFactory.delete(QTodo.todo).where(predicate).execute();
    }

}
