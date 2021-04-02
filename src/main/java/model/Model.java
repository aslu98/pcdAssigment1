package model;

import view.ModelObserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class Model {

    private final int NUMBER_OF_PAGES_EACH_SECTION = 60;
    private final int ADDITIONAL_THREADS = 0;
    private int nThreads;
    private int numberOfOutputWords;
    private SyncWordsExtractor wordsExtractor;
    private List<ModelObserver> observers;
    private Map<String, Integer> wordCount;
    private GlobalMap map;
    private List<WordCounter> threads;
    private List<String> wordsToIgnore = new LinkedList<>();
    private int totWords;
    private int threadsDone;
    private int threadsStopped;
    private boolean completed;
    private boolean stopped ;

    public Model(){
        this.observers = new LinkedList<>();
    }

    public void initialize(){
        this.totWords = 0;
        this.threadsDone = 0;
        this.threadsStopped = 0;
        this.completed = false;
        this.stopped = false;
        this.threads = new LinkedList<>();
        this.wordCount = new HashMap<>();
        this.map = new GlobalMap();
        this.nThreads = Runtime.getRuntime().availableProcessors() + 1 + ADDITIONAL_THREADS;
    }

    public void mostFrequentWords(final String toIgnorePath, final String directoryPath, final int wordsNumber, final boolean sequential){
        try {
            this.wordsToIgnore = Files.readAllLines(new File(toIgnorePath).toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        File[] pdfFiles = new File(directoryPath).listFiles((dir, name) -> name.endsWith(".pdf"));
        List <PDFDocumentReader> readers = new LinkedList<>(Arrays.stream(pdfFiles).map(f -> new PDFDocumentReader(f)).collect(Collectors.toList()));
        this.wordsExtractor = new SyncWordsExtractor(readers, NUMBER_OF_PAGES_EACH_SECTION);
        this.numberOfOutputWords = wordsNumber;
        System.out.println("[Model]" + "total readers " + wordsExtractor.size());
        this.nThreads = Math.min(pdfFiles.length, nThreads);
        if (sequential){
            this.singleThread();
        } else {
            this.startThreads();
        }
    }

    private void startThreads(){
        this.stopped = false;
        this.completed = false;
        for (int i = 0; i < nThreads; i++) {
            WordCounter thread = new WordCounter(this.wordsExtractor, this.wordsToIgnore, map, i, this);
            threads.add(thread);
            thread.start();
        }
    }

    private void singleThread(){
        this.stopped = false;
        this.completed = false;
        this.nThreads = 1;
        WordCounter thread = new WordCounter(this.wordsExtractor, this.wordsToIgnore, map, 0, this);
        threads.add(thread);
        thread.start();
    }

    public void stopThreads(){
        for (WordCounter thread: threads) {
            thread.terminate();
        }
    }

    public void threadStopped(){
        this.threadsStopped += 1;
        if (threadsStopped == nThreads){
            this.stopped = true;
            System.out.println("All stopped.");
            this.notifyObservers();
        }
    }

    public void threadCompleted(){
        this.threadsDone += 1;
        if (threadsDone == nThreads){
            this.completed = true;
            System.out.println("All completed.");
            this.notifyObservers();
        }
    }

    public synchronized void update (final int nWords, Map<String, Integer> actualMap){
        this.totWords += nWords;
        this.wordCount = actualMap;
        this.notifyObservers();
    }

    public void addObserver(ModelObserver obs){
        observers.add(obs);
    }

    public void notifyObservers(){
        for (ModelObserver obs: observers){
            obs.modelUpdated(this);
        }
    }

    public boolean isCompleted(){
        return this.completed;
    }

    public boolean isStopped(){
        return this.stopped;
    }

    public synchronized int getTotWords(){
        return this.totWords;
    }

    public synchronized Map<String, Integer> getWordCount(){
        return this.wordCount.entrySet().stream()
                        .limit(numberOfOutputWords)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                (e1, e2) -> e1, LinkedHashMap::new));
    }
}
