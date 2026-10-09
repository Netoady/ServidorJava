import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Servidor {
    private static final int PORTA = 12345; // Porta TCP definida
    private static final int MAX_ACESSOS = 3; 

    private static final BlockingQueue<String> FILA_REQUISICOES = new ArrayBlockingQueue<>(100); // Armazena e audita as mensagens de texto
    private static final BlockingQueue<Socket> FILA_CONEXOES = new ArrayBlockingQueue<>(MAX_ACESSOS); //Semáforo de capacidade do servidor

    public static void main(String[] args) {
        System.out.println("=== Servidor Multithread Java Iniciado ===");
        System.out.println("=== Limite de clientes: " + MAX_ACESSOS + "===");

        try (ServerSocket servidor = new ServerSocket(PORTA)) { // Inicio o servidor
            System.out.println("Aguardando conexoes na porta " + PORTA + "...");

            while (true) {
                Socket conexao = servidor.accept();

                // Tenta reservar uma vaga na fila de conexões ativas
                if (FILA_CONEXOES.offer(conexao)) {
                    System.out.println("Novo cliente conectado (" + FILA_CONEXOES.size() + "/" + MAX_ACESSOS + "): " 
                                       + conexao.getInetAddress().getHostAddress());

                    TratadorCliente tratador = new TratadorCliente(conexao, FILA_REQUISICOES, FILA_CONEXOES);
                    Thread threadCliente = new Thread(tratador);
                    threadCliente.start();
                } else {
                    // Se o limite de 3 clientes foi atingido
                    System.out.println("Servidor cheio! Recusando conexao de: " + conexao.getInetAddress().getHostAddress());
                    
                    PrintStream escreve = new PrintStream(conexao.getOutputStream());
                    escreve.println("ERRO;Servidor cheio. Tente novamente mais tarde.");
                    conexao.close();
                }
            }
        } catch (IOException e) {
            System.err.println("Erro fatal no ServerSocket: " + e.getMessage());
        }
    }
}
