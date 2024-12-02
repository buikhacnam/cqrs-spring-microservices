package com.techbank.cqrs.cors.infrastructure;



import com.techbank.cqrs.cors.domain.BaseEntity;
import com.techbank.cqrs.cors.queries.BaseQuery;
import com.techbank.cqrs.cors.queries.QueryHandlerMethod;

import java.util.List;

public interface QueryDispatcher {
    <T extends BaseQuery> void registerHandler(Class<T> type, QueryHandlerMethod<T> handler);
    <U extends BaseEntity> List<U> send(BaseQuery query);
}
