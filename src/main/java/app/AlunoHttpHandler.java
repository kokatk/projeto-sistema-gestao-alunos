package app;

// Importações necessárias
import app.controller.AlunoController;  // Controller para operações com alunos
import app.model.Aluno;                 // Modelo/entidade Aluno
import app.service.AlunoService;        // Serviço de negócios para alunos
import com.sun.net.httpserver.HttpExchange;  // Representa uma troca HTTP (request/response)
import com.sun.net.httpserver.HttpHandler;   // Interface para lidar com requisições HTTP
import java.io.IOException;             // Para tratamento de erros de I/O
import java.io.InputStream;             // Para ler dados de entrada
import java.io.OutputStream;            // Para escrever dados de saída
import java.nio.charset.StandardCharsets; // Para codificação de caracteres
import java.util.List;                  // Para trabalhar com listas

/**
 * Handler HTTP para manipular requisições relacionadas a alunos.
 * Implementa as operações CRUD (Create, Read, Update, Delete) via API REST.
 */
public class AlunoHttpHandler implements HttpHandler {
    
    // Controller que gerencia as operações com alunos
    private final AlunoController controller;
    
    /**
     * Construtor que inicializa o controller com suas dependências.
     */
    public AlunoHttpHandler() {
        // Inicializa o controller com uma instância de AlunoService
        this.controller = new AlunoController(new AlunoService());
    }

    /**
     * Método principal que processa cada requisição HTTP.
     * @param exchange Objeto que contém os dados da requisição e resposta
     * @throws IOException Se ocorrer erro durante o processamento
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String response = "";       // Resposta a ser enviada
            int statusCode = 200;       // Código de status HTTP padrão (OK)
            String method = exchange.getRequestMethod(); // Método HTTP (GET, POST, etc.)
            String path = exchange.getRequestURI().getPath(); // Caminho da URL

            // Configura o cabeçalho para indicar que a resposta será em JSON
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

            // Verifica o método HTTP e o caminho para determinar a ação
            if ("GET".equals(method)) {
                if (path.equals("/alunos")) {
                    // GET /alunos - Lista todos os alunos
                    response = listarAlunos();
                } else if (path.matches("/alunos/\\d+")) {
                    // GET /alunos/{id} - Busca um aluno específico
                    int id = extrairIdDaUrl(path);
                    response = buscarAlunoPorId(id);
                    if (response.equals("null")) {
                        statusCode = 404; // Not Found
                        response = "{\"erro\":\"Aluno não encontrado\"}";
                    }
                }
            } 
            else if ("POST".equals(method) && path.equals("/alunos")) {
                // POST /alunos - Adiciona um novo aluno
                response = adicionarAluno(exchange.getRequestBody());
                statusCode = 201; // Created
            }
            else if ("DELETE".equals(method) && path.matches("/alunos/\\d+")) {
                // DELETE /alunos/{id} - Remove um aluno
                int id = extrairIdDaUrl(path);
                if (removerAluno(id)) {
                    response = "{\"mensagem\":\"Aluno removido com sucesso\"}";
                } else {
                    response = "{\"erro\":\"Aluno não encontrado\"}";
                    statusCode = 404; // Not Found
                }
            }
            else {
                // Método não suportado
                response = "{\"erro\":\"Método não suportado\"}";
                statusCode = 405; // Method Not Allowed
            }

            // Converte a resposta para bytes e envia
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(statusCode, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
            
        } catch (Exception e) {
            // Tratamento de erros genéricos
            e.printStackTrace();
            String errorResponse = "{\"erro\":\"" + e.getMessage() + "\"}";
            byte[] errorBytes = errorResponse.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(500, errorBytes.length); // Internal Server Error
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(errorBytes);
            }
        }
    }

    /**
     * Lista todos os alunos em formato JSON.
     * @return String JSON contendo array de alunos
     */
    private String listarAlunos() {
        StringBuilder sb = new StringBuilder("[");
        List<Aluno> alunos = controller.listarAlunos();
        
        // Converte cada aluno para JSON e adiciona ao array
        for (Aluno aluno : alunos) {
            sb.append(String.format(
                "{\"id\":%d,\"nome\":\"%s\",\"idade\":%d,\"email\":\"%s\",\"curso\":\"%s\"},",
                aluno.getId(), aluno.getNome(), aluno.getIdade(), aluno.getEmail(), aluno.getCurso()
            ));
        }
        
        // Remove a vírgula extra do último elemento
        if (alunos.size() > 0) {
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append("]");
        
        return sb.toString();
    }

    /**
     * Busca um aluno por ID e retorna em formato JSON.
     * @param id ID do aluno a ser buscado
     * @return String JSON com dados do aluno ou "null" se não encontrado
     */
    private String buscarAlunoPorId(int id) {
        Aluno aluno = controller.buscarAlunoPorId(id);
        if (aluno == null) return "null";
        
        return String.format(
            "{\"id\":%d,\"nome\":\"%s\",\"idade\":%d,\"email\":\"%s\",\"curso\":\"%s\"}",
            aluno.getId(), aluno.getNome(), aluno.getIdade(), aluno.getEmail(), aluno.getCurso()
        );
    }

    /**
     * Adiciona um novo aluno a partir dos dados do corpo da requisição.
     * @param requestBody Stream com os dados do aluno em JSON
     * @return String JSON com os dados do aluno criado
     * @throws IOException Se ocorrer erro ao ler o corpo da requisição
     */
    private String adicionarAluno(InputStream requestBody) throws IOException {
        // Lê todo o corpo da requisição
        String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
        
        // Extrai os valores do JSON manualmente (simplificado)
        String nome = extrairValorJson(body, "nome");
        int idade = Integer.parseInt(extrairValorJson(body, "idade"));
        String email = extrairValorJson(body, "email");
        String curso = extrairValorJson(body, "curso");
        
        // Cria e salva o novo aluno
        Aluno aluno = new Aluno(nome, idade, email, curso);
        controller.adicionarAluno(aluno);
        
        // Retorna os dados do aluno criado
        return String.format(
            "{\"id\":%d,\"nome\":\"%s\",\"idade\":%d,\"email\":\"%s\",\"curso\":\"%s\"}",
            aluno.getId(), aluno.getNome(), aluno.getIdade(), aluno.getEmail(), aluno.getCurso()
        );
    }

    /**
     * Remove um aluno pelo ID.
     * @param id ID do aluno a ser removido
     * @return true se o aluno foi removido, false se não encontrado
     */
    private boolean removerAluno(int id) {
        return controller.removerAluno(id);
    }

    /**
     * Extrai o ID da URL no formato /alunos/{id}.
     * @param path Caminho da URL
     * @return ID extraído
     */
    private int extrairIdDaUrl(String path) {
        String[] parts = path.split("/");
        return Integer.parseInt(parts[2]);
    }

    /**
     * Extrai o valor de uma propriedade de um JSON simples.
     * @param json String contendo o JSON
     * @param key Nome da propriedade a ser extraída
     * @return Valor da propriedade
     */
    private String extrairValorJson(String json, String key) {
        String keyPattern = "\"" + key + "\":\"";
        int startIndex = json.indexOf(keyPattern) + keyPattern.length();
        int endIndex = json.indexOf("\"", startIndex);
        return json.substring(startIndex, endIndex);
    }
}