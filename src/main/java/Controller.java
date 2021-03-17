import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class Controller {

    private final int ADDITIONAL_THREADS = 3;
    private final int nThreads;
    private final List<PDFDocumentReader> pdfReaders = new LinkedList<>();
    private final int numberOfOutputWords;
    private final GlobalMap globalMap = new GlobalMap();
    private int totWords = 0;
    private int threadsDone = 0;

    public Controller(final String toIgnorePath, final String directoryPath, final int wordsNumber) throws IOException {
        this.numberOfOutputWords = wordsNumber;
        File[] pdfFiles = new File(directoryPath).listFiles((dir, name) -> name.endsWith(".pdf"));
        List<String> wordsToIgnore = Files.readAllLines(new File(toIgnorePath).toPath());
        for (int i = 0; i <pdfFiles.length; i++){
            this.pdfReaders.add(new PDFDocumentReader(pdfFiles[i], wordsToIgnore));
        }
        this.nThreads = Math.min(Runtime.getRuntime().availableProcessors() + ADDITIONAL_THREADS, pdfReaders.size());
    }

    private void startThreads(){
        int readersEachThread, start, stop = -1;
        System.out.println("total readers " + pdfReaders.size());
        for (int i = 0; i < nThreads; i++) {
            readersEachThread = pdfReaders.size()/nThreads +  (i <= pdfReaders.size()%nThreads ? 1 : 0);
            start = stop + 1;
            stop = start + readersEachThread - 1;
            System.out.println("[Thread  " + i +"] from reader " + start + " to reader " + stop);
            new WordCounter(globalMap, pdfReaders.subList(start, stop), i, this).start();
        }
    }

    private void printMostFrequentWords(final Map<String, Integer> wordCount) {
        Map<String, Integer> sortedWordCount = wordCount.entrySet().stream()
                                                        .sorted(Map.Entry.<String,Integer>comparingByValue().reversed())
                                                        .limit(numberOfOutputWords)
                                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                                                (e1, e2) -> e1, LinkedHashMap::new));
        System.out.println(totWords + " analysed");
        sortedWordCount.forEach((w, count) -> System.out.println(w +": " + count +" times"));
    }

    public void mostFrequentWords(){
        this.startThreads();
    }

    public void threadCompleted(final int threadTotWords){
        this.threadsDone+=1;
        this.totWords += threadTotWords;
        if (threadsDone == nThreads){
            this.printMostFrequentWords(globalMap.getMap());
        }
    }

    public void sequentialMostFrequentWords(){
        pdfReaders.forEach(pdf -> {
            try {
                List<String> pdfWords = pdf.getUsefulWords();
                pdf.closeDoc();
                for (String w: pdfWords){
                    globalMap.updateWordCount(w);
                }
                totWords += pdfWords.size();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        printMostFrequentWords(globalMap.getMap());
    }

}
