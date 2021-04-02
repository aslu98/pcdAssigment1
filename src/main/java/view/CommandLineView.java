package view;

import model.Model;

public class CommandLineView implements ModelObserver {

    private final long startTime;

    public CommandLineView(final long startTime){
        this.startTime = startTime;
    }
    @Override
    public void modelUpdated(Model model) {
        if (model.isCompleted()){
            System.out.println("All pdfs processed.");
            long endTime = System.nanoTime();
            long timeElapsed = endTime - startTime;
            System.out.println("Execution time in nanoseconds  : " + timeElapsed);
            System.out.println("Execution time in milliseconds : " + timeElapsed / 1000000);
        }
        System.out.println(model.getTotWords() + " words processed.");
        model.getWordCount().forEach((w, count) -> System.out.println(w +": " + count +" times"));
    }
}
