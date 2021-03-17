import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WordCounter extends Worker {

	private final List<PDFDocumentReader> pdfReaders;
	private final GlobalMap map;
	private final Controller controller;
	private int totWords = 0;

	public WordCounter(final GlobalMap map, final List<PDFDocumentReader> readers, final int index, final Controller controller){
		super("wordCounter " + index);
		this.map = map;
		this.pdfReaders = readers;
		this.controller = controller;
	}

	public void run(){
		log("before counting");
		pdfReaders.forEach(pdf -> {
			try {
				List<String> pdfWords = pdf.getUsefulWords();
				pdf.closeDoc();
				for (String w: pdfWords){
					map.updateWordCount(w);
				}
				totWords += pdfWords.size();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		log("after counting");
		controller.threadCompleted(totWords);
	}
}
