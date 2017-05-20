package br.org.gdt.dao;

import br.org.gdt.model.RecVaga;
import java.util.List;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

@Repository("recVagaDAO")
public class RecVagaDAO extends DAO<RecVaga> {

    public RecVagaDAO() {
        classe = RecVaga.class;
    }

    public List<RecVaga> PesquisarPorDescricao(String descricao) {
        Query query = entityManager.createQuery("from RecVaga where lower(recDescricao) like lower(:busca)");
        query.setParameter("busca", descricao+ "%");
        return query.getResultList();
    }
}  