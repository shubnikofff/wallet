package org.company.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class MessageSerializer<T> implements Serializer<T> {

	private static final Logger logger = Logger.getLogger(MessageSerializer.class.getName());

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public byte[] serialize(String topic, T data) {
		if (data == null) {
			logger.warning("Null received at serializing");
			return new byte[0];
		}

		try {
			return objectMapper.writeValueAsBytes(data);
		} catch (Exception e) {
			throw new SerializationException("Error when serializing kafka message to byte[]");
		}
	}

	@Override
	public byte[] serialize(String topic, Headers headers, T data) {
		headers.add("content-type", data.getClass().getName().getBytes(StandardCharsets.UTF_8));
		return serialize(topic, data);
	}
}
