package app.repository;

import app.model.Aluno;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Classe responsável por armazenar e gerenciar os dados dos alunos em memória.
 * Implementa operações básicas de CRUD (Create, Read, Update, Delete).
 */
public class AlunoRepository {
    // Lista para armazenar os alunos (simula um banco de dados em memória)
    private final List<Aluno> alunos = new ArrayList<>();
    
    // Contador atômico para gerar IDs únicos para novos alunos
    // AtomicInteger é thread-safe (seguro para uso em ambientes concorrentes)
    private final AtomicInteger proximoId = new AtomicInteger(1);

    /**
     * Retorna uma lista com todos os alunos cadastrados.
     * @return Cópia da lista de alunos (para evitar modificações externas na lista interna)
     */
    public List<Aluno> listarTodos() {
        return new ArrayList<>(alunos); // Retorna uma cópia para proteger a lista original
    }

    /**
     * Busca um aluno pelo ID.
     * @param id ID do aluno a ser buscado
     * @return O aluno encontrado ou null se não existir
     */
    public Aluno buscarPorId(int id) {
        return alunos.stream()
                   .filter(a -> a.getId() == id) // Filtra alunos com o ID especificado
                   .findFirst()                  // Pega o primeiro (deveria ser único)
                   .orElse(null);                // Retorna null se não encontrar
    }

    /**
     * Salva um aluno no repositório.
     * Se o aluno não tem ID (ID = 0), é considerado novo e será adicionado.
     * Se o aluno já tem ID, é considerado existente e será atualizado.
     * @param aluno Aluno a ser salvo ou atualizado
     */
    public void salvar(Aluno aluno) {
        if (aluno.getId() == 0) {
            // Aluno novo: atribui um ID e adiciona à lista
            aluno.setId(proximoId.getAndIncrement()); // Atribui ID e incrementa o contador
            alunos.add(aluno);
        } else {
            // Aluno existente: substitui na lista
            alunos.replaceAll(a -> a.getId() == aluno.getId() ? aluno : a);
        }
    }

    /**
     * Remove um aluno pelo ID.
     * @param id ID do aluno a ser removido
     * @return true se o aluno foi encontrado e removido, false caso contrário
     */
    public boolean remover(int id) {
        return alunos.removeIf(a -> a.getId() == id); // Remove se o ID corresponder
    }
}