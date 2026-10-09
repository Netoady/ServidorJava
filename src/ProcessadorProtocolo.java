import java.util.concurrent.BlockingQueue;

public class ProcessadorProtocolo {
    private final BlockingQueue<String> filaRequisicoes;

    public ProcessadorProtocolo(BlockingQueue<String> filaRequisicoes) {
        this.filaRequisicoes = filaRequisicoes;
    }

    public String processarMensagem(String mensagem) {
        if (mensagem == null || mensagem.trim().isEmpty()) {
            return "ERRO;Mensagem Vazia";
        }

        // Tenta registrar na fila do servidor
        try {
            filaRequisicoes.put(mensagem);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String[] partes = mensagem.split(";");
        String comando = partes[0].trim();

        switch (comando) {
            case "1": return calcular(partes, "+");
            case "2": return calcular(partes, "-");
            case "3": return calcular(partes, "*");
            case "4": return calcular(partes, "/");
            case "6": 
                return partes.length > 1 ? "Servidor recebeu: " + partes[1] : "Mensagem vazia";
            default: 
                return "COMANDO_DESCONHECIDO";
        }
    }

    private String calcular(String[] partes, String operacao) {
        if (partes.length < 3) return "ERRO;Parametros insuficientes";
        try {
            double n1 = Double.parseDouble(partes[1].trim());
            double n2 = Double.parseDouble(partes[2].trim());
            double resultado = 0;

            switch (operacao) {
                case "+": resultado = n1 + n2; break;
                case "-": resultado = n1 - n2; break;
                case "*": resultado = n1 * n2; break;
                case "/": resultado = n1 / n2; break;
            }
            return String.valueOf(resultado);
        } catch (NumberFormatException e) {
            return "ERRO;Formato numerico invalido";
        }
    }
}