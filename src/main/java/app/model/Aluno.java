package app.model;

/**
 * Classe que representa a entidade Aluno no sistema.
 * Contém informações pessoais e acadêmicas do aluno.
 * 
 * Padrão: Modelo de Domínio (Domain Model)
 */
public class Aluno {
    // Atributos da classe
    private int id;                 // Identificador único do aluno
    private final String nome;      // Nome completo do aluno (imutável após criação)
    private final int idade;        // Idade do aluno (imutável após criação)
    private final String email;     // Email do aluno (imutável após criação)
    private final String curso;     // Curso que o aluno está matriculado (imutável após criação)

    /**
     * Construtor para criar uma nova instância de Aluno.
     * @param nome Nome completo do aluno
     * @param idade Idade do aluno
     * @param email Email do aluno
     * @param curso Curso do aluno
     */
    public Aluno(String nome, int idade, String email, String curso) {
        this.nome = nome;
        this.idade = idade;
        this.email = email;
        this.curso = curso;
        // O ID não é definido no construtor - será atribuído pelo repositório
    }

    // ========== GETTERS ==========
    /**
     * @return O ID único do aluno
     */
    public int getId() { return id; }

    /**
     * @return O nome completo do aluno
     */
    public String getNome() { return nome; }

    /**
     * @return A idade do aluno
     */
    public int getIdade() { return idade; }

    /**
     * @return O email do aluno
     */
    public String getEmail() { return email; }

    /**
     * @return O curso do aluno
     */
    public String getCurso() { return curso; }

    // ========== SETTER ==========
    /**
     * Define o ID do aluno.
     * Este é o único setter pois os outros atributos são final (imutáveis).
     * @param id Novo ID do aluno
     */
    public void setId(int id) { this.id = id; }

    // ========== MÉTODOS SOBRESCRITOS ==========
    /**
     * Retorna uma representação em String do objeto Aluno.
     * @return String formatada com todos os dados do aluno
     */
    @Override
    public String toString() {
        return String.format(
            "ID: %d | Nome: %s | Idade: %d | Email: %s | Curso: %s",
            id, nome, idade, email, curso
        );
    }
}