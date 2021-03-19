package model;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.Math.min;

public class PDFDocumentReader {

    private final int NUMBER_OF_PAGES_EACH_READ = 10;
    private PDDocument toRead;
    private String title;
    private final List<String> wordsToIgnore;
    private int actualPage = 1;

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
            this.title = file.getName();
            System.out.println("Loaded " + title);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public String getTitle(){
        return this.title;
    }

    public Optional<List<String>> extractWords() {
        try {
            PDFTextStripper stripper = new PDFTextStripper();
            if (actualPage <= toRead.getNumberOfPages()) {
                stripper.setStartPage(actualPage);
                actualPage = min(toRead.getNumberOfPages(), actualPage + NUMBER_OF_PAGES_EACH_READ);
                stripper.setEndPage(actualPage);
                actualPage += 1;
                return getWords(stripper);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<List<String>> extractAllWords() {
        try {
            PDFTextStripper stripper = new PDFTextStripper();
            return getWords(stripper);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Optional<List<String>> getWords(PDFTextStripper stripper) throws IOException {
        String text = (stripper.getText(toRead)).toLowerCase();
        List<String> words = new ArrayList<>(Arrays.stream(text.split("\\W+")).collect(Collectors.toList()));
        for (String toIgnore : wordsToIgnore) {
            words.removeIf(word -> word.equals(toIgnore));
        }
        return Optional.of(words);
    }
}
