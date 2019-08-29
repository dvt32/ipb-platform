package com.ipb.platform.worker;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

import com.ipb.platform.persistence.LandmarkRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebParserWorker {
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36";

	@Autowired
	private LandmarkRepository landmarkRepository;

	/*
	 * Establish connection to remote URL.
	 * @return retrieved source as {@see Document}
	 */
	public Document connectURL(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).userAgent(USER_AGENT).get();
		} catch (IOException e) {
			LOGGER.info("IOException", e);
		}
		return doc;
	}

	/*
	 * Returns matched elements by scc selector.
	 * @return matched elements
	 */
	public Elements getElementsBySelector(Document doc, String selector) {
		return doc.select(selector);
	}
}
