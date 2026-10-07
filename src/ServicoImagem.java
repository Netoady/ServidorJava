import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class ServicoImagem {
    private static final String CAMINHO_IMAGEM = "imagens/morpheus.jpg";

    public String obterImagemBase64() throws IOException {
        Path caminho = Paths.get(CAMINHO_IMAGEM);
        if (!Files.exists(caminho)) {
            throw new IOException("Arquivo não encontrado: " + CAMINHO_IMAGEM);
        }
        byte[] bytesImagem = Files.readAllBytes(caminho);
        return Base64.getEncoder().encodeToString(bytesImagem);
    }
}