/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sesab.desafiotec.controllers;

import com.sesab.desafiotec.controllers.exceptions.NonexistentEntityException;
import com.sesab.desafiotec.controllers.exceptions.RollbackFailureException;
import java.io.Serializable;
import com.sesab.desafiotec.models.Endereco;
import com.sesab.desafiotec.models.Pessoa;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;

/**
 *
 * @author joao
 */
public class PessoaJpaController implements Serializable {

    public PessoaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Pessoa pessoa) throws RollbackFailureException, Exception {
        if (pessoa.getEnderecos() == null) {
            pessoa.setEnderecos(new ArrayList<Endereco>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Endereco> attachedEnderecos = new ArrayList<Endereco>();
            for (Endereco enderecosEnderecoToAttach : pessoa.getEnderecos()) {
                enderecosEnderecoToAttach = em.getReference(enderecosEnderecoToAttach.getClass(), enderecosEnderecoToAttach.getId());
                attachedEnderecos.add(enderecosEnderecoToAttach);
            }
            pessoa.setEnderecos(attachedEnderecos);
            em.persist(pessoa);
            for (Endereco enderecosEndereco : pessoa.getEnderecos()) {
                enderecosEndereco.getUsuarios().add(pessoa);
                enderecosEndereco = em.merge(enderecosEndereco);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Pessoa pessoa) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Pessoa persistentPessoa = em.find(Pessoa.class, pessoa.getId());
            List<Endereco> enderecosOld = persistentPessoa.getEnderecos();
            List<Endereco> enderecosNew = pessoa.getEnderecos();
            List<Endereco> attachedEnderecosNew = new ArrayList<Endereco>();
            for (Endereco enderecosNewEnderecoToAttach : enderecosNew) {
                enderecosNewEnderecoToAttach = em.getReference(enderecosNewEnderecoToAttach.getClass(), enderecosNewEnderecoToAttach.getId());
                attachedEnderecosNew.add(enderecosNewEnderecoToAttach);
            }
            enderecosNew = attachedEnderecosNew;
            pessoa.setEnderecos(enderecosNew);
            pessoa = em.merge(pessoa);
            for (Endereco enderecosOldEndereco : enderecosOld) {
                if (!enderecosNew.contains(enderecosOldEndereco)) {
                    enderecosOldEndereco.getUsuarios().remove(pessoa);
                    enderecosOldEndereco = em.merge(enderecosOldEndereco);
                }
            }
            for (Endereco enderecosNewEndereco : enderecosNew) {
                if (!enderecosOld.contains(enderecosNewEndereco)) {
                    enderecosNewEndereco.getUsuarios().add(pessoa);
                    enderecosNewEndereco = em.merge(enderecosNewEndereco);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = pessoa.getId();
                if (findPessoa(id) == null) {
                    throw new NonexistentEntityException("The pessoa with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Pessoa pessoa;
            try {
                pessoa = em.getReference(Pessoa.class, id);
                pessoa.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pessoa with id " + id + " no longer exists.", enfe);
            }
            List<Endereco> enderecos = pessoa.getEnderecos();
            for (Endereco enderecosEndereco : enderecos) {
                enderecosEndereco.getUsuarios().remove(pessoa);
                enderecosEndereco = em.merge(enderecosEndereco);
            }
            em.remove(pessoa);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Pessoa> findPessoaEntities() {
        return findPessoaEntities(true, -1, -1);
    }

    public List<Pessoa> findPessoaEntities(int maxResults, int firstResult) {
        return findPessoaEntities(false, maxResults, firstResult);
    }

        //usando Criteria
    private List<Pessoa> findPessoaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pessoa.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Pessoa findPessoa(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pessoa.class, id);
        } finally {
            em.close();
        }
    }

    public int getPessoaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pessoa> rt = cq.from(Pessoa.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    
}
