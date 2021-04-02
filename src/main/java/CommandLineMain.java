import model.Model;
import util.DefaultInputs;
import view.CommandLineView;

public class CommandLineMain {
    public static void main(String[] args)
    {
        long startTime = System.nanoTime();
        String d = DefaultInputs.AbsolutePath + (args.length >= 1 ? args[0] : DefaultInputs.DirectoryPath);
        String f = DefaultInputs.AbsolutePath + (args.length >= 2 ? args[1] : DefaultInputs.IgnoreFilePath);
        int n = args.length >= 3 ? Integer.parseInt(args[2]) : DefaultInputs.N;

        Model m = new Model();
        m.addObserver(new CommandLineView(startTime));
        m.initialize();
        m.mostFrequentWords(f, d, n, false);
    }
}
