import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class ServicoImagem {
    private static final String CAMINHO_1 = "src/imagens/morpheus.jpg";
    private static final String CAMINHO_2 = "imagens/morpheus.jpg";

    public String obterImagemBase64() throws IOException {
        Path caminho = Paths.get(CAMINHO_1);

        // Tenta buscar em src/imagens/morpheus.jpg e, se não achar, busca na raiz
        if (!Files.exists(caminho)) {
            caminho = Paths.get(CAMINHO_2);
        }

        if (!Files.exists(caminho)) {
            throw new IOException("Arquivo não encontrado em nenhum dos caminhos previstos.");
        }

        byte[] bytesImagem = Files.readAllBytes(caminho);
        return Base64.getEncoder().encodeToString(bytesImagem);
    }
}