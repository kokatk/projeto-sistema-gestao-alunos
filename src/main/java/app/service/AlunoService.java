package app.service;

import app.model.Aluno;
import app.repository.AlunoRepository;
import java.util.List;

public class AlunoService {
    private final AlunoRepository repository;

    public AlunoService() {
        this.repository = new AlunoRepository();
    }

    public List<Aluno> listarTodos() {
        return repository.listarTodos();
    }

    public Aluno buscarPorId(int id) {
        return repository.buscarPorId(id);
    }

    public void salvar(Aluno aluno) {
        repository.salvar(aluno);
    }

    public boolean remover(int id) {
        return repository.remover(id);
    }
}