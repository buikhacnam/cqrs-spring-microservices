package com.techbank.cqrs.cors.queries;


import com.techbank.cqrs.cors.domain.BaseEntity;

import java.util.List;

@FunctionalInterface
public interface QueryHandlerMethod<T extends BaseQuery> {
    List<BaseEntity> handle(T query);
}

