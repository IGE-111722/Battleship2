package battleship;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.util.List;

public class PDFGenerator {
    private static final String FILE_NAME = "Historico_Batalha_Naval.pdf";
    private Document document;

    public PDFGenerator() {
        try {
            document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(FILE_NAME));
            document.open();
            document.add(new Paragraph("HISTÓRICO DE JOGO - BATALHA NAVAL"));
            document.add(new Paragraph("--------------------------------------------------"));

            // --- ESTA É A REDE DE SEGURANÇA ---
            // Se clicares no botão vermelho do IntelliJ, ele tenta fechar o PDF sozinho!
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                fecharDocumento();
            }));
            // ----------------------------------

        } catch (Exception e) {
            System.err.println("Erro ao criar PDF: " + e.getMessage());
        }
    }

    public void adicionarJogada(int numJogada, String jsonShots, String resultado) {
        if (document != null && document.isOpen()) {
            try {
                document.add(new Paragraph("Jogada nº: " + numJogada));
                document.add(new Paragraph("Tiros (JSON): " + jsonShots));
                document.add(new Paragraph("Resultado: " + resultado));
                document.add(new Paragraph(" ")); // Linha em branco
            } catch (Exception e) {
                System.err.println("Erro ao adicionar texto ao PDF: " + e.getMessage());
            }
        }
    }

    public void fecharDocumento() {
        if (document != null && document.isOpen()) {
            document.close();
            System.out.println("=> PDF guardado com sucesso: " + FILE_NAME);
        }
    }
}