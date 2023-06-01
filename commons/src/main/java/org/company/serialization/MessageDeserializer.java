package org.company.serialization;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.logging.Logger;


public class MessageDeserializer<T> implements Deserializer<T> {

	private static final Logger log = Logger.getLogger(MessageDeserializer.class.getName());

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private JavaType dataType;

	@Override
	public T deserialize(String topic, byte[] data) {
		if (data == null) {
			log.warning("Null received at deserializing");
			return null;
		}

		try {
			return objectMapper.readValue(data, dataType);
		} catch (Exception e) {
			throw new RuntimeException("Error when deserializing kafka message", e.getCause());
		}
	}

	@Override
	public T deserialize(String topic, Headers headers, byte[] data) {
		dataType = resolveType(headers);
		return deserialize(topic, data);
	}

	public static JavaType resolveType(Headers headers) {
		final var type = new String(headers.lastHeader("content-type").value());
		return TypeFactory.defaultInstance().constructFromCanonical(type);
	}
}
