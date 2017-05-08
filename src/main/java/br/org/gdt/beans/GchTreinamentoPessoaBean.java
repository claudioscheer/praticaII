/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.org.gdt.beans;

import br.org.gdt.model.GchTreinamentos;
import br.org.gdt.model.GchTreinamentospessoas;
import br.org.gdt.model.RecPessoa;
import br.org.gdt.service.GchTreinamentoPessoaService;
import br.org.gdt.service.GchTreinamentosService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 *
 * @author Diego
 */
@ManagedBean
@RequestScoped
public class GchTreinamentoPessoaBean {

    private boolean formAtivo = false;

    private Map<RecPessoa, Boolean> checked = new HashMap<RecPessoa, Boolean>();

    private List<RecPessoa> pessoasVinculadas = new ArrayList<>();

    private GchTreinamentospessoas gchTreinamentospessoas = new GchTreinamentospessoas();
    private List<GchTreinamentospessoas> todosGchTreinamentosPessoas;

    @ManagedProperty("#{gchTreinamentosService}")
    private GchTreinamentosService gchTreinamentosService;

    @ManagedProperty("#{gchTreinamentoPessoaService}")
    private GchTreinamentoPessoaService gchTreinamentospessoasService;

    public GchTreinamentoPessoaBean() {

    }

    public String save() {

//        if (gchTreinamentos.getTreiCodigo() > 0) {
//            gchTreinamentosService.update(gchTreinamentos);
//        } else {
        System.out.println("Teste teste: " + gchTreinamentospessoas.getTreiCodigo().getTreiNome());

        Iterator<RecPessoa> keyIterrator = checked.keySet().iterator();

        while (keyIterrator.hasNext()) {

            RecPessoa pessoa = keyIterrator.next();
            Boolean value = checked.get(pessoa);

            System.out.println(pessoa.getRecNomecompleto() + " - " + value);

            if (value) {

                gchTreinamentospessoas.setRecIdpessoa(pessoa);

                gchTreinamentospessoasService.save(gchTreinamentospessoas);
            }
        }

//        System.out.println("Salvar: " + pessoasVinculadas.get(0).getRecNomecompleto());
//        gchTreinamentospessoas.setRecIdpessoa(pessoasVinculadas.get(0));
//        }
        todosGchTreinamentosPessoas = null;
        this.formAtivo = false;
        gchTreinamentospessoas = null;
//        FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "Treinamentos.xhtml?faces-redirect=true");

        return "Treinamentos";
    }

    public void cancel() {
        this.formAtivo = false;
        this.gchTreinamentospessoas = new GchTreinamentospessoas();
    }

    public void add() {
        System.out.println("Aqui tambem ta tretando");
        this.formAtivo = true;
        this.gchTreinamentospessoas = new GchTreinamentospessoas();
    }

    public String excluir(GchTreinamentospessoas gchTreinamentosPessoas) {
        gchTreinamentospessoasService.delete(gchTreinamentosPessoas.getTreiPescodigo());
        todosGchTreinamentosPessoas.remove(gchTreinamentosPessoas);
//        FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "Treinamentos.xhtml?faces-redirect=true");
        return "Treinamentos";
    }

    public void adicionaPessoa(RecPessoa recPessoa) {

        if (recPessoa.getRecIdpessoa() == 0) {
            return;
        }

        pessoasVinculadas.add(recPessoa);

    }

    public String prepareEdit(GchTreinamentos gchTreinamentos) {
        this.formAtivo = true;

        this.gchTreinamentospessoas.setTreiCodigo(gchTreinamentos);

        return "VincularTreinamentos";
    }

    public String buscaTreinamentoPorId(long id) {

        System.out.println("Id Treinamento" + id);

        if (id != 0) {

            gchTreinamentospessoas.setTreiCodigo(gchTreinamentosService.findById(id));

//            FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "VincularPessoasTreinamento.xhtml?faces-redirect=true");
            return "VincularTreinamentos";

        }
        return null;
    }

    public boolean isFormAtivo() {
        return formAtivo;
    }

    public GchTreinamentospessoas getGchTreinamentospessoas() {
        return gchTreinamentospessoas;
    }

    public void setGchTreinamentospessoas(GchTreinamentospessoas gchTreinamentospessoas) {
        this.gchTreinamentospessoas = gchTreinamentospessoas;
    }

    public List<GchTreinamentospessoas> getTodosGchTreinamentosPessoas() {
        return todosGchTreinamentosPessoas;
    }

    public void setTodosGchTreinamentosPessoas(List<GchTreinamentospessoas> todosGchTreinamentosPessoas) {
        this.todosGchTreinamentosPessoas = todosGchTreinamentosPessoas;
    }

    public GchTreinamentoPessoaService getGchTreinamentospessoasService() {
        return gchTreinamentospessoasService;
    }

    public void setGchTreinamentospessoasService(GchTreinamentoPessoaService gchTreinamentospessoasService) {
        this.gchTreinamentospessoasService = gchTreinamentospessoasService;
    }

    public Map<RecPessoa, Boolean> getChecked() {
        return checked;
    }

    public void setChecked(Map<RecPessoa, Boolean> checked) {
       this.checked = checked;
    }

    public List<RecPessoa> getPessoasVinculadas() {
        return pessoasVinculadas;
    }

    public void setPessoasVinculadas(List<RecPessoa> pessoasVinculadas) {
        this.pessoasVinculadas = pessoasVinculadas;
    }

    public GchTreinamentosService getGchTreinamentosService() {
        return gchTreinamentosService;
    }

    public void setGchTreinamentosService(GchTreinamentosService gchTreinamentosService) {
        this.gchTreinamentosService = gchTreinamentosService;
    }

}
