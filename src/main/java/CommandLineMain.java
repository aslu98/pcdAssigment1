import controller.ThreadsController;
import view.CommandLineView;

import java.io.File;
import java.io.IOException;

public class CommandLineMain {
    public static void main(String[] args)
    {
        String absolutePath = new File("").getAbsolutePath() + "/src/main/resources/";
        String defaultDirectoryPath = "pdfDocuments";
        String defaultIgnoreFilePath = "ignored/empty.txt";
        int defaultN = 3;

        String d = absolutePath + (args.length >= 1 ? args[0] : defaultDirectoryPath);
        String f = absolutePath + (args.length >= 2 ? args[1] : defaultIgnoreFilePath);
        int n = args.length >= 3 ? Integer.parseInt(args[2]) : defaultN;

        try {
            new ThreadsController(f, d, n, new CommandLineView()).mostFrequentWords();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
