package app;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.*;

public class Server {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        String staticFilesDir = "src/main/resources/web";
        
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.println("Servidor rodando em http://localhost:" + port);

        server.createContext("/", new StaticFileHandler(staticFilesDir));
        server.setExecutor(null);
        server.start();
    }

    static class StaticFileHandler implements HttpHandler {
        private final String baseDir;
        private final File baseDirFile;

        StaticFileHandler(String baseDir) throws IOException {
            this.baseDir = baseDir;
            this.baseDirFile = new File(baseDir).getCanonicalFile();
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String path = exchange.getRequestURI().getPath();
                path = path.equals("/") ? "/index.html" : path;

                File file = new File(baseDir + path).getCanonicalFile();

                // Verificação de segurança
                if (!file.getPath().startsWith(baseDirFile.getPath())) {
                    sendError(exchange, 403, "Acesso negado");
                    return;
                }

                if (!file.exists() || file.isDirectory()) {
                    sendError(exchange, 404, "Arquivo não encontrado");
                    return;
                }

                byte[] content = Files.readAllBytes(file.toPath());
                String mime = Files.probeContentType(file.toPath());
                
                exchange.getResponseHeaders().set("Content-Type", mime != null ? mime : "application/octet-stream");
                exchange.sendResponseHeaders(200, content.length);
                exchange.getResponseBody().write(content);
            } catch (Exception e) {
                sendError(exchange, 500, "Erro interno: " + e.getMessage());
            } finally {
                exchange.close();
            }
        }

        private void sendError(HttpExchange exchange, int code, String message) throws IOException {
            byte[] response = message.getBytes();
            exchange.sendResponseHeaders(code, response.length);
            exchange.getResponseBody().write(response);
        }
    }
}