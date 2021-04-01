package model;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SyncGetReader {

	private final List<PDFDocumentReader> documentReaders;
 	private int actualIndex = 0;

	public SyncGetReader(List<PDFDocumentReader> documentReaders) {
		this.documentReaders = documentReaders;
	}

	public synchronized Optional<PDFDocumentReader> getReader(){
		if (actualIndex < documentReaders.size()) {
			return Optional.of(documentReaders.get(actualIndex++));
		} else {
			return Optional.empty();
		}
	}
}

