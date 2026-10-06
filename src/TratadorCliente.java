import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class TratadorCliente implements Runnable {
    private final Socket conexao;
    private final ProcessadorProtocolo processador;

    public TratadorCliente(Socket conexao, BlockingQueue<String> filaRequisicoes) {
        this.conexao = conexao;
        this.processador = new ProcessadorProtocolo(filaRequisicoes);
    }

    @Override
    public void run() {
        String ipCliente = conexao.getInetAddress().getHostAddress();
        System.out.println("Iniciando atendimento para: " + ipCliente);

        try (
            Scanner leDoSocket = new Scanner(conexao.getInputStream());
            PrintStream escreveNoSocket = new PrintStream(conexao.getOutputStream())
        ) {
            while (leDoSocket.hasNextLine()) {
                String mensagem = leDoSocket.nextLine();

                // Trata encerramento do cliente (Opção "0" do menu ou palavra "SAIR")
                if ("0".equals(mensagem.trim()) || "SAIR".equalsIgnoreCase(mensagem.trim())) {
                    System.out.println("Cliente " + ipCliente + " solicitou desconexao.");
                    break;
                }

                // Trata o protocolo da Imagem Base64 (Opção 4)
                if ("4".equals(mensagem.trim())) {
                    System.out.println("Cliente " + ipCliente + " enviando imagem Base64...");
                    if (leDoSocket.hasNextLine()) {
                        String base64Recebido = leDoSocket.nextLine();
                        System.out.println("Imagem Base64 recebida (" + base64Recebido.length() + " caracteres). Devolvendo ao cliente...");
                        
                        // Responde devolvendo a própria imagem em Base64 (esperado pelo cliente)
                        escreveNoSocket.println(base64Recebido);
                    }
                    continue;
                }

                // Processa operações matematicas e mensagens
                String resposta = processador.processarMensagem(mensagem);
                System.out.println("[" + ipCliente + "]: " + mensagem + " -> Resposta: " + resposta);
                
                // Envia a resposta de volta ao Socket do cliente
                escreveNoSocket.println(resposta);
            }

        } catch (IOException e) {
            System.err.println("Erro de E/S no cliente " + ipCliente + ": " + e.getMessage());
        } finally {
            try {
                conexao.close();
                System.out.println("Conexao encerrada com o cliente: " + ipCliente);
            } catch (IOException e) {
                System.err.println("Erro ao fechar conexao: " + e.getMessage());
            }
        }
    }
}