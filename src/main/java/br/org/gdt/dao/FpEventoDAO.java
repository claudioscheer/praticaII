package br.org.gdt.dao;

import br.org.gdt.model.FpEvento;
import org.springframework.stereotype.Repository;

@Repository("fpEventoDAO")
public class FpEventoDAO extends DAO<FpEvento> {

    public FpEventoDAO() {
        classe = FpEvento.class;
    }

}
