import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PDFDocumentReader {

    private final PDDocument toRead;
    private final List<String> wordsToIgnore;

    public PDFDocumentReader(final File toReadFile, final List<String> wordsToIgnore) throws IOException {
        this.toRead = loadDocument(toReadFile);
        this.wordsToIgnore = wordsToIgnore;
    }

    public PDDocument loadDocument(final File file) throws IOException {
        PDDocument document = PDDocument.load(file);
        AccessPermission ap = document.getCurrentAccessPermission();
        if (!ap.canExtractContent())
        {
            throw new IOException("You do not have permission to extract text");
        }
        return document;
    }

    private List<String> getWords() throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        String text = (stripper.getText(toRead)).toLowerCase();
        List<String> words = new ArrayList<>(Arrays.stream(text.split("\\W+")).collect(Collectors.toList()));
        return words;
    }

    public List<String> getUsefulWords() throws IOException {
        List<String> words = getWords();
        for(String toIgnore: wordsToIgnore){
            words.removeIf(word -> word.equals(toIgnore));
        }
        return words;
    }

    public void closeDoc() throws IOException {
        toRead.close();
    }
}
