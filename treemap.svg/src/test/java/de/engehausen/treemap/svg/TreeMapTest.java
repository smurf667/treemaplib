package de.engehausen.treemap.svg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.junit.Assert;
import org.junit.Test;

import de.engehausen.treemap.Node;
import de.engehausen.treemap.TreeModel;
import de.engehausen.treemap.svg.impl.CushionRectangleRenderer;

public class TreeMapTest {

	@Test
	public void testGeneration() throws XMLStreamException {
		final TreeMap<Node> map = new TreeMap<Node>(TreeModel.WIJK);
		map.setLabelProvider((nodes, node) -> {
			return node.getNode().getName();
		});
		map.setRectangleRenderer(new CushionRectangleRenderer<>());
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		map.render(out, 1280, 720, () -> false);
		final ByteArrayInputStream bais = new ByteArrayInputStream(out.toByteArray());
		final XMLInputFactory factory = XMLInputFactory.newInstance();
		final XMLEventReader reader = factory.createXMLEventReader(new InputStreamReader(bais));
		final Map<String, AtomicInteger> counts = new HashMap<>();
		while (reader.hasNext()) {
			final XMLEvent event = reader.nextEvent();
			if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
				final AtomicInteger count = counts.computeIfAbsent(((StartElement) event).getName().getLocalPart(), key -> new AtomicInteger());
				count.getAndIncrement();
			}
		}
		expect("svg", 1, counts);
		expect("defs", 1, counts);
		expect("radialGradient", 1, counts);
		expect("g", 1, counts);
		expect("rect", 16, counts);
		expect("text", 8, counts);
	}

	private void expect(final String element, final int count, final Map<String, AtomicInteger> counts) {
		final AtomicInteger integer = counts.computeIfAbsent(element, key -> new AtomicInteger());
		Assert.assertEquals(count, integer.get());
	}

}