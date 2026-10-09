import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;


public class TratadorCliente implements Runnable {
    private final Socket conexao;
    private final ProcessadorProtocolo processador;
    private final BlockingQueue<Socket> filaConexoes;

    public TratadorCliente(Socket conexao, BlockingQueue<String> filaRequisicoes, BlockingQueue<Socket> filaConexoes) {
        this.conexao = conexao; // Socket TCP do cliente
        this.processador = new ProcessadorProtocolo(filaRequisicoes);
        this.filaConexoes = filaConexoes;
    }

    @Override
    public void run() {
        String ipCliente = conexao.getInetAddress().getHostAddress();
        System.out.println("Iniciando atendimento para: " + ipCliente);

        try (
            Scanner leDoSocket = new Scanner(conexao.getInputStream()); // ** Lê os bytes vindo da rede
            PrintStream escreveNoSocket = new PrintStream(conexao.getOutputStream()) // ** Envia as respostas
        ) {
            while (leDoSocket.hasNextLine()) {
                String mensagem = leDoSocket.nextLine();

                // Trata encerramento do cliente
                if ("0".equals(mensagem.trim()) || "SAIR".equalsIgnoreCase(mensagem.trim())) {
                    System.out.println("Cliente " + ipCliente + " solicitou desconexao.");
                    break;
                }

                // Trata o protocolo da Imagem Base64 (Opção 5)
                if ("5".equals(mensagem.trim())) {
                    System.out.println("Cliente " + ipCliente + " solicitou a imagem Base64...");
                    ServicoImagem servicoImagem = new ServicoImagem();
                    String base64Imagem = servicoImagem.obterImagemBase64();
                    // Envia a imagem em Base64 para o cliente
                    escreveNoSocket.println(base64Imagem);
                    System.out.println("Imagem morpheus.jpg enviada com sucesso para " + ipCliente);
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
                filaConexoes.remove(conexao); // Libera a vaga para um novo cliente se conectar!
                conexao.close();
                System.out.println("Conexao encerrada com o cliente: " + ipCliente);
            } catch (IOException e) {
                System.err.println("Erro ao fechar conexao: " + e.getMessage());
            }
        }
    }
}