import java.io.IOException;

public class CommandLineMain {
    public static void main(String[] args)
    {
        String absolutePath = "/Users/asialucchi/Documents/Magistrale/II sem/Concorrente/Assignment1/src/main/resources/";
        String defaultDirectoryPath = "pdfDocuments";
        String defaultIgnoreFilePath = "ignored/empty.txt";
        int defaultN = 50;

        String d = absolutePath + (args.length >= 1 ? args[0] : defaultDirectoryPath);
        String f = absolutePath + (args.length >= 2 ? args[1] : defaultIgnoreFilePath);
        int n = args.length >= 3 ? Integer.parseInt(args[2]) : defaultN;

        try {
            Controller controller = new Controller(f, d, n);
            controller.mostFrequentWords();
            //controller.sequentialMostFrequentWords();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
