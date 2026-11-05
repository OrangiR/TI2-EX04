package br.pucminas;

import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.azure.ai.textanalytics.TextAnalyticsClientBuilder;
import com.azure.ai.textanalytics.models.DocumentSentiment;
import com.azure.ai.textanalytics.models.SentenceSentiment;
import com.azure.ai.textanalytics.models.SentimentConfidenceScores;
import com.azure.core.credential.AzureKeyCredential;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import java.util.List;

/**
 * Exercício 4 - Produto 3 (Opção B: serviço cognitivo pronto).
 *
 * Consome o serviço de Análise de Sentimento do Azure AI Language
 * (antigo Cognitive Services / Text Analytics) hospedado na nuvem.
 *
 * A chave e o endpoint NÃO ficam no código: são lidos das variáveis de ambiente
 * AZURE_LANGUAGE_KEY e AZURE_LANGUAGE_ENDPOINT.
 *
 * Uso:
 *   sem argumentos  -> analisa um conjunto de frases de exemplo
 *   com argumentos  -> analisa cada argumento como um texto
 */
public class AnaliseSentimento {

    private static final String IDIOMA = "pt";

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        TextAnalyticsClient client = criarCliente();

        List<String> textos = args.length > 0
                ? List.of(args)
                : List.of(
                    "Adorei a aula de computação em nuvem, foi muito interessante!",
                    "O servidor caiu de novo e perdi todo o meu trabalho. Que raiva.",
                    "O banco de dados PostgreSQL foi criado na região Brazil South.",
                    "O jogo é lindo, mas a dificuldade do final é frustrante."
                  );

        System.out.println("=== Análise de Sentimento - Azure AI Language ===\n");
        for (String texto : textos) {
            analisar(client, texto);
        }
    }

    /** Cria o cliente autenticado a partir das variáveis de ambiente. */
    private static TextAnalyticsClient criarCliente() {
        String chave = System.getenv("AZURE_LANGUAGE_KEY");
        String endpoint = System.getenv("AZURE_LANGUAGE_ENDPOINT");

        if (chave == null || chave.isBlank() || endpoint == null || endpoint.isBlank()) {
            System.err.println("Defina as variáveis de ambiente AZURE_LANGUAGE_KEY e AZURE_LANGUAGE_ENDPOINT.");
            System.exit(1);
        }

        return new TextAnalyticsClientBuilder()
                .credential(new AzureKeyCredential(chave))
                .endpoint(endpoint)
                .buildClient();
    }

    /** Envia um texto ao serviço na nuvem e imprime o sentimento geral e por frase. */
    private static void analisar(TextAnalyticsClient client, String texto) {
        DocumentSentiment resultado = client.analyzeSentiment(texto, IDIOMA);

        System.out.println("Texto: \"" + texto + "\"");
        System.out.println("Sentimento geral: " + traduzir(resultado.getSentiment().toString()));
        System.out.println("  " + formatar(resultado.getConfidenceScores()));

        // Quando o texto tem mais de uma frase, mostra o sentimento de cada uma
        int qtdFrases = 0;
        for (SentenceSentiment ignored : resultado.getSentences()) qtdFrases++;
        if (qtdFrases > 1) {
            for (SentenceSentiment frase : resultado.getSentences()) {
                System.out.printf("  -> \"%s\": %s%n",
                        frase.getText(), traduzir(frase.getSentiment().toString()));
            }
        }
        System.out.println();
    }

    private static String formatar(SentimentConfidenceScores s) {
        return String.format("Confiança -> Positivo: %.2f | Neutro: %.2f | Negativo: %.2f",
                s.getPositive(), s.getNeutral(), s.getNegative());
    }

    private static String traduzir(String sentimento) {
        switch (sentimento.toLowerCase()) {
            case "positive": return "POSITIVO";
            case "negative": return "NEGATIVO";
            case "neutral":  return "NEUTRO";
            case "mixed":    return "MISTO";
            default:         return sentimento;
        }
    }
}
