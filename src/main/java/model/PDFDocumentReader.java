package model;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.min;

public class PDFDocumentReader {

    private final File toReadFile;
    private final List<String> wordsToIgnore;
    private PDFTextStripper stripper;
    private String title;
    private Optional<PDDocument> toRead;
    private int actualPage = 1;

    public PDFDocumentReader(final File toReadFile, final List<String> wordsToIgnore) {
       this.toReadFile = toReadFile;
       this.wordsToIgnore = wordsToIgnore;
       this.toRead = Optional.empty();
    }

    public void loadDocument() throws IOException {
        try {
            this.stripper = new PDFTextStripper();
            this.toRead = Optional.of(PDDocument.load(toReadFile));
            AccessPermission ap = this.toRead.get().getCurrentAccessPermission();
            if (!ap.canExtractContent()) {
                throw new IOException("You do not have permission to extract text");
            }
            this.title = toReadFile.getName();
            System.out.println("Loaded " + title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Optional<List<String>> extractWords(final int pagesEachSection){
        try {
            if (toRead.isEmpty()){
                this.loadDocument();
            }
            if (actualPage <= toRead.get().getNumberOfPages()) {
                this.stripper.setStartPage(actualPage);
                actualPage = min(toRead.get().getNumberOfPages(), actualPage + pagesEachSection);
                this.stripper.setEndPage(actualPage);
                actualPage += 1;
                String text = (stripper.getText(toRead.get())).toLowerCase();
                List<String> words = new LinkedList<>(Arrays.stream(text.split("\\s+")).collect(Collectors.toList()));
                for (String toIgnore : wordsToIgnore) {
                    words.removeIf(word -> word.equals(toIgnore));
                }
                return Optional.of(words);
            }
        } catch (IOException e) {
            System.out.println(this.title);
            e.printStackTrace();
        }
        this.closeDocument();
        return Optional.empty();
    }

    private void closeDocument(){
        try {
            this.toRead.get().close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
