package br.org.gdt.beans;

import br.org.gdt.enums.EstadoCivil;
import br.org.gdt.enums.Sexo;
import br.org.gdt.model.RecPessoa;
import br.org.gdt.resources.Helper;
import br.org.gdt.service.RecPessoaService;
import java.io.IOException;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class CsbffDadosPessoaisBean {

    private boolean formAtivo = false;
    private String recCpf;

    private RecPessoa recPessoa = new RecPessoa();
    private List<RecPessoa> recPessoaList;

    @ManagedProperty("#{recPessoaService}")
    private RecPessoaService recPessoaService;
    private boolean colaboradorInativo;

    public CsbffDadosPessoaisBean() {

    }

    public Sexo[] getGeneros() {
        return Sexo.values();
    }

    public EstadoCivil[] getEstadoCivil() {
        return EstadoCivil.values();
    }

    public void buscarCpf() {
        recPessoa = recPessoaService.findByRecCpf(recCpf);
        String MsgNotificacao = "";
        while (recPessoa == null) {
            MsgNotificacao = "A pessoa não existe.";
            Helper.mostrarNotificacao("Atenção!", MsgNotificacao, "error");
            return;
        }
        if (recPessoa.colaboradorInativo == true) {
            MsgNotificacao = "O colaborador está inativo.";
            Helper.mostrarNotificacao("Atenção!", MsgNotificacao, "info");
        }
        if (recPessoa == null) {
            recPessoa = new RecPessoa();

        }

    }

    public List<RecPessoa> getPessoas() {
        List<RecPessoa> pessoas = recPessoaService.findAll();

        return pessoas;
    }

//    public String saveDadosPessoais() {
//        if (recPessoa.getRecIdpessoa() > 0) {
//            recPessoaService.update(recPessoa);
//        }
//        recPessoaList = recPessoaService.findAll();
//        this.formAtivo = false;
//        this.recPessoa = new RecPessoa();
//         return "dadosprofissionais";
//    }
//    public String cancel() {
//        this.formAtivo = false;
//        this.recPessoa = new RecPessoa();
//        return null;
//
//    }
    public void cancel() {
        this.formAtivo = false;
        this.recPessoa = new RecPessoa();

        FacesContext context = FacesContext.getCurrentInstance();
        try {
            context.getExternalContext().redirect("listaadmissao.xhtml");
        } catch (IOException ex) {

        }
    }

    public String saveDadosPessoais() {
        String MsgNotificacao = "";
        try {
            if (recPessoa.getRecIdpessoa() > 0) {
                recPessoaService.update(recPessoa);
            }
            MsgNotificacao = "Os dados pessoais do colaborador foram atualizados com Sucesso!";
            Helper.mostrarNotificacao("Sucesso", MsgNotificacao, "success");
        } catch (Exception ex) {
            MsgNotificacao = "Os dados pessoais não foram atualizados ";
            Helper.mostrarNotificacao("Erro", MsgNotificacao, "error");
        }
        recPessoaList = recPessoaService.findAll();
        

        return "dadosprofissionais";
    }

    public String editaConsulta(RecPessoa pessoas) {
        this.recPessoa = pessoas;
        selectConsulta(pessoas);
        return "dadospessoais";
    }

    public void selectConsulta(RecPessoa pessoas) {
        this.recPessoa = pessoas;
        alteraConsulta(pessoas);
    }

    public String alteraConsulta(RecPessoa pessoas) {
        recPessoaService.update(pessoas);
        return "pessoas";
    }
    public String criaContrato(RecPessoa pessoas) {
        this.recPessoa = pessoas;
        selectConsulta(pessoas);
        return "dadospessoais";
    }

    public String excluir(RecPessoa pessoas) {
        recPessoaService.delete(pessoas.getRecIdpessoa());
        recPessoaList.remove(pessoas);
        return "pessoas";
    }

    public boolean isFormAtivo() {
        return formAtivo;
    }

    public void setFormAtivo(boolean formAtivo) {
        this.formAtivo = formAtivo;
    }

    public RecPessoa getRecPessoa() {
        return recPessoa;
    }

    public void setRecPessoa(RecPessoa recPessoa) {
        this.recPessoa = recPessoa;
    }

    public List<RecPessoa> getRecPessoaList() {
        return recPessoaList;
    }

    public void setRecPessoaList(List<RecPessoa> recPessoaList) {
        this.recPessoaList = recPessoaList;
    }

    public RecPessoaService getRecPessoaService() {
        return recPessoaService;
    }

    public void setRecPessoaService(RecPessoaService recPessoaService) {
        this.recPessoaService = recPessoaService;
    }

    public String getRecCpf() {
        return recCpf;
    }

    public void setRecCpf(String recCpf) {
        this.recCpf = recCpf;
    }

    public boolean isColaboradorInativo() {
        return colaboradorInativo;
    }

    public void setColaboradorInativo(boolean colaboradorInativo) {
        this.colaboradorInativo = colaboradorInativo;
    }

}
