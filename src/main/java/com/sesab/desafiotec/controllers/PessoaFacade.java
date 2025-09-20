/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sesab.desafiotec.controllers;

import com.sesab.desafiotec.models.Pessoa;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author joao
 */
@Stateless
public class PessoaFacade extends AbstractFacade<Pessoa> {

    @PersistenceContext(unitName = "com.SESAB_desafiotec_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PessoaFacade() {
        super(Pessoa.class);
    }
    
    public List<Pessoa> findWithFilters(String nome, String cpf, Date dataInicio, Date dataFim) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Pessoa> cq = cb.createQuery(Pessoa.class);
        Root<Pessoa> pessoa = cq.from(Pessoa.class);
        
        List<Predicate> predicates = new ArrayList<>();

        if (nome != null && !nome.trim().isEmpty()) {
            predicates.add(cb.like(pessoa.get("nome"), "%" + nome + "%"));
        }

        if (cpf != null && !cpf.trim().isEmpty()) {
            predicates.add(cb.equal(pessoa.get("cpf"), cpf));
        }

        if (dataInicio != null) {
            predicates.add(cb.greaterThanOrEqualTo(pessoa.get("dataCriacao"), dataInicio));
        }

        if (dataFim != null) {
            predicates.add(cb.lessThanOrEqualTo(pessoa.get("dataCriacao"), dataFim));
        }

        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }

        return em.createQuery(cq).getResultList();
    }
    
}
