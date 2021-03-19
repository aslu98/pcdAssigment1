package controller;

import model.GlobalMap;
import model.PDFDocumentReader;
import model.WordCounter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class ThreadsController {

    private final int ADDITIONAL_THREADS = 1;
    private final int nThreads;
    private final List<File> pdfFiles;
    private final List<String> wordsToIgnore;
    private final int numberOfOutputWords;
    private int totWords = 0;
    private int threadsDone = 0;

    public ThreadsController(final String toIgnorePath, final String directoryPath, final int wordsNumber) throws IOException {
        this.numberOfOutputWords = wordsNumber;
        this. pdfFiles = Arrays.stream(new File(directoryPath).listFiles((dir, name) -> name.endsWith(".pdf"))).collect(Collectors.toList());
        this. wordsToIgnore = Files.readAllLines(new File(toIgnorePath).toPath());
        this.nThreads = Math.min(Runtime.getRuntime().availableProcessors() + ADDITIONAL_THREADS, pdfFiles.size());
    }

    private void startThreads(){
        int readersEachThread, start, stop = 0;
        System.out.println("total readers " + pdfFiles.size());
        for (int i = 0; i < nThreads; i++) {
            readersEachThread = pdfFiles.size()/nThreads +  (i < pdfFiles.size() % nThreads ? 1 : 0);
            start = stop;
            stop = start + readersEachThread;
            System.out.println("[wordCounter  " + i +"] from reader " + start + " to reader " + stop);
            new WordCounter(pdfFiles.subList(start, Math.min(stop, pdfFiles.size())), wordsToIgnore, i, this).start();
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

    public void sequentialMostFrequentWords(){
        HashMap<String, Integer> map = new HashMap<>();
        List<PDFDocumentReader> pdfReaders = new LinkedList<>();
        for (int i = 0; i <pdfFiles.size(); i++){
            pdfReaders.add(new PDFDocumentReader(pdfFiles.get(i), wordsToIgnore));
        }
        pdfReaders.forEach(pdf -> {
            List<String> pdfWords = pdf.getUsefulWords();
            for (String w: pdfWords){
                map.put(w, (map.containsKey(w)? 1: map.get(w) +1));
            }
            totWords += pdfWords.size();
        });
        printMostFrequentWords(map);
    }

}
