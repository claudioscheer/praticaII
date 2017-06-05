package br.org.gdt.beans;

import br.org.gdt.enums.AbrangenciaBeneficio;
import br.org.gdt.enums.TipoBeneficio;
import br.org.gdt.model.CsbffBeneficios;
import br.org.gdt.resources.Helper;
import br.org.gdt.service.CsbffBeneficiosService;
import br.org.gdt.service.CsbffDependentesService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class CsbffBeneficioBean {

    private CsbffBeneficios nomeBeneficio;
    private CsbffBeneficios codigoBeneficio;
    private boolean formAtivo = false;
//    private int nomeBeneficio;
    private CsbffBeneficios csbffBeneficios = new CsbffBeneficios();
    private List<CsbffBeneficios> todosCsbffBeneficios;
    private List<TipoBeneficio> csbffTipoBeneficiosList;

    @ManagedProperty("#{csbffBeneficiosService}")
    private CsbffBeneficiosService csbffBeneficiosService;

    @ManagedProperty("#{csbffDependentesService}")
    private CsbffDependentesService csbffDependentesService;

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.csbffBeneficios);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CsbffBeneficioBean other = (CsbffBeneficioBean) obj;
        if (!Objects.equals(this.csbffBeneficios, other.csbffBeneficios)) {
            return false;
        }
        return true;
    }

    public CsbffBeneficioBean(CsbffBeneficios codigoBeneficio) {
        this.codigoBeneficio = codigoBeneficio;
    }

    public CsbffBeneficioBean(List<CsbffBeneficios> todosCsbffBeneficios, TipoBeneficio tipoBeneficio, List<TipoBeneficio> csbffTipoBeneficiosList, CsbffBeneficiosService csbffBeneficiosService) {
        this.todosCsbffBeneficios = todosCsbffBeneficios;
        this.csbffTipoBeneficiosList = csbffTipoBeneficiosList;
        this.csbffBeneficiosService = csbffBeneficiosService;
    }

    public List<TipoBeneficio> getCsbffTipoBeneficiosList() {
        return csbffTipoBeneficiosList;
    }

    public void setCsbffTipoBeneficiosList(List<TipoBeneficio> csbffTipoBeneficiosList) {
        this.csbffTipoBeneficiosList = csbffTipoBeneficiosList;
    }

    public CsbffBeneficioBean() {

    }

    public List<CsbffBeneficios> getBeneficios() {
        List<CsbffBeneficios> beneficios = new ArrayList<>();
        beneficios.add(new CsbffBeneficios());
        return beneficios;
    }

//    public String buscaNome() {
//
//        if (nomeBeneficio !=0) {
//            csbffBeneficios = (CsbffBeneficios) csbffBeneficiosService.findAll();
//        }
//        return csbffBeneficios.getBeneficioNome();
//
//    }
//     public String prepareEdit(CsbffBeneficios beneficio) {
//        this.formAtivo = true;
//        this.csbffBeneficios = beneficio;
//        return "beneficio";
//    }
    public String altera(CsbffBeneficios beneficio) {
        codigoBeneficio = beneficio;
        csbffBeneficiosService.update(beneficio);

        return "cadastrobeneficios";
    }

    public void select(CsbffBeneficios beneficio) {
        this.csbffBeneficios = beneficio;
        codigoBeneficio = beneficio;
        altera(beneficio);

    }

    public String edita(CsbffBeneficios beneficio) {
        this.csbffBeneficios = beneficio;
        select(beneficio);
        return "cadastrobeneficios";
    }

    public TipoBeneficio[] getTipoBeneficio() {
        return TipoBeneficio.values();
    }

    public AbrangenciaBeneficio[] getAbrangenciaBeneficio() {
        return AbrangenciaBeneficio.values();
    }

    public String excluir(CsbffBeneficios beneficio) {
        csbffBeneficiosService.delete(beneficio.getBeneficioCodigo());
        todosCsbffBeneficios.remove(beneficio);
        return "consultabeneficios";
    }

    public String save() {
        String MsgNotificacao = "";
        try {
            if (csbffBeneficios.getBeneficioCodigo() > 0) {
                csbffBeneficiosService.update(csbffBeneficios);
                MsgNotificacao = "O beneficio foi atualizado com Sucesso!";
            } else {

                csbffBeneficiosService.save(csbffBeneficios);
                MsgNotificacao = "O beneficio foi cadastrado com Sucesso!";
            }
            Helper.mostrarNotificacao("Sucesso", MsgNotificacao, "sucess");
        } catch (Exception ex) {
            MsgNotificacao = "O benefício não pode ser cadastrado.";
            Helper.mostrarNotificacao("Erro", MsgNotificacao, "error");

        }
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            context.getExternalContext().redirect("cadastrobeneficios.xhtml");
        } catch (IOException ex) {
        }
        return "consultabeneficios";
    }

//    public String save() {
////        this.formAtivo = true;
//        if (csbffBeneficios.getBeneficioCodigo() > 0) {
//            csbffBeneficiosService.update(csbffBeneficios);
//
//        } else {
//            csbffBeneficiosService.save(csbffBeneficios);
//
//        }
//        todosCsbffBeneficios = csbffBeneficiosService.findAll();
//        this.csbffBeneficios = new CsbffBeneficios();
////        this.formAtivo = false;
//        return "consultabeneficios";
//    }
//    public void cancel() {
//        this.formAtivo = false;
//        this.csbffBeneficios = new CsbffBeneficios();
//    }

    public void cancel() {
        this.formAtivo = false;
        this.csbffBeneficios = new CsbffBeneficios();

        FacesContext context = FacesContext.getCurrentInstance();
        try {
            context.getExternalContext().redirect("consultabeneficios.xhtml");
        } catch (IOException ex) {

        }
    }
    public void add() {
        this.formAtivo = true;
        this.csbffBeneficios = new CsbffBeneficios();
    }

    public boolean isFormAtivo() {
        return formAtivo;
    }

    public void setFormAtivo(boolean formAtivo) {
        this.formAtivo = formAtivo;
    }

    public CsbffBeneficios getCsbffBeneficios() {
        return csbffBeneficios;
    }

    public void setCsbffBeneficios(CsbffBeneficios csbffBeneficios) {
        this.csbffBeneficios = csbffBeneficios;
    }

    public List<CsbffBeneficios> getTodosCsbffBeneficios() {

        if (todosCsbffBeneficios == null) {
            todosCsbffBeneficios = csbffBeneficiosService.findAll();
        }
        return todosCsbffBeneficios;
    }

    public void setTodosCsbffBeneficios(List<CsbffBeneficios> todosCsbffBeneficios) {
        this.todosCsbffBeneficios = todosCsbffBeneficios;
    }

    public CsbffBeneficiosService getCsbffBeneficiosService() {
        return csbffBeneficiosService;
    }

    public void setCsbffBeneficiosService(CsbffBeneficiosService csbffBeneficiosService) {
        this.csbffBeneficiosService = csbffBeneficiosService;
    }

    public CsbffBeneficios getCodigoBeneficio() {
        return codigoBeneficio;
    }

    public void setCodigoBeneficio(CsbffBeneficios codigoBeneficio) {
        this.codigoBeneficio = codigoBeneficio;
    }

    public CsbffDependentesService getCsbffDependentesService() {
        return csbffDependentesService;
    }

    public void setCsbffDependentesService(CsbffDependentesService csbffDependentesService) {
        this.csbffDependentesService = csbffDependentesService;
    }

    public CsbffBeneficios getNomeBeneficio() {
        return nomeBeneficio;
    }

    public void setNomeBeneficio(CsbffBeneficios nomeBeneficio) {
        this.nomeBeneficio = nomeBeneficio;
    }
}
