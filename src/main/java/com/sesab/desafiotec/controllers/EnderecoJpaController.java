/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sesab.desafiotec.controllers;

import com.sesab.desafiotec.controllers.exceptions.NonexistentEntityException;
import com.sesab.desafiotec.controllers.exceptions.RollbackFailureException;
import com.sesab.desafiotec.models.Endereco;
import java.io.Serializable;
import com.sesab.desafiotec.models.Pessoa;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;

/**
 *
 * @author joao
 */
public class EnderecoJpaController implements Serializable {

    public EnderecoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Endereco endereco) throws RollbackFailureException, Exception {
        if (endereco.getUsuarios() == null) {
            endereco.setUsuarios(new ArrayList<Pessoa>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Pessoa> attachedUsuarios = new ArrayList<Pessoa>();
            for (Pessoa usuariosPessoaToAttach : endereco.getUsuarios()) {
                usuariosPessoaToAttach = em.getReference(usuariosPessoaToAttach.getClass(), usuariosPessoaToAttach.getId());
                attachedUsuarios.add(usuariosPessoaToAttach);
            }
            endereco.setUsuarios(attachedUsuarios);
            em.persist(endereco);
            for (Pessoa usuariosPessoa : endereco.getUsuarios()) {
                usuariosPessoa.getEnderecos().add(endereco);
                usuariosPessoa = em.merge(usuariosPessoa);
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

    public void edit(Endereco endereco) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Endereco persistentEndereco = em.find(Endereco.class, endereco.getId());
            List<Pessoa> usuariosOld = persistentEndereco.getUsuarios();
            List<Pessoa> usuariosNew = endereco.getUsuarios();
            List<Pessoa> attachedUsuariosNew = new ArrayList<Pessoa>();
            for (Pessoa usuariosNewPessoaToAttach : usuariosNew) {
                usuariosNewPessoaToAttach = em.getReference(usuariosNewPessoaToAttach.getClass(), usuariosNewPessoaToAttach.getId());
                attachedUsuariosNew.add(usuariosNewPessoaToAttach);
            }
            usuariosNew = attachedUsuariosNew;
            endereco.setUsuarios(usuariosNew);
            endereco = em.merge(endereco);
            for (Pessoa usuariosOldPessoa : usuariosOld) {
                if (!usuariosNew.contains(usuariosOldPessoa)) {
                    usuariosOldPessoa.getEnderecos().remove(endereco);
                    usuariosOldPessoa = em.merge(usuariosOldPessoa);
                }
            }
            for (Pessoa usuariosNewPessoa : usuariosNew) {
                if (!usuariosOld.contains(usuariosNewPessoa)) {
                    usuariosNewPessoa.getEnderecos().add(endereco);
                    usuariosNewPessoa = em.merge(usuariosNewPessoa);
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
                Long id = endereco.getId();
                if (findEndereco(id) == null) {
                    throw new NonexistentEntityException("The endereco with id " + id + " no longer exists.");
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
            Endereco endereco;
            try {
                endereco = em.getReference(Endereco.class, id);
                endereco.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The endereco with id " + id + " no longer exists.", enfe);
            }
            List<Pessoa> usuarios = endereco.getUsuarios();
            for (Pessoa usuariosPessoa : usuarios) {
                usuariosPessoa.getEnderecos().remove(endereco);
                usuariosPessoa = em.merge(usuariosPessoa);
            }
            em.remove(endereco);
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

    public List<Endereco> findEnderecoEntities() {
        return findEnderecoEntities(true, -1, -1);
    }

    public List<Endereco> findEnderecoEntities(int maxResults, int firstResult) {
        return findEnderecoEntities(false, maxResults, firstResult);
    }

    //usando criteria
    private List<Endereco> findEnderecoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Endereco.class));
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

    public Endereco findEndereco(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Endereco.class, id);
        } finally {
            em.close();
        }
    }

    public int getEnderecoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Endereco> rt = cq.from(Endereco.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
