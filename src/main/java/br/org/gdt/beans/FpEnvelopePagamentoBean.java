package br.org.gdt.beans;

import br.org.gdt.enums.FpTipoFolha;
import br.org.gdt.model.FpEventoPeriodo;
import br.org.gdt.model.FpFolhaPeriodo;
import br.org.gdt.model.FpPeriodo;
import br.org.gdt.model.RecPessoa;
import br.org.gdt.resources.Helper;
import br.org.gdt.service.FpEventoService;
import br.org.gdt.service.FpFolhaPeriodoService;
import br.org.gdt.service.folhapagamento.CalcularFolha;
import br.org.gdt.service.FpPeriodoService;
import br.org.gdt.service.RecPessoaService;
import br.org.gdt.service.folhapagamento.DadosCalculadosDoFuncionario;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class FpEnvelopePagamentoBean {

    private boolean mostrarTodasFolhasPeriodo = false;
    private FpPeriodo fpPeriodo = new FpPeriodo();
    private RecPessoa recPessoa = new RecPessoa();
    private FpFolhaPeriodo fpFolhaPeriodo = new FpFolhaPeriodo();
    private FpTipoFolha fpTipoFolha;
    private List<FpFolhaPeriodo> todasFolhasPeriodo = new ArrayList<>();
    private List<FpPeriodo> todosFpPeriodo;

    @ManagedProperty("#{fpPeriodoService}")
    private FpPeriodoService fpPeriodoService;

    @ManagedProperty("#{calcularFolha}")
    private CalcularFolha calcularFolha;

    @ManagedProperty("#{recPessoaService}")
    private RecPessoaService recPessoaService;

    @ManagedProperty("#{fpEventoService}")
    private FpEventoService fpEventoService;

    @ManagedProperty("#{fpFolhaPeriodoService}")
    private FpFolhaPeriodoService fpFolhaPeriodoService;

    public FpEnvelopePagamentoBean() {
    }

    public void validarFolhaPeriodo(FpFolhaPeriodo fpFolhaPeriodo) {
        mostrarTodasFolhasPeriodo = false;

        recPessoa = fpFolhaPeriodo.getForPessoa();
        this.fpFolhaPeriodo = fpFolhaPeriodo;
        this.fpFolhaPeriodo.removerEventosNaoAlteraFolha();
    }

    public void mostrarTodasFolhasPeriodo() {
        if (mostrarTodasFolhasPeriodo) {
            mostrarTodasFolhasPeriodo = false;
            return;
        }

        if (fpPeriodo.getPerId() <= 0) {
            Helper.mostrarNotificacao("Período", "Selecione um período.", "error");
            return;
        }

        todasFolhasPeriodo = fpFolhaPeriodoService.findAllPeriodo(fpPeriodo);

        mostrarTodasFolhasPeriodo = true;
    }

    public void reiniciarTodaTela() {
        fpFolhaPeriodo = new FpFolhaPeriodo();
        mostrarTodasFolhasPeriodo = false;
    }

    public void selecionarTipoFolha() {
        reiniciarTodaTela();
    }

    public void selecionarPeriodo() {
        reiniciarTodaTela();
        FpPeriodo periodo = fpPeriodoService.findById(fpPeriodo.getPerId());
        if (periodo == null) {
            periodo = new FpPeriodo();
        }
        fpPeriodo = periodo;
    }

    public void selecionarPessoa() {
        RecPessoa pessoa = recPessoaService.BuscarId((int) recPessoa.getRecIdpessoa());
        if (pessoa == null) {
            Helper.mostrarNotificacao("Dados inválidos", "A pessoa não existe.", "error");
            recPessoa = new RecPessoa();
            return;
        } else if (!pessoa.getRecFuncionario()) {
            Helper.mostrarNotificacao("Dados inválidos", "A pessoa não é um colaborador.", "error");
            recPessoa = new RecPessoa();
            return;
        }

        recPessoa = pessoa;
    }

    public void buscarFolhaPeriodo() {
        if (recPessoa.getRecIdpessoa() <= 0) {
            Helper.mostrarNotificacao("Pessoa", "Selecione uma pessoa.", "error");
            return;
        }

        if (fpPeriodo.getPerId() <= 0) {
            Helper.mostrarNotificacao("Período", "Selecione um período.", "error");
            return;
        }

        FpFolhaPeriodo folhaPeriodo = fpFolhaPeriodoService.findByPessoaEPeriodo(fpPeriodo, recPessoa);
        if (folhaPeriodo == null) {
            Helper.mostrarNotificacao("Folha", "Folha não encontrada para esta pessoa.", "error");
            folhaPeriodo = new FpFolhaPeriodo();
        }
        fpFolhaPeriodo = folhaPeriodo;
        fpFolhaPeriodo.removerEventosNaoAlteraFolha();
        mostrarTodasFolhasPeriodo = false;
    }

    public void recalcularFolhaPeriodo() {
        try {
            DadosCalculadosDoFuncionario dadosCalculadosDoFuncionario = new DadosCalculadosDoFuncionario();
            dadosCalculadosDoFuncionario.setPeriodo(fpPeriodo);
            dadosCalculadosDoFuncionario.setDeletarJaCalculada(true);
            dadosCalculadosDoFuncionario.setRecalculando(true);

            if (recPessoa.getRecIdpessoa() <= 0) {
                Helper.mostrarNotificacao("Dados inválidos", "Selecione um colaborador.", "error");
                return;
            }
            dadosCalculadosDoFuncionario.setPessoa(recPessoa);

            List<FpEventoPeriodo> eventosVerificados = verificarEventosAjustadosManualmente();
            eventosVerificados.forEach(x -> x.setJaCalculado(false));

            dadosCalculadosDoFuncionario.setEventos(eventosVerificados);

            fpFolhaPeriodo = calcularFolha.calcularFolhaPagamentoFuncionario(dadosCalculadosDoFuncionario);
            fpFolhaPeriodo.removerEventosNaoAlteraFolha();

            Helper.mostrarNotificacao("Calcular folha", "Folha de pagamento recalculada.", "success");
        } catch (Exception e) {
            Helper.mostrarNotificacao("Calcular folha", "Não foi possível recalcular a folha de pagamento.", "error");
        }
    }

    private List<FpEventoPeriodo> verificarEventosAjustadosManualmente() {

        FpFolhaPeriodo folhaPeriodo = fpFolhaPeriodoService.findByPessoaEPeriodo(fpPeriodo, recPessoa);

        folhaPeriodo.getForEventos().forEach((eventoOriginal) -> {
            Optional<FpEventoPeriodo> optionalEventoAlterado = fpFolhaPeriodo.getForEventos().stream()
                    .filter(x -> x.getEvpEvento().getEveId() == eventoOriginal.getEvpEvento().getEveId())
                    .findFirst();

            if (!optionalEventoAlterado.isPresent()) {
                return;
            }

            FpEventoPeriodo eventoAlterado = optionalEventoAlterado.get();

            boolean valoresAlterados = eventoOriginal.getEvpValorReferencia() != eventoAlterado.getEvpValorReferencia()
                    || eventoOriginal.getEvpValor() != eventoAlterado.getEvpValor();

            eventoOriginal.setEventoAlteradoManualmente(valoresAlterados);
            if (valoresAlterados) {
                eventoOriginal.setEvpValorReferencia(eventoAlterado.getEvpValorReferencia());
                eventoOriginal.setEvpValor(eventoAlterado.getEvpValor());
            }
        });

        return folhaPeriodo.getForEventos();
    }

    public void reimprimirFolhaPagamento(FpFolhaPeriodo fpFolhaPeriodo) {
        imprimirFolhaPagamento(fpFolhaPeriodo);
    }

    public void gerarTodasFolhasPagamento() {
        try {
            calcularFolha.gerarFolha(todasFolhasPeriodo, fpPeriodo.getPerMes() + " - " + fpPeriodo.getPerAno());
        } catch (Exception e) {
            Helper.mostrarNotificacao("Relatório", "Ocorreu um erro ao gerar as folhas de pagamento.", "error");
        }
    }

    public void gerarFolhaPagamento() {
        if (fpFolhaPeriodo.getForId() <= 0) {
            Helper.mostrarNotificacao("Mensagem", "É preciso ter a folha carregada para imprimir.", "error");
            return;
        }
        imprimirFolhaPagamento(fpFolhaPeriodo);

    }

    private void imprimirFolhaPagamento(FpFolhaPeriodo fpFolhaPeriodo) {
        try {
            List<FpFolhaPeriodo> folhasPeriodo = new ArrayList<>();
            folhasPeriodo.add(fpFolhaPeriodo);
            calcularFolha.gerarFolha(folhasPeriodo, fpFolhaPeriodo.getForPessoa().getRecNomecompleto());
        } catch (Exception e) {
            Helper.mostrarNotificacao("Relatório", "Ocorreu um erro ao gerar a folha de pagamento.", "error");
        }
    }

    public boolean isMostrarTodasFolhasPeriodo() {
        return mostrarTodasFolhasPeriodo;
    }

    public void setMostrarTodasFolhasPeriodo(boolean mostrarTodasFolhasPeriodo) {
        this.mostrarTodasFolhasPeriodo = mostrarTodasFolhasPeriodo;
    }

    public FpPeriodo getFpPeriodo() {
        return fpPeriodo;
    }

    public void setFpPeriodo(FpPeriodo fpPeriodo) {
        this.fpPeriodo = fpPeriodo;
    }

    public List<FpPeriodo> getTodosFpPeriodo() {
        if (todosFpPeriodo == null) {
            todosFpPeriodo = fpPeriodoService.findAll().stream()
                    .sorted((x, y) -> Integer.compare(x.getPerMes(), y.getPerMes()))
                    .collect(Collectors.toList());
        }
        return todosFpPeriodo;

    }

    public void setTodosFpPeriodo(List<FpPeriodo> todosFpPeriodo) {
        this.todosFpPeriodo = todosFpPeriodo;
    }

    public RecPessoa getRecPessoa() {
        return recPessoa;
    }

    public void setRecPessoa(RecPessoa recPessoa) {
        this.recPessoa = recPessoa;
    }

    public FpFolhaPeriodo getFpFolhaPeriodo() {
        return fpFolhaPeriodo;
    }

    public void setFpFolhaPeriodo(FpFolhaPeriodo fpFolhaPeriodo) {
        this.fpFolhaPeriodo = fpFolhaPeriodo;
    }

    public FpTipoFolha getFpTipoFolha() {
        return fpTipoFolha;
    }

    public void setFpTipoFolha(FpTipoFolha fpTipoFolha) {
        this.fpTipoFolha = fpTipoFolha;
    }

    public List<FpFolhaPeriodo> getTodasFolhasPeriodo() {
        return todasFolhasPeriodo;
    }

    public void setTodasFolhasPeriodo(List<FpFolhaPeriodo> todasFolhasPeriodo) {
        this.todasFolhasPeriodo = todasFolhasPeriodo;
    }

    public FpPeriodoService getFpPeriodoService() {
        return fpPeriodoService;
    }

    public void setFpPeriodoService(FpPeriodoService fpPeriodoService) {
        this.fpPeriodoService = fpPeriodoService;
    }

    public CalcularFolha getCalcularFolha() {
        return calcularFolha;
    }

    public void setCalcularFolha(CalcularFolha calcularFolha) {
        this.calcularFolha = calcularFolha;
    }

    public RecPessoaService getRecPessoaService() {
        return recPessoaService;
    }

    public void setRecPessoaService(RecPessoaService recPessoaService) {
        this.recPessoaService = recPessoaService;
    }

    public FpEventoService getFpEventoService() {
        return fpEventoService;
    }

    public void setFpEventoService(FpEventoService fpEventoService) {
        this.fpEventoService = fpEventoService;
    }

    public FpFolhaPeriodoService getFpFolhaPeriodoService() {
        return fpFolhaPeriodoService;
    }

    public void setFpFolhaPeriodoService(FpFolhaPeriodoService fpFolhaPeriodoService) {
        this.fpFolhaPeriodoService = fpFolhaPeriodoService;
    }

}
