package com.ncuculova.oauth2.service.impl;

import com.ncuculova.oauth2.model.BaseEntity;
import com.ncuculova.oauth2.service.BaseEntityCrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public abstract class BaseEntityCrudServiceImpl<T extends BaseEntity, R extends JpaRepository<T, Long>>
        implements BaseEntityCrudService<T> {

    protected abstract R getRepository();

    @Override
    public T save(T entity) {
        return getRepository().save(entity);
    }

    @Override
    public List<T> save(Iterable<T> entities) {
        return getRepository().save(entities);
    }

    @Override
    public T saveAndFlush(T entity) {
        return getRepository().saveAndFlush(entity);
    }

    @Override
    public List<T> findAll() {
        return getRepository().findAll();
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return getRepository().findAll(pageable);
    }

    @Override
    public List<T> findAll(Sort sort) {
        return getRepository().findAll(sort);
    }

    @Override
    public List<T> findAll(Iterable<Long> ids) {
        return getRepository().findAll(ids);
    }

    @Override
    public long count() {
        return getRepository().count();
    }


    @Override
    public T findOne(Long id) {
        return getRepository().findOne(id);
    }

    @Override
    public boolean exists(Long id) {
        return getRepository().exists(id);
    }

    @Override
    public void delete(Long id) {
        getRepository().delete(id);
    }

    @Override
    public void delete(T entity) {
        getRepository().delete(entity);
    }

    @Override
    public void delete(Iterable<T> entities) {
        getRepository().delete(entities);
    }

    @Override
    public void deleteAll() {
        getRepository().deleteAll();
    }

}