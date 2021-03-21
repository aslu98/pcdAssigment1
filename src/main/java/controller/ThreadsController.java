package controller;

import model.GlobalMap;
import model.PDFDocumentReader;
import model.WordCounter;
import model.SyncWordsExtractor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class ThreadsController {

    private final int NUMBER_OF_PAGES_EACH_SECTION = 20;
    private final int ADDITIONAL_THREADS = 3;
    private final int nThreads;
    private final List<String> wordsToIgnore;
    private final int numberOfOutputWords;
    private final SyncWordsExtractor wordsExtractor;
    private int totWords = 0;
    private int threadsDone = 0;

    public ThreadsController(final String toIgnorePath, final String directoryPath, final int wordsNumber) throws IOException {
        this.numberOfOutputWords = wordsNumber;
        this.wordsToIgnore = Files.readAllLines(new File(toIgnorePath).toPath());
        File[] pdfFiles = new File(directoryPath).listFiles((dir, name) -> name.endsWith(".pdf"));
        this.wordsExtractor = new SyncWordsExtractor(Arrays.stream(pdfFiles).map(f -> new PDFDocumentReader(f, this.wordsToIgnore)).collect(Collectors.toList()), NUMBER_OF_PAGES_EACH_SECTION);
        this.nThreads = Math.min(Runtime.getRuntime().availableProcessors() + ADDITIONAL_THREADS, pdfFiles.length);
    }

    private void startThreads(){
        System.out.println("total readers " + wordsExtractor.size());
        for (int i = 0; i < nThreads; i++) {
            new WordCounter(this.wordsExtractor, i, this).start();
        }
    }

    private void printMostFrequentWords(final Map<String, Integer> wordCount) {
        Map<String, Integer> sortedWordCount = wordCount.entrySet().stream()
                                                        .sorted(Map.Entry.<String,Integer>comparingByValue().reversed())
                                                        .limit(numberOfOutputWords)
                                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                                                (e1, e2) -> e1, LinkedHashMap::new));
        sortedWordCount.forEach((w, count) -> System.out.println(w +": " + count +" times"));
    }

    public void mostFrequentWords(){
        this.startThreads();
    }

    public void threadCompleted(final int threadTotWords, GlobalMap map){
        this.threadsDone+=1;
        this.totWords += threadTotWords;
        if (threadsDone == nThreads){
            System.out.println("All pdfs analysed");
            System.out.println(totWords + " words analysed");
            this.printMostFrequentWords(map.getMap());
        }
    }

    public void update(GlobalMap map){
        this.printMostFrequentWords(map.getMap());
    }

}
