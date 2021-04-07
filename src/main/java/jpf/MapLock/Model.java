package jpf.MapLock;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import gov.nasa.jpf.vm.Verify;

public class Model {

    private int threadsDone = 0;
    private final int nThreads = 3;
    List<List<String>> words = new LinkedList<>();
    GlobalMap map = new GlobalMap();

    public void compute() throws InterruptedException {

        words.add(0, new LinkedList<String>());
        words.get(0).add("ciao");
        words.get(0).add("a");
        words.get(0).add("ciao");
        words.get(0).add("b");
        words.get(0).add("c");

        words.add(1, new LinkedList<String>());
        words.get(1).add("ciao");
        words.get(1).add("b");
        words.get(1).add("c");
        words.get(1).add("b");
        words.get(1).add("b");

        words.add(2, new LinkedList<String>());
        words.get(2).add("c");
        words.get(2).add("b");
        words.get(2).add("a");
        words.get(2).add("c");
        words.get(2).add("b");

        List<WordCounter> wcs = new LinkedList<>();

        Verify.beginAtomic();
        for (int i = 0; i < nThreads; i++){
            wcs.add(i, new WordCounter(i, map, words.get(i), this));
            wcs.get(i).start();
        }
        Verify.endAtomic();

        for (int i = 0; i < nThreads; i++){
           wcs.get(i).join();
        }

        Map<String, Integer> newmap = map.getMap();
        int ciaoCount = newmap.get("ciao");
        assert ciaoCount == 3;
        int aCount = newmap.get("a");
        assert aCount == 2;
        int bCount = newmap.get("b");
        assert bCount == 6;
        int cCount = newmap.get("c");
        assert cCount == 4;
    }
}
