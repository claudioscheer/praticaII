/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.org.gdt.beans;

import br.org.gdt.model.CsbffCargos;
import br.org.gdt.model.RecPessoa;
import br.org.gdt.service.CsbffCargosService;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author MARCOS FELIPE
 */
@ManagedBean
@SessionScoped
public class CsbffCargosBean {

    private boolean formAtivo = false;
    private int numeroCBO;
    private String nomeCBO;
    private CsbffCargos codigoColaborador;
    private CsbffCargos csbffcargos = new CsbffCargos();
    private List<CsbffCargos> todosCargos;

    @ManagedProperty("#{csbffCargosService}")
    private CsbffCargosService csbffCargosService;
    private RecPessoa recPessoa;

    public CsbffCargosBean() {
        csbffcargos = new CsbffCargos();

    }

    public String pg(CsbffCargos cargo) {
        this.csbffcargos = cargo;
        select(cargo);
        return "form_cargo";
    }

    public void select(CsbffCargos carg) {
        this.csbffcargos = carg;
        codigoColaborador = carg;
        altera(carg);

    }

    public String altera(CsbffCargos carg) {

        codigoColaborador = carg;

        csbffcargos.setCargoCodigoSuperior(BigInteger.valueOf(4));
        csbffcargos.setCargoDataDeCriacao(new Date());
        csbffcargos.setCargoDataUltimaAlteracao(new Date());
        csbffCargosService.update(codigoColaborador);

        return "cargo_consulta";
    }

    public String buscaNomeCBO() {

        if (nomeCBO != null) {
            todosCargos = csbffCargosService.findByCargos(numeroCBO);
            
        }
        return "cargo_consulta";
    }

    public String buscaPorCbo() {

        if (numeroCBO != 0) {
            todosCargos = csbffCargosService.findByCargos(numeroCBO);
            
        } else {
            todosCargos = csbffCargosService.findAll();
            
        }
        return "cargo_consulta";
    }

    public String save() {

        if (csbffcargos.getCargoCodigo() > 0) {
            csbffcargos.setCargoCodigoSuperior(BigInteger.valueOf(1));
            csbffcargos.setCargoDataDeCriacao(new Date());
            csbffcargos.setCargoDataUltimaAlteracao(new Date());
            csbffCargosService.update(csbffcargos);
          

        } else {
   
            csbffcargos.setCargoCodigoSuperior(BigInteger.valueOf(1));
            csbffcargos.setCargoDataDeCriacao(new Date());
            csbffcargos.setCargoDataUltimaAlteracao(new Date());
            csbffCargosService.save(csbffcargos);
            

        }
        add2();
        return "cargo_consulta";
    }

//   public String novo() {
//
//        save();
//
//        return "cargo_consulta";
//
//    }

    public String buscaPorId(int idcargo) {
        if (idcargo != 0) {
            csbffcargos = csbffCargosService.findById(idcargo);

        }
        return "form_cargo";
    }
    public String prepareEdit(CsbffCargos cargos) {
        this.formAtivo = true;
        this.csbffcargos = cargos;
        return "cargo_consulta";
    }

    public void cancel() {
        this.formAtivo = false;
        this.csbffcargos = new CsbffCargos();
    }

    public String add() {
        this.formAtivo = true;
        this.csbffcargos = new CsbffCargos();
        return "form_cargo";
    }
    public String add2() {
        this.formAtivo = true;
        this.csbffcargos = new CsbffCargos();
        return "cargo_consulta";
    }

    public String excluir(CsbffCargos cargos) {
        csbffCargosService.delete(cargos.getCargoCodigo());
        todosCargos.remove(cargos);
        return "cargos";
    }


    public CsbffCargos getCargoCodigo() {
        return csbffcargos;
    }

    public void setCargoCodigo(CsbffCargos cargos) {
        this.csbffcargos = cargos;
    }

    public boolean isFormAtivo() {
        return formAtivo;
    }

    public void setFormAtivo(boolean formAtivo) {
        this.formAtivo = formAtivo;
    }

    public CsbffCargos getCsbffcargos() {
        return csbffcargos;
    }

    public void setCsbffcargos(CsbffCargos csbffcargos) {
        this.csbffcargos = csbffcargos;
    }

    public List<CsbffCargos> getTodosCargos() {
        if (todosCargos == null) {
            todosCargos = csbffCargosService.findAll();
        }
        return todosCargos;
    }

    public void setTodosCargos(List<CsbffCargos> todosCargos) {
        this.todosCargos = todosCargos;
    }

    public CsbffCargosService getCsbffCargosService() {
        return csbffCargosService;
    }

    public void setCsbffCargosService(CsbffCargosService csbffCargosService) {
        this.csbffCargosService = csbffCargosService;
    }

    public RecPessoa getRecPessoa() {
        return recPessoa;
    }

    public void setRecPessoa(RecPessoa recPessoa) {
        this.recPessoa = recPessoa;
    }

    public int getNumeroCBO() {
        return numeroCBO;
    }

    public void setNumeroCBO(int numeroCBO) {
        this.numeroCBO = numeroCBO;
    }

    public String getNomeCBO() {
        return nomeCBO;
    }

    public void setNomeCBO(String nomeCBO) {
        this.nomeCBO = nomeCBO;
    }

    public CsbffCargos getCodigoColaborador() {
        return codigoColaborador;
    }

    public void setCodigoColaborador(CsbffCargos codigoColaborador) {
        this.codigoColaborador = codigoColaborador;
    }
}