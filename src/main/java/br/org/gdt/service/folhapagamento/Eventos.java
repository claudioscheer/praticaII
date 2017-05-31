package br.org.gdt.service.folhapagamento;

import br.org.gdt.enums.FpEnumEventos;
import br.org.gdt.enums.FpEnumTabelas;
import br.org.gdt.enums.FpTipoEvento;
import br.org.gdt.enums.FpTipoValorFaixa;
import br.org.gdt.model.FpEventoPeriodo;
import br.org.gdt.model.FpFaixa;
import br.org.gdt.model.FpTabela;
import br.org.gdt.service.FpFolhaPeriodoService;
import br.org.gdt.service.FpPeriodoService;
import br.org.gdt.service.FpTabelaService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("eventos")
public class Eventos {

    private final int HORAS_MENSAIS = 220;

    @Autowired
    private FpTabelaService fpTabelaService;

    public FpEventoPeriodo calcularEvento(FpEventoPeriodo fpEventoPeriodo, DadosCalculadosDoFuncionario dadosCalculadosDoFuncionario) throws Exception {
        if (fpEventoPeriodo.isJaCalculado()) {
            return fpEventoPeriodo;
        }

        int evento = FpEnumEventos.values()[(int) fpEventoPeriodo.getEvpEvento().getEveId() - 1].ordinal();
        if (evento == FpEnumEventos.Salario.ordinal()) {
            fpEventoPeriodo.setEvpValorReferencia(HORAS_MENSAIS);
            fpEventoPeriodo.setEvpValor(1000);

        } else if (evento == FpEnumEventos.INSS.ordinal()) {
            // Proventos onde incide INSS.
            double valorEventosIncideINSS = dadosCalculadosDoFuncionario.getEventos().stream()
                    .filter(x -> x.getEvpEvento().isEveIncideINSS() && x.getEvpEvento().getEveTipoEvento() == FpTipoEvento.Provento)
                    .mapToDouble(x -> x.getEvpValor())
                    .sum();

            // Descontos onde incide INSS.
            valorEventosIncideINSS -= dadosCalculadosDoFuncionario.getEventos().stream()
                    .filter(x -> x.getEvpEvento().isEveIncideINSS() && x.getEvpEvento().getEveTipoEvento() == FpTipoEvento.Desconto)
                    .map((x) -> {
                        try {
                            return verificarEventoJaEstaCalculado(x, dadosCalculadosDoFuncionario);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }).
                    mapToDouble(x -> x.getEvpValor())
                    .sum();

            FpFaixa fpFaixa = fpTabelaService.encontrarFaixaDaTabela(valorEventosIncideINSS, FpEnumTabelas.INSS.ordinal() + 1);
            fpEventoPeriodo.setEvpValor(fpFaixa.getFaiTipoValor() == FpTipoValorFaixa.Decimal
                    ? fpFaixa.getFaiValor()
                    : valorEventosIncideINSS * (fpFaixa.getFaiValor() / 100));
            fpEventoPeriodo.setEvpValorReferencia(fpFaixa.getFaiValor());

        } else if (evento == FpEnumEventos.FGTS.ordinal()) {
            // Proventos onde incide FGTS.
            double valorEventosIncideFGTS = dadosCalculadosDoFuncionario.getEventos().stream()
                    .filter(x -> x.getEvpEvento().isEveIncideFGTS() && x.getEvpEvento().getEveTipoEvento() == FpTipoEvento.Provento)
                    .mapToDouble(x -> x.getEvpValor())
                    .sum();

            // Descontos onde incide FGTS.
            valorEventosIncideFGTS -= dadosCalculadosDoFuncionario.getEventos().stream()
                    .filter(x -> x.getEvpEvento().isEveIncideFGTS() && x.getEvpEvento().getEveTipoEvento() == FpTipoEvento.Desconto)
                    .map((x) -> {
                        try {
                            return verificarEventoJaEstaCalculado(x, dadosCalculadosDoFuncionario);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }).
                    mapToDouble(x -> x.getEvpValor())
                    .sum();

            FpFaixa fpFaixa = fpTabelaService.encontrarFaixaDaTabela(valorEventosIncideFGTS, FpEnumTabelas.FGTS.ordinal() + 1);
            fpEventoPeriodo.setEvpValor(fpFaixa.getFaiTipoValor() == FpTipoValorFaixa.Decimal
                    ? fpFaixa.getFaiValor()
                    : valorEventosIncideFGTS * (fpFaixa.getFaiValor() / 100));
            fpEventoPeriodo.setEvpValorReferencia(fpFaixa.getFaiValor());

        } else if (evento == FpEnumEventos.IRRF.ordinal()) {
            // Proventos onde incide IRRF.
            double valorEventosIncideIRRF = dadosCalculadosDoFuncionario.getEventos().stream()
                    .filter(x -> x.getEvpEvento().isEveIncideIRRF() && x.getEvpEvento().getEveTipoEvento() == FpTipoEvento.Provento)
                    .mapToDouble(x -> x.getEvpValor())
                    .sum();

            // Descontar o valor do evento INSS.
            valorEventosIncideIRRF -= getValorEventoDosEventosDoFuncionario(FpEnumEventos.INSS, dadosCalculadosDoFuncionario);

            // Quantidade de dependentes que o funcionário tem.
            int dependentes = 1;
            if (dependentes > 0) {
                // Se o funcionário tem dependentes desconta R$ 189 por dependentes.
                FpFaixa fpFaixaValorPorDependente = fpTabelaService.encontrarFaixaDaTabela(0, FpEnumTabelas.ValorIRRFPorDependente.ordinal() + 1);
                valorEventosIncideIRRF -= fpFaixaValorPorDependente.getFaiValor() * dependentes;
            }

            // Joga para a tabela de IRRF.
            FpFaixa fpFaixa = fpTabelaService.encontrarFaixaDaTabela(valorEventosIncideIRRF, FpEnumTabelas.IRRF.ordinal() + 1);
            double valor = fpFaixa.getFaiTipoValor() == FpTipoValorFaixa.Decimal
                    ? fpFaixa.getFaiValor()
                    : valorEventosIncideIRRF * (fpFaixa.getFaiValor() / 100);

            // Desconta o valor de dedução da faixa.
            valor -= fpFaixa.getFaiValorDeducao();

            fpEventoPeriodo.setEvpValor(valor);
            fpEventoPeriodo.setEvpValorReferencia(fpFaixa.getFaiValor());

        } else if (evento == FpEnumEventos.HorasExtras50.ordinal()) {
            double valorHoraFuncionario = getValorHoraFuncionario(dadosCalculadosDoFuncionario);
            fpEventoPeriodo.setEvpValor(valorHoraFuncionario * fpEventoPeriodo.getEvpValorReferencia() * 1.5);

        } else if (evento == FpEnumEventos.HorasExtras100.ordinal()) {
            double valorHoraFuncionario = getValorHoraFuncionario(dadosCalculadosDoFuncionario);
            fpEventoPeriodo.setEvpValor(valorHoraFuncionario * fpEventoPeriodo.getEvpValorReferencia() * 2);

        } else if (evento == FpEnumEventos.HorasFaltas.ordinal()) {
            double valorHoraFuncionario = getValorHoraFuncionario(dadosCalculadosDoFuncionario);
            fpEventoPeriodo.setEvpValor(valorHoraFuncionario * fpEventoPeriodo.getEvpValorReferencia());

        } else if (evento == FpEnumEventos.HorasNoturnas.ordinal()) {
            // Trabalhadores rurais no mínimo 25%? Fonte: http://www.mcalculos.com.br/noticias/ler/32/fonte-guia-trabalhista.html.
            double valorHoraFuncionario = getValorHoraFuncionario(dadosCalculadosDoFuncionario);
            fpEventoPeriodo.setEvpValor(valorHoraFuncionario * fpEventoPeriodo.getEvpValorReferencia() * 1.2);

        } else if (evento == FpEnumEventos.DSR.ordinal()) {
            double valorHorasExtras50 = getValorEventoDosEventosDoFuncionario(FpEnumEventos.HorasExtras50, dadosCalculadosDoFuncionario);
            double valorHorasExtras100 = getValorEventoDosEventosDoFuncionario(FpEnumEventos.HorasExtras100, dadosCalculadosDoFuncionario);
            double valorHorasNoturnas = getValorEventoDosEventosDoFuncionario(FpEnumEventos.HorasNoturnas, dadosCalculadosDoFuncionario);

            double valorHorasExtras = valorHorasExtras50 + valorHorasExtras100 + valorHorasNoturnas;

            fpEventoPeriodo.setEvpValor((valorHorasExtras / dadosCalculadosDoFuncionario.getPeriodo().getPerDiasUteis()) * dadosCalculadosDoFuncionario.getPeriodo().getPerDiasNaoUteis());

        } else if (evento == FpEnumEventos.SalarioFamilia.ordinal()) {
            double valorSalario = getValorEventoDosEventosDoFuncionario(FpEnumEventos.Salario, dadosCalculadosDoFuncionario);
            FpFaixa fpFaixa = fpTabelaService.encontrarFaixaDaTabela(valorSalario, FpEnumTabelas.SalarioFamilia.ordinal() + 1);
            // Buscar a quantidade de filhos abaixo de 14 anos.
            int quantidadeFilhos = 1;
            fpEventoPeriodo.setEvpValor(fpFaixa.getFaiValor() * quantidadeFilhos);
            fpEventoPeriodo.setEvpValorReferencia(quantidadeFilhos);

        } else if (evento == FpEnumEventos.Insalubridade.ordinal()) {
            FpFaixa fpFaixa = fpTabelaService.encontrarFaixaDaTabela(0, FpEnumTabelas.SalarioMinimo.ordinal() + 1);

            // Buscar o nível de insalubridade da pessoa.
            double nivelInsalubridade = 0.1;
            fpEventoPeriodo.setEvpValor(fpFaixa.getFaiValor() * nivelInsalubridade);
            fpEventoPeriodo.setEvpValorReferencia(nivelInsalubridade);

        } else if (evento == FpEnumEventos.Periculosidade.ordinal()) {
            double valorSalario = getValorEventoDosEventosDoFuncionario(FpEnumEventos.Salario, dadosCalculadosDoFuncionario);

            // Buscar se a pessoa tem periculosidade.
            double nivelPericulosidade = 0.3;
            fpEventoPeriodo.setEvpValor(valorSalario * nivelPericulosidade);
            fpEventoPeriodo.setEvpValorReferencia(nivelPericulosidade);

        }

        fpEventoPeriodo.setJaCalculado(true);
        return fpEventoPeriodo;
    }

    private double getValorEventoDosEventosDoFuncionario(FpEnumEventos evento, DadosCalculadosDoFuncionario dadosCalculadosDoFuncionario) throws Exception {
        FpEventoPeriodo fpEventoPeriodo = getEventoDosEventosDoFuncionario(evento, dadosCalculadosDoFuncionario);
        return fpEventoPeriodo == null ? 0 : fpEventoPeriodo.getEvpValor();
    }

    private double getValorHoraFuncionario(DadosCalculadosDoFuncionario dadosCalculadosDoFuncionario) throws Exception {
        double fpEventoPeriodoSalario = getValorEventoDosEventosDoFuncionario(FpEnumEventos.Salario, dadosCalculadosDoFuncionario);
        return fpEventoPeriodoSalario / HORAS_MENSAIS;
    }

    private FpEventoPeriodo getEventoDosEventosDoFuncionario(FpEnumEventos evento, DadosCalculadosDoFuncionario dadosCalculadosDoFuncionario) throws Exception {
        Optional<FpEventoPeriodo> optionalEventoPeriodo = dadosCalculadosDoFuncionario.getEventos().stream()
                .filter(x -> x.getEvpEvento().getEveId() == evento.ordinal() + 1)
                .findFirst();
        if (!optionalEventoPeriodo.isPresent()) {
            return null;
        }
        return verificarEventoJaEstaCalculado(optionalEventoPeriodo.get(), dadosCalculadosDoFuncionario);
    }

    private FpEventoPeriodo verificarEventoJaEstaCalculado(FpEventoPeriodo fpEventoPeriodo, DadosCalculadosDoFuncionario dadosCalculadosDoFuncionario) throws Exception {
        if (fpEventoPeriodo == null || fpEventoPeriodo.isJaCalculado()) {
            return fpEventoPeriodo;
        }
        return calcularEvento(fpEventoPeriodo, dadosCalculadosDoFuncionario);
    }

}
