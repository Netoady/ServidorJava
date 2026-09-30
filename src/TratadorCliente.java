import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class TratadorCliente implements Runnable {
    private final Socket conexao;
    private final BlockingQueue<String> filaRequisicoes;

    public TratadorCliente (Socket conexao, BlockingQueue<String> filaRequisicoes) {
        this.conexao = conexao;
        this.filaRequisicoes = filaRequisicoes;
    }

    @Override 
    public void run() {
        try {
            Scanner LE_DO_SOCKET = new Scanner(conexao.getInputStream());
            PrintStream ESCREVE_NO_SOCKET = new PrintStream(conexao.getOutputStream());

            ESCREVE_NO_SOCKET.println("BEM-VINDO!");

            //loop de leitura exclusivo deste cliente
            while(LE_DO_SOCKET.hasNextLine()) {
                String mensagem = LE_DO_SOCKET.nextLine();

                //comando para encerrar a conexao limpa
                if ("SAIR".equalsIgnoreCase(mensagem.trim())) {
                    ESCREVE_NO_SOCKET.println("CONEXAO ENCERRADA!");
                    break;
                }

                // Adiciona a mensaem recebida na fila compartilhada
                filaRequisicoes.put(mensagem);
                System.out.println("[" + conexao.getInetAddress().getHostAddress() + "na fila]: " + mensagem);

                //Responde ao cliente
                ESCREVE_NO_SOCKET.println("Resposta da mensagem" + mensagem + " = BLZ!!");
            }

            LE_DO_SOCKET.close();
            ESCREVE_NO_SOCKET.close();
            conexao.close();
            System.out.println("Cliente desconectado: " + conexao.getInetAddress().getHostAddress());

        } catch (IOException | InterruptedException e) {
            System.out.println("Erro no atendimento ao cliente" + e.getMessage());
        }

    }
    
}
