// Define o pacote da aplicação
package app;

// Importações necessárias para o servidor HTTP e manipulação de arquivos
import com.sun.net.httpserver.HttpServer;       // Classe principal do servidor HTTP
import com.sun.net.httpserver.HttpHandler;      // Interface para lidar com requisições
import com.sun.net.httpserver.HttpExchange;     // Representa uma troca HTTP (request/response)
import java.net.InetSocketAddress;              // Para definir endereço e porta do servidor
import java.io.IOException;                     // Para tratamento de erros de I/O
import java.io.OutputStream;                    // Para escrever respostas HTTP
import java.io.File;                            // Para manipular arquivos do sistema
import java.nio.file.Files;                     // Para ler conteúdo de arquivos

/**
 * Classe principal que inicia um servidor web simples.
 * Ideal para aprender conceitos básicos de:
 * - Servidores HTTP
 * - Rotas/endpoints
 * - Manipulação de requisições/respostas
 * - Servir arquivos estáticos
 */
public class WebServer {
    
    /**
     * Método principal que inicia o servidor.
     * @param args Argumentos de linha de comando (não utilizados)
     * @throws IOException Se houver erro ao iniciar o servidor
     */
    public static void main(String[] args) throws IOException {
        // Cria um servidor HTTP na porta 8080
        // O segundo parâmetro (0) define o tamanho da fila de conexões pendentes
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        /**
         * Configura a rota principal ("/") que serve a página inicial.
         * Usa uma expressão lambda para implementar HttpHandler.
         */
        server.createContext("/", exchange -> {
            try {
                // Carrega o arquivo HTML da página inicial
                File file = new File("src/main/resources/web/index.html");
                
                // Lê todo o conteúdo do arquivo para memória
                byte[] bytes = Files.readAllBytes(file.toPath());
                
                // Define o cabeçalho Content-Type como HTML
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                
                // Envia os cabeçalhos HTTP com status 200 (OK) e tamanho do conteúdo
                exchange.sendResponseHeaders(200, bytes.length);
                
                // Obtém o stream de saída e escreve o conteúdo do arquivo
                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            } catch (IOException e) {
                // Log simples em caso de erro
                e.printStackTrace();
            }
        });
        
        /**
         * Configura rota para arquivos estáticos (CSS, JS, imagens)
         * Usa a classe StaticFileHandler para servir os arquivos
         */
        server.createContext("/static", new StaticFileHandler("src/main/resources/web"));
        
        // Define o executor de threads como null (usa o padrão)
        server.setExecutor(null);
        
        // Inicia o servidor
        server.start();
        
        // Mensagem indicando que o servidor está rodando
        System.out.println("Servidor rodando em http://localhost:8080");
    }
}

/**
 * Classe que manipula requisições para arquivos estáticos.
 * Implementa HttpHandler para processar requisições HTTP.
 */
class StaticFileHandler implements HttpHandler {
    
    // Diretório base onde os arquivos estáticos estão armazenados
    private final String baseDir;
    
    /**
     * Construtor que recebe o diretório base dos arquivos estáticos.
     * @param baseDir Caminho para a pasta com os arquivos estáticos
     */
    public StaticFileHandler(String baseDir) {
        this.baseDir = baseDir;
    }
    
    /**
     * Método principal que processa cada requisição HTTP.
     * @param exchange Objeto que representa a troca HTTP (request/response)
     * @throws IOException Se houver erro no processamento
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Obtém o caminho da requisição (ex: /static/css/style.css)
        String path = exchange.getRequestURI().getPath();
        
        // Remove o prefixo "/static/" para obter o caminho relativo do arquivo
        path = path.replace("/static/", "");
        
        try {
            // Cria um objeto File representando o arquivo solicitado
            // getCanonicalFile() resolve caminhos relativos e links simbólicos
            File file = new File(baseDir + File.separator + path).getCanonicalFile();
            
            /**
             * Verificação de segurança:
             * Impede acesso a arquivos fora do diretório base (Directory Traversal)
             */
            if (!file.getPath().startsWith(new File(baseDir).getCanonicalPath())) {
                sendError(exchange, 403, "Acesso negado");
                return;
            }
            
            // Verifica se o arquivo existe
            if (!file.exists()) {
                sendError(exchange, 404, "Arquivo não encontrado");
                return;
            }
            
            // Lê o conteúdo do arquivo para memória
            byte[] bytes = Files.readAllBytes(file.toPath());
            
            // Determina o tipo MIME baseado na extensão do arquivo
            String mimeType = "text/plain"; // Padrão
            if (path.endsWith(".css")) mimeType = "text/css";
            if (path.endsWith(".js")) mimeType = "application/javascript";
            if (path.endsWith(".png")) mimeType = "image/png";
            
            // Define o cabeçalho Content-Type apropriado
            exchange.getResponseHeaders().set("Content-Type", mimeType);
            
            // Envia os cabeçalhos HTTP com status 200 (OK)
            exchange.sendResponseHeaders(200, bytes.length);
            
            // Escreve o conteúdo do arquivo na resposta
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        } catch (Exception e) {
            // Em caso de erro genérico, retorna status 500
            sendError(exchange, 500, "Erro interno: " + e.getMessage());
        }
    }
    
    /**
     * Método auxiliar para enviar mensagens de erro.
     * @param exchange Objeto HTTP exchange
     * @param code Código de status HTTP (ex: 404, 500)
     * @param message Mensagem de erro
     * @throws IOException Se houver erro ao enviar a resposta
     */
    private void sendError(HttpExchange exchange, int code, String message) throws IOException {
        byte[] response = message.getBytes();
        exchange.sendResponseHeaders(code, response.length);
        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();
    }
}