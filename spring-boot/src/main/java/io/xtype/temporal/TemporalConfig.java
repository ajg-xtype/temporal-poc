package io.xtype.temporal;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.temporal.common.converter.ByteArrayPayloadConverter;
import io.temporal.common.converter.DataConverter;
import io.temporal.common.converter.DefaultDataConverter;
import io.temporal.common.converter.JacksonJsonPayloadConverter;
import io.temporal.common.converter.NullPayloadConverter;
import io.temporal.common.converter.ProtobufJsonPayloadConverter;
import io.temporal.common.converter.ProtobufPayloadConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemporalConfig {

  /**
   * use Spring's object mapper with Kotlin support
   */
  @Bean
  public DataConverter dataConverter(ObjectMapper objectMapper) {
    return new DefaultDataConverter(
        new NullPayloadConverter(),
        new ByteArrayPayloadConverter(),
        new ProtobufJsonPayloadConverter(),
        new ProtobufPayloadConverter(),
        new JacksonJsonPayloadConverter(),
        new JacksonJsonPayloadConverter(objectMapper)
    );
  }
}
