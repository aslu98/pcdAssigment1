package view;

import java.util.Map;

public class CommandLineView implements View{
    @Override
    public void update(final Map<String, Integer> sortedWordCount) {
        sortedWordCount.forEach((w, count) -> System.out.println(w +": " + count +" times"));
    }
}
