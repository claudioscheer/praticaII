/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.org.gdt.beans;

import br.org.gdt.model.GchMunicipios;
import br.org.gdt.model.GchTreinamentos;
import br.org.gdt.model.GchUfs;
import br.org.gdt.resources.Helper;
import br.org.gdt.service.GchMunicipiosService;
import br.org.gdt.service.GchTreinamentosService;
import br.org.gdt.service.GchUFsService;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Diego
 */
@ManagedBean
@SessionScoped
public class GchTreinamentosBean {

//    private boolean formAtivo = false;
    private GchTreinamentos gchTreinamentos = new GchTreinamentos();
    private List<GchTreinamentos> todosGchTreinamentos;
    private List<GchMunicipios> todosGchMunicipiosUF;

    @ManagedProperty("#{gchTreinamentosService}")
    private GchTreinamentosService gchTreinamentosService;

    private GchMunicipios gchMunicipio;

    private long curCodigoCombo;
    private long munCodigoCombo;

    private Date dataInicio;
    private Date dataFim;

    private long ufCodigoCombo;

    @ManagedProperty("#{gchMunicipiosService}")
    private GchMunicipiosService gchMunicipiosService;

    private List<GchUfs> todosGchUfs;

    @ManagedProperty("#{gchUFsService}")
    private GchUFsService gchUFsService;

    public GchTreinamentosBean() {

    }

    public String save() {

        String MsgNotificacao = "";

     
//      DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formato = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yy");

        try {

            gchTreinamentos.setTreiDataInicio(dataInicio);
            gchTreinamentos.setTreiDataFim(dataFim);

            if (gchTreinamentos.getTreiCodigo() > 0) {

                gchTreinamentosService.update(gchTreinamentos);
                MsgNotificacao = "O treinamento " + gchTreinamentos.getTreiNome() + " foi atualizado com sucesso!";

            } else {

                gchTreinamentos.setTreiDatainclusao(new Date());
                gchTreinamentosService.save(gchTreinamentos);
                MsgNotificacao = "O treinamento " + gchTreinamentos.getTreiNome() + " foi cadastrado com sucesso!";
            }

            Helper.mostrarNotificacao("Sucesso", MsgNotificacao, "success");

        } catch (Exception ex) {
          
            MsgNotificacao = "Não foi possível cadastrar o treinamento " + gchTreinamentos.getTreiNome();
            Helper.mostrarNotificacao("Erro", MsgNotificacao, "error");
        }

        todosGchMunicipiosUF = null;
        gchTreinamentos = new GchTreinamentos();
        todosGchUfs = null;
        todosGchMunicipiosUF = null;
        todosGchTreinamentos = null;
        dataInicio = null;
        dataFim = null;
        ufCodigoCombo = 0;
        return "Treinamentos";

    }

    public boolean buscaTreinamentosPorCurso(long id) {

        List<GchTreinamentos> retorno = new ArrayList<>();

        retorno = gchTreinamentosService.BuscaTreinamentoPorCurso(id);

        if (retorno.isEmpty()) {

            return true;

        } else {

            return false;
        }

    }

    public String cancel() {
//        this.formAtivo = false;
        this.gchTreinamentos = new GchTreinamentos();

        return "Treinamentos";

    }

    public void add() {
        
//        this.formAtivo = true;
        this.gchTreinamentos = new GchTreinamentos();
    }

    public String excluir(GchTreinamentos gchTreinamentos) {
        try {
            gchTreinamentosService.delete(gchTreinamentos.getTreiCodigo());
            todosGchTreinamentos.remove(gchTreinamentos);

             Helper.mostrarNotificacao("Sucesso", "Treinamento <b>" + gchTreinamentos.getTreiNome() + "</b> foi excluído com sucesso!", "success");
            
        } catch (Exception e) {

            //Formulário já vinculado a uma pessoa
            if (e.toString().indexOf("fk_im6u1b5d9yqnf8r9ai7jiad1x") > 0) {

                Helper.mostrarNotificacao("Erro", "Este treinamento já possui colaboradores vinculados!", "error");
            } else {

                Helper.mostrarNotificacao("Erro", "Uma Exceção não tratada impediu a exclusão do treinamento! : " + e.getMessage(), "error");
            }
        }
        return "Treinamentos";
    }

    public String prepareEdit(GchTreinamentos gchTreinamentos) {
//        this.formAtivo = true;
        this.gchTreinamentos = gchTreinamentos;
        return "treinamentos";
    }

//    public boolean isFormAtivo() {
//        return formAtivo;
//    }
    public GchTreinamentos getGchTreinamentos() {
        return gchTreinamentos;
    }

    public void setGchTreinamentos(GchTreinamentos gchTreinamentos) {
        this.gchTreinamentos = gchTreinamentos;
    }

    public List<GchTreinamentos> getTodosGchTreinamentos() {

        

        if (todosGchTreinamentos == null) {

            

            todosGchTreinamentos = new ArrayList<>();

            todosGchTreinamentos = gchTreinamentosService.findAll();

        }

        return todosGchTreinamentos;
    }

    public String buscaTreinamentoPorId(int id) {

        

        if (id != 0) {

            gchTreinamentos = gchTreinamentosService.findById(id);

            Date a = dataInicio;

            ufCodigoCombo = gchTreinamentos.getMunCodigo().getUfCodigo().getUfCodigo();

            carregaMunicipios();

//            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            dataInicio = gchTreinamentos.getTreiDataInicio();
            dataFim = gchTreinamentos.getTreiDataFim();

            return "CadastroTreinamentos";

        }
        return null;
    }

    public void carregaMunicipios() {

        if (ufCodigoCombo != 0) {
            todosGchMunicipiosUF = gchMunicipiosService.findUfCodigo(ufCodigoCombo);
        }

    }

    public void setTodosGchTreinamentos(List<GchTreinamentos> todosGchTreinamentos) {
        this.todosGchTreinamentos = todosGchTreinamentos;
    }

    public GchTreinamentosService getGchTreinamentosService() {
        return gchTreinamentosService;
    }

    public void setGchTreinamentosService(GchTreinamentosService gchTreinamentosService) {
        this.gchTreinamentosService = gchTreinamentosService;
    }

    public long getCurCodigoCombo() {
        return curCodigoCombo;
    }

    public void setCurCodigoCombo(long curCodigoCombo) {
        this.curCodigoCombo = curCodigoCombo;
    }

    public long getMunCodigoCombo() {
        return munCodigoCombo;
    }

    public void setMunCodigoCombo(long munCodigoCombo) {
        this.munCodigoCombo = munCodigoCombo;
    }

    public GchMunicipios getGchMunicipio() {
        return gchMunicipio;
    }

    public void setGchMunicipio(GchMunicipios gchMunicipio) {
        this.gchMunicipio = gchMunicipio;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public GchMunicipiosService getGchMunicipiosService() {
        return gchMunicipiosService;
    }

    public void setGchMunicipiosService(GchMunicipiosService gchMunicipiosService) {
        this.gchMunicipiosService = gchMunicipiosService;
    }

    public List<GchUfs> getTodosGchUfs() {

        if (todosGchUfs == null) {

            todosGchUfs = gchUFsService.findAll();

        }

        return todosGchUfs;
    }

    public void setTodosGchUfs(List<GchUfs> todosGchUfs) {
        this.todosGchUfs = todosGchUfs;
    }

    public GchUFsService getGchUFsService() {
        return gchUFsService;
    }

    public void setGchUFsService(GchUFsService gchUFsService) {
        this.gchUFsService = gchUFsService;
    }

    public List<GchMunicipios> getTodosGchMunicipiosUF() {
        return todosGchMunicipiosUF;
    }

    public void setTodosGchMunicipiosUF(List<GchMunicipios> todosGchMunicipiosUF) {
        this.todosGchMunicipiosUF = todosGchMunicipiosUF;
    }

    public long getUfCodigoCombo() {
        return ufCodigoCombo;
    }

    public void setUfCodigoCombo(long ufCodigoCombo) {
        this.ufCodigoCombo = ufCodigoCombo;
    }

}
