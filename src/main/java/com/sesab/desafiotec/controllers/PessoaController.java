package com.sesab.desafiotec.controllers;

import com.sesab.desafiotec.models.Pessoa;
import com.sesab.desafiotec.controllers.util.JsfUtil;
import com.sesab.desafiotec.controllers.util.JsfUtil.PersistAction;


import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import javax.inject.Named;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;


@Named("pessoaController")
@SessionScoped
public class PessoaController implements Serializable {

    @Resource
    private UserTransaction utx = null;
    @PersistenceUnit(unitName = "com.SESAB_desafiotec_war_1.0-SNAPSHOTPU")
    private EntityManagerFactory emf = null;

    private PessoaJpaController jpaController = null;
    private List<Pessoa> items = null;
    private Pessoa selected;

    public PessoaController() {
    }

    public Pessoa getSelected() {
        return selected;
    }

    public void setSelected(Pessoa selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private PessoaJpaController getJpaController() {
        if (jpaController == null) {
            jpaController = new PessoaJpaController(utx, emf);
        }
        return jpaController;
    }

    public Pessoa prepareCreate() {
        selected = new Pessoa();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("PessoaCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("PessoaUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("PessoaDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Pessoa> getItems() {
        if (items == null) {
            items = getJpaController().findPessoaEntities();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction == PersistAction.UPDATE) {
                    getJpaController().edit(selected);
                } else if (persistAction == PersistAction.CREATE) {
                    getJpaController().create(selected);
                } else {
                    getJpaController().destroy(selected.getId());
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public List<Pessoa> getItemsAvailableSelectMany() {
        return getJpaController().findPessoaEntities();
    }

    public List<Pessoa> getItemsAvailableSelectOne() {
        return getJpaController().findPessoaEntities();
    }

    @FacesConverter(forClass = Pessoa.class)
    public static class PessoaControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PessoaController controller = (PessoaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "pessoaController");
            return controller.getJpaController().findPessoa(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Pessoa) {
                Pessoa o = (Pessoa) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Pessoa.class.getName()});
                return null;
            }
        }

    }

}
