package com.empresa.crm_system.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public abstract class BaseService<T, ID> {

    protected abstract JpaRepository<T, ID> getRepository();

    @Transactional(readOnly = true)
    public List<T> listarTodos() {
        return getRepository().findAll();
    }

    @SuppressWarnings("null")
    @Transactional(readOnly = true)
    public Optional<T> buscarPorId(ID id) {
        return getRepository().findById(id);
    }

    @Transactional
    public T salvar(T entidade) {
        beforeSave(entidade);
        @SuppressWarnings("null")
        T resultado = getRepository().save(entidade);
        afterSave(resultado);
        return resultado;
    }

    @SuppressWarnings("null")
    @Transactional
    public void deletar(ID id) {
        beforeDelete(id);
        getRepository().deleteById(id);
        afterDelete(id);
    }

    @SuppressWarnings("null")
    @Transactional(readOnly = true)
    public boolean existe(ID id) {
        return getRepository().existsById(id);
    }

    @Transactional(readOnly = true)
    public long contarTotal() {
        return getRepository().count();
    }

    // Hooks para sobrescrever
    protected void beforeSave(T entidade) {}
    protected void afterSave(T entidade) {}
    protected void beforeDelete(ID id) {}
    protected void afterDelete(ID id) {}
}
