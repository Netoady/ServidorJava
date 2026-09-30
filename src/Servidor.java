import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Servidor {

    private static final BlockingQueue<String> FILA_REQUISICOES = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws IOException{

        ServerSocket servidor = new ServerSocket(12345);
        System.out.println("comecou. aguardando...");


        while(true){
            Socket conexao = servidor.accept();
            System.out.println ("Novo cliente conectado" + conexao.getInetAddress().getHostAddress());

            //Cria e dispara uma nova thread independente para atender o cliente recem-chegado
            TratadorCliente tratador = new TratadorCliente (conexao, FILA_REQUISICOES);
            Thread threadCliente = new Thread(tratador);
            threadCliente.start();
        }
    }
}
