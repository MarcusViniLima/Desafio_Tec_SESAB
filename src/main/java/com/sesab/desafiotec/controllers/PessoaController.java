package com.sesab.desafiotec.controllers;

import com.sesab.desafiotec.models.Pessoa;
import com.sesab.desafiotec.controllers.util.JsfUtil;
import com.sesab.desafiotec.controllers.util.JsfUtil.PersistAction;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("pessoaController")
@SessionScoped
public class PessoaController implements Serializable {

    @EJB
    private com.sesab.desafiotec.controllers.PessoaFacade ejbFacade;
    private List<Pessoa> items = null;
    private Pessoa selected;
    
    private String nomeFiltro;
    private String cpfFiltro;
    private Date dataInicioFiltro;
    private Date dataFimFiltro;

    public String getNomeFiltro() {
        return nomeFiltro;
    }

    public void setNomeFiltro(String nomeFiltro) {
        this.nomeFiltro = nomeFiltro;
    }

    public String getCpfFiltro() {
        return cpfFiltro;
    }

    public void setCpfFiltro(String cpfFiltro) {
        this.cpfFiltro = cpfFiltro;
    }

    public Date getDataInicioFiltro() {
        return dataInicioFiltro;
    }

    public void setDataInicioFiltro(Date dataInicioFiltro) {
        this.dataInicioFiltro = dataInicioFiltro;
    }

    public Date getDataFimFiltro() {
        return dataFimFiltro;
    }

    public void setDataFimFiltro(Date dataFimFiltro) {
        this.dataFimFiltro = dataFimFiltro;
    }

    public PessoaController() {
    }

    @PostConstruct
    public void init() {
        selected = new Pessoa();
        // Garante data atual
        if (selected.getDataCriacao() == null) {
            selected.setDataCriacao(new Date());
        }
    }

    public Pessoa getSelected() {
        if (selected == null) {
            selected = new Pessoa();
        }
        // Garante que sempre tenha data atual se for nula
        if (selected.getDataCriacao() == null) {
            selected.setDataCriacao(new Date());
        }
        return selected;
    }

    public void setSelected(Pessoa selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private PessoaFacade getFacade() {
        return ejbFacade;
    }

    public Pessoa prepareCreate() {
        selected = new Pessoa();
        selected.setDataCriacao(new Date());
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
            items = getFacade().findWithFilters(nomeFiltro, cpfFiltro, dataInicioFiltro, dataFimFiltro);
        }
        return items;
    }
    
    public void applyFilters() {
        items = null;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
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

    public Pessoa getPessoa(java.lang.Long id) {
        return getFacade().find(id);
    }


    public List<Pessoa> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Pessoa> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Pessoa.class)
    public static class PessoaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PessoaController controller = (PessoaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "pessoaController");
            return controller.getPessoa(getKey(value));
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

        @Override
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
