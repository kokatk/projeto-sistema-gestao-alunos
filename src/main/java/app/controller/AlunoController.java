package app.controller;

import app.model.Aluno;
import app.service.AlunoService;
import java.util.List;
import java.util.Scanner;

/**
 * Controlador responsável por gerenciar a interação entre a interface do usuário
 * e a camada de serviço de alunos.
 * 
 * Oferece duas interfaces:
 * 1. Métodos para API HTTP (caso precise integrar com uma aplicação web)
 * 2. Interface console interativa para uso via terminal
 */
public class AlunoController {
    // Dependências do controlador
    private final AlunoService service;  // Camada de serviço para operações de negócio
    private final Scanner scanner;      // Para leitura de inputs do usuário

    /**
     * Construtor que recebe as dependências necessárias
     * @param service Serviço de alunos injetado
     */
    public AlunoController(AlunoService service) {
        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    // ========== MÉTODOS PÚBLICOS PRINCIPAIS ==========
    
    /**
     * Inicia o loop principal da interface console
     */
    public void iniciar() {
        int opcao;
        do {
            exibirMenu();
            opcao = lerOpcao();
            processarOpcao(opcao);
        } while (opcao != 0);
    }

    // ========== MÉTODOS PARA API HTTP (caso necessário) ==========
    
    public List<Aluno> listarAlunos() {
        return service.listarTodos();
    }

    public Aluno buscarAlunoPorId(int id) {
        return service.buscarPorId(id);
    }

    public void adicionarAluno(Aluno aluno) {
        service.salvar(aluno);
    }

    public boolean removerAluno(int id) {
        return service.remover(id);
    }

    // ========== MÉTODOS PRIVADOS PARA INTERFACE CONSOLE ==========
    
    /**
     * Exibe o menu de opções no console
     */
    private void exibirMenu() {
        System.out.println("\n=== SISTEMA DE GESTÃO DE ALUNOS ===");
        System.out.println("1. Adicionar aluno");
        System.out.println("2. Listar alunos");
        System.out.println("3. Buscar aluno por ID");
        System.out.println("4. Remover aluno");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    /**
     * Lê a opção digitada pelo usuário
     * @return Número da opção ou -1 se inválido
     */
    private int lerOpcao() {
        try {
            return scanner.nextInt();
        } catch (Exception e) {
            System.out.println("Erro: Digite um número válido!");
            scanner.nextLine(); // Limpa o buffer
            return -1;
        } finally {
            scanner.nextLine(); // Sempre limpa o buffer após leitura
        }
    }

    /**
     * Processa a opção selecionada pelo usuário
     * @param opcao Número da opção selecionada
     */
    private void processarOpcao(int opcao) {
        switch (opcao) {
            case 1 -> adicionarAlunoConsole();
            case 2 -> listarAlunosConsole();
            case 3 -> buscarAlunoPorIdConsole();
            case 4 -> removerAlunoConsole();
            case 0 -> System.out.println("Encerrando o sistema...");
            default -> System.out.println("Opção inválida! Tente novamente.");
        }
    }

    // ========== OPERAÇÕES ESPECÍFICAS DA INTERFACE CONSOLE ==========
    
    private void adicionarAlunoConsole() {
        try {
            System.out.println("\n--- NOVO ALUNO ---");
            
            // Coleta dados do usuário
            System.out.print("Nome: ");
            String nome = scanner.nextLine();

            System.out.print("Idade: ");
            int idade = scanner.nextInt();
            scanner.nextLine(); // Limpa o buffer

            System.out.print("Email: ");
            String email = scanner.nextLine();

            System.out.print("Curso: ");
            String curso = scanner.nextLine();

            // Cria e salva o aluno
            Aluno aluno = new Aluno(nome, idade, email, curso);
            service.salvar(aluno);
            System.out.println("\n✅ Aluno cadastrado com sucesso! ID: " + aluno.getId());
        } catch (Exception e) {
            System.out.println("❌ Erro ao cadastrar aluno: " + e.getMessage());
        }
    }

    private void listarAlunosConsole() {
        System.out.println("\n--- LISTA DE ALUNOS ---");
        List<Aluno> alunos = service.listarTodos();
        if (alunos.isEmpty()) {
            System.out.println("Nenhum aluno cadastrado.");
            return;
        }
        alunos.forEach(System.out::println); // Imprime cada aluno usando toString()
    }

    private void buscarAlunoPorIdConsole() {
        try {
            System.out.println("\n--- BUSCAR ALUNO ---");
            System.out.print("Digite o ID do aluno: ");
            int id = scanner.nextInt();
            scanner.nextLine(); // Limpa o buffer

            Aluno aluno = service.buscarPorId(id);
            if (aluno != null) {
                System.out.println("\nAluno encontrado:");
                System.out.println(aluno);
            } else {
                System.out.println("Aluno não encontrado com o ID: " + id);
            }
        } catch (Exception e) {
            System.out.println("Erro: ID deve ser um número válido!");
        }
    }

    private void removerAlunoConsole() {
        try {
            System.out.println("\n--- REMOVER ALUNO ---");
            System.out.print("Digite o ID do aluno: ");
            int id = scanner.nextInt();
            scanner.nextLine(); // Limpa o buffer

            if (service.remover(id)) {
                System.out.println("✅ Aluno removido com sucesso!");
            } else {
                System.out.println("❌ Aluno não encontrado com o ID: " + id);
            }
        } catch (Exception e) {
            System.out.println("Erro: ID deve ser um número válido!");
        }
    }
}