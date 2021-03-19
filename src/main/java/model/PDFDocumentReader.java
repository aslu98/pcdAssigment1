package model;

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

    private PDDocument toRead;
    private final List<String> wordsToIgnore;
    private List<String> words;

    public PDFDocumentReader(final File toReadFile, final List<String> wordsToIgnore) {
        try {
            this.loadDocument(toReadFile);
        } catch( IOException e) {
            e.printStackTrace();
        }
        this.wordsToIgnore = wordsToIgnore;
    }

    private void loadDocument(final File file) throws IOException {
        try {
            this.toRead = PDDocument.load(file);
            AccessPermission ap = this.toRead.getCurrentAccessPermission();
            if (!ap.canExtractContent()) {
                throw new IOException("You do not have permission to extract text");
            }
            System.out.println("Loading " + file.getName());
            this.extractWords();
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if (this.toRead != null) {
                this.toRead.close();
            }
        }
    }

    private void extractWords() {
        try {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = (stripper.getText(toRead)).toLowerCase();
            this.words = new ArrayList<>(Arrays.stream(text.split("\\W+")).collect(Collectors.toList()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getUsefulWords(){
        for(String toIgnore: wordsToIgnore){
            this.words.removeIf(word -> word.equals(toIgnore));
        }
        return words;
    }
}
