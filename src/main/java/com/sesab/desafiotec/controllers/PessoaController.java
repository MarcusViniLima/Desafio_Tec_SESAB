package com.sesab.desafiotec.controllers;

import com.sesab.desafiotec.models.Pessoa;
import com.sesab.desafiotec.controllers.util.JsfUtil;
import com.sesab.desafiotec.controllers.util.JsfUtil.PersistAction;
import com.sesab.desafiotec.models.Endereco;

import java.io.Serializable;
import java.util.ArrayList;
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
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

@Named("pessoaController")
@SessionScoped
public class PessoaController implements Serializable {

    @EJB
    private PessoaFacade ejbFacade;

    @EJB
    private EnderecoFacade enderecoFacade;

    private List<Pessoa> items = null;
    private Pessoa selected;
    private Pessoa selectedWithEnderecos;

    private String nomeFiltro;
    private String cpfFiltro;
    private Date dataInicioFiltro;
    private Date dataFimFiltro;

    private String cepBusca;
    private String logradouroBusca;
    private List<Endereco> enderecosDaPessoa;
    private Endereco enderecoSelecionado;
    private List<Endereco> todosOsEnderecos;
    private Endereco novoEndereco; // NOVO CAMPO ADICIONADO

    public PessoaController() {
    }

    @PostConstruct
    public void init() {
        selected = new Pessoa();
        if (selected.getDataCriacao() == null) {
            selected.setDataCriacao(new Date());
        }
        enderecosDaPessoa = new ArrayList<>();
        novoEndereco = new Endereco(); // INICIALIZAÇÃO DO NOVO CAMPO
    }

    public Pessoa getSelectedWithEnderecos() {
        if (selected != null && selected.getId() != null) {
            if (selectedWithEnderecos == null || !selectedWithEnderecos.getId().equals(selected.getId())) {
                selectedWithEnderecos = getFacade().findWithEnderecos(selected.getId());
            }
            return selectedWithEnderecos;
        }
        return selected;
    }

    public Pessoa getSelected() {
        if (selected == null) {
            selected = new Pessoa();
        }
        if (selected.getDataCriacao() == null) {
            selected.setDataCriacao(new Date());
        }
        return selected;
    }

    public void setSelected(Pessoa selected) {
        this.selected = selected;
    }

    // NOVO GETTER E SETTER
    public Endereco getNovoEndereco() {
        if (novoEndereco == null) {
            novoEndereco = new Endereco();
        }
        return novoEndereco;
    }

    public void setNovoEndereco(Endereco novoEndereco) {
        this.novoEndereco = novoEndereco;
    }

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

    public String getCepBusca() {
        return cepBusca;
    }

    public void setCepBusca(String cepBusca) {
        this.cepBusca = cepBusca;
    }

    public String getLogradouroBusca() {
        return logradouroBusca;
    }

    public void setLogradouroBusca(String logradouroBusca) {
        this.logradouroBusca = logradouroBusca;
    }

    public List<Endereco> getEnderecosDaPessoa() {
        if (enderecosDaPessoa == null) {
            enderecosDaPessoa = new ArrayList<>();
        }
        return enderecosDaPessoa;
    }

    public void setEnderecosDaPessoa(List<Endereco> enderecosDaPessoa) {
        this.enderecosDaPessoa = enderecosDaPessoa;
    }

    public Endereco getEnderecoSelecionado() {
        return enderecoSelecionado;
    }

    public void setEnderecoSelecionado(Endereco enderecoSelecionado) {
        this.enderecoSelecionado = enderecoSelecionado;
    }

    public List<Endereco> getTodosOsEnderecos() {
        if (todosOsEnderecos == null) {
            todosOsEnderecos = enderecoFacade.findAll();
        }
        return todosOsEnderecos;
    }

    private PessoaFacade getFacade() {
        return ejbFacade;
    }

    public List<Pessoa> getItems() {
        if (items == null) {
            items = getFacade().findWithFilters(nomeFiltro, cpfFiltro, dataInicioFiltro, dataFimFiltro);
        }
        return items;
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

    public Pessoa prepareCreate() {
        selected = new Pessoa();
        selected.setDataCriacao(new Date());
        enderecosDaPessoa = new ArrayList<>();
        novoEndereco = new Endereco(); // RESETA O NOVO ENDEREÇO
        initializeEmbeddableKey();
        return selected;
    }

    // NOVO MÉTODO ADICIONADO
    public void adicionarEndereco() {
        if (novoEndereco != null
                && !StringUtils.isEmpty(novoEndereco.getLogradouro())
                && !StringUtils.isEmpty(novoEndereco.getCep())) {

            // Verifica se o endereço já foi adicionado
            boolean enderecoExiste = false;
            for (Endereco end : enderecosDaPessoa) {
                if (end.getCep().equals(novoEndereco.getCep())
                        && end.getLogradouro().equals(novoEndereco.getLogradouro())) {
                    enderecoExiste = true;
                    break;
                }
            }

            if (!enderecoExiste) {
                getEnderecosDaPessoa().add(novoEndereco);
                JsfUtil.addSuccessMessage("Endereço adicionado com sucesso!");

                // Limpa o formulário para novo preenchimento
                novoEndereco = new Endereco();
            } else {
                JsfUtil.addWarningMessage("Este endereço já foi adicionado.");
            }
        } else {
            JsfUtil.addErrorMessage("Preencha todos os campos obrigatórios (Logradouro e CEP)!");
        }
    }

    public void create() {
        try {
            Logger.getLogger(PessoaController.class.getName()).log(Level.INFO,
                    "Iniciando criação da pessoa com {0} endereços", enderecosDaPessoa.size());

            selected.setEnderecos(enderecosDaPessoa);

            if (selected.getEnderecos() != null) {
                Logger.getLogger(PessoaController.class.getName()).log(Level.INFO,
                        "Pessoa tem {0} endereços associados", selected.getEnderecos().size());
            }

            persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("PessoaCreated"));

            if (!JsfUtil.isValidationFailed()) {
                JsfUtil.addSuccessMessage("Pessoa criada com sucesso com " + enderecosDaPessoa.size() + " endereços!");

                items = null;
                enderecosDaPessoa = new ArrayList<>();
                selected = new Pessoa();
                selected.setDataCriacao(new Date());
                novoEndereco = new Endereco(); // LIMPA O NOVO ENDEREÇO
            }
        } catch (Exception e) {
            Logger.getLogger(PessoaController.class.getName()).log(Level.SEVERE, "Erro ao criar pessoa", e);
            JsfUtil.addErrorMessage("Erro ao salvar: " + e.getMessage());
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("PessoaUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("PessoaDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null;
            items = null;
        }
    }

    public void prepareView(Pessoa pessoa) {
        this.selected = pessoa;
        this.selectedWithEnderecos = null;
    }

    public void applyFilters() {
        items = null;
    }

    public void buscarEAdicionarEndereco() {
        Endereco enderecoEncontrado = null;
        String tipoBusca = "";

        if (cepBusca != null && !cepBusca.trim().isEmpty()) {
            enderecoEncontrado = enderecoFacade.findByCep(cepBusca);
            tipoBusca = "CEP: " + cepBusca;
        } else if (logradouroBusca != null && !logradouroBusca.trim().isEmpty()) {
            enderecoEncontrado = enderecoFacade.findByLogradouro(logradouroBusca);
            tipoBusca = "Logradouro: " + logradouroBusca;
        }

        if (enderecoEncontrado != null) {
            if (!getEnderecosDaPessoa().contains(enderecoEncontrado)) {
                getEnderecosDaPessoa().add(enderecoEncontrado);
                JsfUtil.addSuccessMessage("✅ Endereço encontrado por " + tipoBusca + " e adicionado!");
            } else {
                JsfUtil.addWarningMessage("⚠️ Este endereço já foi adicionado anteriormente.");
            }
        } else {
            JsfUtil.addErrorMessage("❌ Endereço não encontrado por " + tipoBusca + ". Cadastre-o primeiro.");
        }

        cepBusca = null;
        logradouroBusca = null;
    }

    public void adicionarEnderecoSelecionado() {
        if (enderecoSelecionado != null) {
            if (!getEnderecosDaPessoa().contains(enderecoSelecionado)) {
                getEnderecosDaPessoa().add(enderecoSelecionado);
                JsfUtil.addSuccessMessage("Endereço adicionado com sucesso!");
            } else {
                JsfUtil.addWarningMessage("Este endereço já foi adicionado.");
            }
            enderecoSelecionado = null;
        }
    }

    public void removerEndereco(Endereco endereco) {
        getEnderecosDaPessoa().remove(endereco);
        JsfUtil.addSuccessMessage("Endereço removido com sucesso!");
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
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
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Erro EJB: " + msg, ex);

                    if (msg.contains("ConstraintViolation") || msg.contains("unique") || msg.contains("Duplicate")) {
                        JsfUtil.addErrorMessage("Dados duplicados ou inválidos. Verifique CPF, email ou outros campos únicos.");
                    } else {
                        JsfUtil.addErrorMessage("Erro ao salvar: " + msg);
                    }
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Erro geral: ", ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            }
        }
    }

    @FacesConverter(forClass = Pessoa.class)
    public static class PessoaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PessoaController controller = (PessoaController) facesContext.getApplication().getELResolver()
                    .getValue(facesContext.getELContext(), null, "pessoaController");
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
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
                        "object {0} is of type {1}; expected type: {2}",
                        new Object[]{object, object.getClass().getName(), Pessoa.class.getName()});
                return null;
            }
        }
    }

    public void onRowSelect(SelectEvent event) {
        // Método vazio - apenas para trigger do evento
    }

    public void onRowUnselect(UnselectEvent event) {
        // Método vazio - apenas para trigger do evento
    }
}
