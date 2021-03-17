import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WordCounter extends Worker {

	private final List<PDFDocumentReader> pdfReaders;
	private int totWords = 0;
	private final GlobalMap map;
	private final Controller controller;

	public WordCounter(final GlobalMap map, final List<PDFDocumentReader> readers, final int index, final Controller controller){
		super("wordCounter " + index);
		this.map = map;
		this.pdfReaders = readers;
		this.controller = controller;
	}

	private List<String> getAllWords(){
		final List<String> words = new ArrayList<>();
		pdfReaders.forEach(pdf -> {
			try {
				List<String> pdfWords = pdf.getUsefulWords();
				pdf.closeDoc();
				this.totWords += pdfWords.size();
				words.addAll(pdfWords);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		return words;
	}
	
	public void run(){
		log(" before getting words");
	    List<String> words = getAllWords();
		log(" got all words, before counting");
		for (String w: words){
			map.updateWordCount(w);
		}
		log(" after counting");
		controller.threadCompleted(totWords);
	}
}
