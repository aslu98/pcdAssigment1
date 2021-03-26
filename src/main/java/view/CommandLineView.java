package view;

import model.Model;

import java.util.Map;

public class CommandLineView implements ModelObserver {
    @Override
    public void modelUpdated(Model model) {
        if (model.isCompleted()){
            System.out.println("All pdfs analysed");
        }
        model.getSortedWordCount().forEach((w, count) -> System.out.println(w +": " + count +" times"));
    }
}
