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

    private final File toReadFile;
    private final List<String> wordsToIgnore;
    private String title;
    private Optional<PDDocument> toRead;
    private int actualPage = 1;

    public PDFDocumentReader(final File toReadFile, final List<String> wordsToIgnore) {
       this.toReadFile = toReadFile;
       this.wordsToIgnore = wordsToIgnore;
       this.toRead = Optional.empty();
    }

    private void loadDocument(final File file) throws IOException {
        try {
            this.toRead = Optional.of(PDDocument.load(file));
            AccessPermission ap = this.toRead.get().getCurrentAccessPermission();
            if (!ap.canExtractContent()) {
                throw new IOException("You do not have permission to extract text");
            }
            this.title = file.getName();
            System.out.println("Loaded " + title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Optional<List<String>> extractWords(final int pagesEachSection){
        try {
            if (toRead.isEmpty()){
                this.loadDocument(toReadFile);
            }
            PDFTextStripper stripper = new PDFTextStripper();
            if (actualPage <= toRead.get().getNumberOfPages()) {
                stripper.setStartPage(actualPage);
                actualPage = min(toRead.get().getNumberOfPages(), actualPage + pagesEachSection);
                stripper.setEndPage(actualPage);
                actualPage += 1;
                String text = (stripper.getText(toRead.get())).toLowerCase();
                List<String> words = new ArrayList<>(Arrays.stream(text.split("\\W+")).collect(Collectors.toList()));
                for (String toIgnore : wordsToIgnore) {
                    words.removeIf(word -> word.equals(toIgnore));
                }
                return Optional.of(words);
            }
        } catch (IOException e) {
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
