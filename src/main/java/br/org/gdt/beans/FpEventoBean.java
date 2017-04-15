package br.org.gdt.beans;

import br.org.gdt.model.FpEvento;
import br.org.gdt.service.FpEventoService;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class FpEventoBean {

    private boolean formAtivo = false;

    private FpEvento fpEvento = new FpEvento();
    private List<FpEvento> todosFpEvento;

    @ManagedProperty("#{fpEventoService}")
    private FpEventoService fpEventoService;

    public FpEventoBean() {

    }

    public void save() {
        if (fpEvento.getEveId() > 0) {
            fpEventoService.update(fpEvento);
        } else {
            fpEventoService.save(fpEvento);
        }
        todosFpEvento = fpEventoService.findAll();
        this.formAtivo = false;
    }

    public void cancel() {
        this.formAtivo = false;
        this.fpEvento = new FpEvento();
    }

    public void add() {
        this.formAtivo = true;
        this.fpEvento = new FpEvento();
    }

    public String excluir(FpEvento fpEvento) {
        fpEventoService.delete(fpEvento.getEveId());
        todosFpEvento.remove(fpEvento);
        return "eventos";
    }

    public String prepareEdit(FpEvento fpEvento) {
        this.formAtivo = true;
        this.fpEvento = fpEvento;
        return "eventos";
    }

    public boolean isFormAtivo() {
        return formAtivo;
    }

    public void setFormAtivo(boolean formAtivo) {
        this.formAtivo = formAtivo;
    }

    public FpEvento getFpEvento() {
        return fpEvento;
    }

    public void setFpEvento(FpEvento fpEvento) {
        this.fpEvento = fpEvento;
    }

    public List<FpEvento> getTodosFpEvento() {
        if (todosFpEvento == null) {
            todosFpEvento = fpEventoService.findAll();
        }
        return todosFpEvento;
    }

    public void setTodosFpEvento(List<FpEvento> todosFpEvento) {
        this.todosFpEvento = todosFpEvento;
    }

    public FpEventoService getFpEventoService() {
        return fpEventoService;
    }

    public void setFpEventoService(FpEventoService fpEventoService) {
        this.fpEventoService = fpEventoService;
    }

}