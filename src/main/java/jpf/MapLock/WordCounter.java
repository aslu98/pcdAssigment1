package jpf.MapLock;
import java.util.List;

public class WordCounter extends Thread {

	private final GlobalMap map;
	private final List<String> pdfWords;
	private final Model m;

	public WordCounter(final int index, final GlobalMap map, final List<String> pdfWords, final Model m){
		super("wordCounter " + index);
		this.map = map;
		this.pdfWords = pdfWords;
		this.m = m;
	}

	public void run(){
		for (String w : pdfWords) {
			map.computeWord(w, this.getName());
		}
	   //m.update();
	}
}
