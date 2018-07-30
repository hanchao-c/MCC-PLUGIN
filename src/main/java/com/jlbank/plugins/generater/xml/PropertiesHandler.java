package com.jlbank.plugins.generater.xml;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PropertiesHandler {

	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "property")
	private List<Property> properties;

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	@Override
	public String toString() {
		return "PropertiesHandler [properties=" + properties + "]";
	}

	public Map<String, String> autoFill() {
		if (null == this.properties || properties.isEmpty()) {
			return Maps.newHashMap();
		}
		Map<String, String> finishingData = Maps.newHashMap();
		Map<String, String> rawData = Maps.newHashMap();
		for (Property property : properties) {
			String name = property.getName();
			String value = property.getValue();
			if (StringUtils.isBlank(name)) {
				throw new IllegalStateException("property.name is null");
			}
			if (StringUtils.contains(name, "$")) {
				throw new IllegalStateException("property.name is Illegal");
			}
			if (StringUtils.contains(value, "$")) {
				rawData.put(name, value);
			} else {
				finishingData.put(name, value);
			}
		}
		if (finishingData.isEmpty()) {
			throw new IllegalStateException("none value is completed");
		}
		boolean handle = true;

		while (handle) {
			boolean currentHandle = false;
			for (Map.Entry<String, String> entry : rawData.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				String rowKey = getRawKey(value);
				if (finishingData.containsKey(rowKey)) {
					rawData.remove(key);
					finishingData.put(key, StringUtils.replace(value, "${" + rowKey + "}", finishingData.get(rowKey)));
					currentHandle = true;
					break;
				}
			}
			if (!currentHandle) {
				handle = false;
			}
		}

		Iterable<Property> propertiesIterable = Iterables.transform(finishingData.entrySet(),
				new Function<Map.Entry<String, String>, Property>() {

					@Override
					public Property apply(Entry<String, String> input) {
						return new Property(input.getKey(), input.getValue());
					}
				});
		this.properties = Lists.newArrayList(propertiesIterable);
		return finishingData;

	}

	private static String getRawKey(String key) {
		return StringUtils.substringBetween(key, "${", "}");
	}

}
