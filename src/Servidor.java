import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Servidor {
    private static final int PORTA = 12345; // **
    private static final BlockingQueue<String> FILA_REQUISICOES = new ArrayBlockingQueue<>(100); // **

    public static void main(String[] args) {
        System.out.println("=== Servidor Multithread Java Iniciado ===");

        try (ServerSocket servidor = new ServerSocket(PORTA)) { // **
            System.out.println("Aguardando conexoes na porta " + PORTA + "...");

            while (true) { // **
                Socket conexao = servidor.accept(); // **
                System.out.println("Novo cliente conectado: " + conexao.getInetAddress().getHostAddress());

                TratadorCliente tratador = new TratadorCliente(conexao, FILA_REQUISICOES); // **
                Thread threadCliente = new Thread(tratador);
                threadCliente.start();
            }
        } catch (IOException e) {
            System.err.println("Erro fatal no ServerSocket: " + e.getMessage());
        }
    }
}
