import java.util.concurrent.BlockingQueue;

public class ProcessadorProtocolo {
    private final BlockingQueue<String> filaRequisicoes;

    public ProcessadorProtocolo(BlockingQueue<String> filaRequisicoes) {
        this.filaRequisicoes = filaRequisicoes;
    }

    public String processarMensagem(String mensagem) {
        if (mensagem == null || mensagem.trim().isEmpty()) {
            return "Erro: Mensagem Vazia";
        }

        // Tenta registrar na BlockingQueue para auditoria/fila do servidor
        try {
            filaRequisicoes.put(mensagem);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String[] partes = mensagem.split(";");
        String comando = partes[0].trim();

        switch (comando) {
            case "1": // Somar (1;n1;n2)
                return calcular(partes, "+");
            case "2": // Subtrair (2;n1;n2)
                return calcular(partes, "-");
            case "3": // Multiplicar (3;n1;n2)
                return calcular(partes, "*");
            case "5": // Mensagem (5;conteudo)
                return partes.length > 1 ? "MENSAGEM_RECEBIDA: " + partes[1] : "MENSAGEM_VAZIA";
            default:
                return "COMANDO_DESCONHECIDO";
        }
    }

    private String calcular(String[] partes, String operacao) {
        if (partes.length < 3) return "ERRO;Parametros insuficientes";
        try {
            double n1 = Double.parseDouble(partes[1]);
            double n2 = Double.parseDouble(partes[2]);
            double resultado = 0;

            switch (operacao) {
                case "+": resultado = n1 + n2; break;
                case "-": resultado = n1 - n2; break;
                case "*": resultado = n1 * n2; break;
            }
            return String.valueOf(resultado);
        } catch (NumberFormatException e) {
            return "ERRO;Numero invalido";
        }
    }
}