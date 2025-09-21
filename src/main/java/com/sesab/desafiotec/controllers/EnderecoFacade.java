package com.sesab.desafiotec.controllers;

import com.sesab.desafiotec.models.Endereco;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
public class EnderecoFacade extends AbstractFacade<Endereco> {

    @PersistenceContext(unitName = "com.SESAB_desafiotec_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EnderecoFacade() {
        super(Endereco.class);
    }

    public Endereco findByCep(String cep) {
        try {
            TypedQuery<Endereco> query = em.createQuery("SELECT e FROM Endereco e WHERE e.cep = :cep", Endereco.class);
            query.setParameter("cep", cep);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            System.err.println("Múltiplos resultados encontrados para o CEP: " + cep);
            return null;
        }
    }

    public Endereco findByLogradouro(String logradouro) {
        try {
            TypedQuery<Endereco> query = em.createQuery("SELECT e FROM Endereco e WHERE e.logradouro LIKE :logradouro", Endereco.class);
            query.setParameter("logradouro", logradouro);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            System.err.println("Múltiplos resultados encontrados para o logradouro: " + logradouro);
            return null;
        }
    }

    public Endereco findByCepAndLogradouro(String cep, String logradouro) {
        try {
            TypedQuery<Endereco> query = em.createQuery(
                    "SELECT e FROM Endereco e WHERE e.cep = :cep AND e.logradouro = :logradouro", Endereco.class);
            query.setParameter("cep", cep);
            query.setParameter("logradouro", logradouro);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            System.err.println("Múltiplos resultados encontrados para CEP: " + cep + " e logradouro: " + logradouro);
            return null;
        }
    }

    public void create(Endereco endereco) {
        em.persist(endereco);
    }
}
