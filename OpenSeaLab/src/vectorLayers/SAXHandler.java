package vectorLayers;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import feature.Feature;
import feature.FeatureCollection;
import feature.GeometryFactory;
import feature.Point;
import feature.Polygon;

public class SAXHandler extends DefaultHandler {
	private final FeatureCollection featureCollection = new FeatureCollection();
	private Feature feature;

	private boolean isFeature;
	private boolean multi;
	private boolean polygon;
	private boolean point;
	private boolean lineString;

	private String element;
	private StringBuilder sb;
	private Polygon pol;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equals("gml:featureMember") || qName.equals("wfs:member")) {
			feature = new Feature();
			featureCollection.addFeature(feature);
			multi = false;
			isFeature = true;
			sb = new StringBuilder();
			return;
		}
		element = qName.split(":")[1];
		if (element.equals("MultiSurface")) { // add more conditions if needed (e.g multilinestring)
			multi = true;
			return;
		}
		if (element.equals("Polygon")) {
			pol = new Polygon();
			polygon = true;
			return;
		}
		if (element.equals("Point")) {
			point = true;
			return;
		}
		if (element.equals("LineString")) {
			lineString = true;
			return;
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		String endElement = qName.split(":")[1];
		if (endElement == null) {
			return;
		}
		if (polygon && (endElement.equals("exterior") || endElement.equals("interior"))) {
			pol.addRing(GeometryFactory.createPoints(sb.toString()));
			sb = new StringBuilder();
			return;
		}
		if (endElement.equals("Polygon")) {
			if (multi) {
				Polygon geo = (Polygon) feature.getGeometry();
				if (geo == null) {
					geo = new Polygon();
					feature.setGeometry(geo);
				}
				geo.addRings(pol);
			} else {
				feature.setGeometry(pol);
			}

			polygon = false;
			sb = new StringBuilder();
			return;
		}
		if (endElement.equals("Point")) {
			if (multi) {
				// TODO deal with multipoint
			} else {
				feature.setGeometry(GeometryFactory.newPoint(sb.toString()));
			}
			sb = new StringBuilder();
			point = false;
			return;
		}

		if (endElement.equals("LineString")) {
			if (multi) {
				// TODO deal with multilinestring
			} else {
				feature.setGeometry(GeometryFactory.newLineString(sb.toString()));
			}
			lineString = false;
			sb = new StringBuilder();
			return;
		}

		element = "";
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (new String(ch, start, length).trim().length() == 0)
			return;
		String s = new String(ch, start, length).replace("\t", " ");

		if (element.equals("lowerCorner")) {
			Point p = (Point) GeometryFactory.newPoint(s);
			if (isFeature) {
				feature.getBbox()[0] = p;
			}
			return;
		}
		if (element.equals("upperCorner")) {
			Point p = (Point) GeometryFactory.newPoint(s);
			if (isFeature) {
				feature.getBbox()[1] = p;
			}
			return;
		}
		if (polygon) {
			sb.append(s);
			return;
		}
		if (point) {
			if (s.contains(",")) {
				String[] splited = s.split(",");
				s = splited[1] + " " + splited[0];
			}
			sb.append(s);
			return;
		}
		if (lineString) {
			sb.append(s);
			return;
		}
		if (isFeature) {
			feature.addProperty(element, s);
		}
	}

	public FeatureCollection getFeatures() {
		return featureCollection;
	}

}
