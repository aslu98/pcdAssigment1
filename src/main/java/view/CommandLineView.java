package view;

import model.Model;

public class CommandLineView implements ModelObserver {
    @Override
    public void modelUpdated(Model model) {
        if (model.isCompleted()){
            System.out.println("All pdfs processed.");
        }
        System.out.println(model.getTotWords() + " words processed.");
        model.getWordCount().forEach((w, count) -> System.out.println(w +": " + count +" times"));
    }
}
