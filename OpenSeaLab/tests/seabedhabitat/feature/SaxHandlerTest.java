package seabedhabitat.feature;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;
import org.xml.sax.SAXException;

import seabedhabitat.SAXHandler;

public class SaxHandlerTest {

	@Test
	public void test() throws ParserConfigurationException, SAXException, IOException {
		File file = new File("test.xml");
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		SAXHandler userhandler = new SAXHandler();
		saxParser.parse(file,userhandler);
	}

}
