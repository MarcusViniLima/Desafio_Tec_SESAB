package com.sesab.desafiotec.controllers;

import com.sesab.desafiotec.models.Endereco;
import com.sesab.desafiotec.controllers.util.JsfUtil;
import com.sesab.desafiotec.controllers.util.JsfUtil.PersistAction;
import com.sesab.desafiotec.models.Pessoa;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("enderecoController")
@SessionScoped
public class EnderecoController implements Serializable {

    @EJB
    private com.sesab.desafiotec.controllers.EnderecoFacade ejbFacade;
    
    @EJB
    private PessoaFacade ejbFacadePessoa;
    
    private List<Endereco> items = null;
    private Endereco selected;

    public EnderecoController() {
    }

    public Endereco getSelected() {
        return selected;
    }

    public void setSelected(Endereco selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private EnderecoFacade getFacade() {
        return ejbFacade;
    }

    public Endereco prepareCreate() {
        selected = new Endereco();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("EnderecoCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("EnderecoUpdated"));
    }

    // EnderecoController.java
// ...
    public void destroy() {
        // Adicionar esta verificação
        if (selected != null && selected.getId() != null) {
            // Encontrar todas as pessoas que têm este endereço
            // e remover a associação em cada uma delas.
            // **Este método (findByEndereco) não existe, você precisará criá-lo no PessoaFacade.**
            List<Pessoa> pessoasComEndereco = ejbFacadePessoa.findByEndereco(selected);

            for (Pessoa pessoa : pessoasComEndereco) {
                pessoa.getEnderecos().remove(selected);
                ejbFacadePessoa.edit(pessoa);
            }
        }

        // Agora que o endereço não está mais associado a nenhuma pessoa, você pode excluí-lo.
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("EnderecoDeleted"));

        if (!JsfUtil.isValidationFailed()) {
            selected = null;
            items = null;
        }
    }
// ...

    public List<Endereco> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
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

    public Endereco getEndereco(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Endereco> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Endereco> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Endereco.class)
    public static class EnderecoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            EnderecoController controller = (EnderecoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "enderecoController");
            return controller.getEndereco(getKey(value));
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
            if (object instanceof Endereco) {
                Endereco o = (Endereco) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Endereco.class.getName()});
                return null;
            }
        }

    }

    public String createAndReturn() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("EnderecoCreated"));

        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String source = params.get("source");

        if ("pessoa".equals(source)) {
            return "/pessoa/Create?faces-redirect=true";
        }
        return null;
    }

    public void prepareCreateFromPessoa() {
        selected = new Endereco();

        // Obter parâmetros da URL se vierem do cadastro de pessoa
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String cep = params.get("cep");
        String logradouro = params.get("logradouro");

        if (cep != null) {
            selected.setCep(cep);
        }
        if (logradouro != null) {
            selected.setLogradouro(logradouro);
        }

        initializeEmbeddableKey();
    }

}
