package cash.super_.platform.utils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import cash.super_.platform.client.DefaultObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Utility methods used all around.
 *
 * @author marcellodesales
 *
 */
public enum JsonUtil {
  ;

  // TODO: this will not use the timezone defined in the yaml file, but it will use the system timezone.
  private static DefaultObjectMapper MAPPER = new DefaultObjectMapper();

  private static final Logger LOG = LoggerFactory.getLogger(JsonUtil.class);

  /**
   * @param o An object instance from a POJO implementation.
   * @return The JSON representation of the given O.
   * @throws JsonProcessingException
   */
  public static String toJson(Object o) throws JsonProcessingException {
      return MAPPER.writeValueAsString(o);
  }

  /**
   * Retrieves an object from a given file instance. More details at
   * http://www.mkyong.com/java/how-to-convert-java-object-to-from-json-jackson/
   *
   * @param <T> is the type of the class to be deserialized.
   * @param jsonObject the object in Json format.
   * @param clazz the class to convert.
   * @return The instance of the given class.
   */
  public static <T> T toObject(String jsonObject, Class<T> clazz) {
    try {
      return MAPPER.readValue(jsonObject, clazz);

    } catch (JsonProcessingException errorDeserializing) {
      LOG.error("While deserializing json String {} {}", jsonObject, errorDeserializing);
      return null;
    }
  }

  /**
   * Retrieves an object from a given file instance. More details at
   * http://www.mkyong.com/java/how-to-convert-java-object-to-from-json-jackson/
   *
   * @param <T> is the type of the class to be deserialized.
   * @param clazz the class to convert.
   * @return The instance of the given class.
   */
  public static <T> T toObject(Optional<ByteBuffer> buffer, Class<T> clazz) {
    String jsonObject = null;
    try {
      if (buffer.isPresent()) {
        jsonObject = StandardCharsets.UTF_8.decode(buffer.get()).toString();
        return MAPPER.readValue(jsonObject, clazz);
      }
      return null;

    } catch (JsonProcessingException errorDeserializing) {
      LOG.error("While deserializing json String {} {}", jsonObject, errorDeserializing);
      return null;
    }
  }

  /**
   * Retrieves an object from a given file instance. More details at
   * http://www.mkyong.com/java/how-to-convert-java-object-to-from-json-jackson/
   *
   * @param <T> is the type of the class to be deserialized.
   * @param file the file path in the fs
   * @param clazz the class to convert.
   * @return The instance of the given class.
   * @throws IOException If any error occurs while loading the file.
   * @throws JsonMappingException  if any mapping error occurs.
   * @throws JsonParseException if any json format error occurs.
   */
  public static <T> T toObject(File file, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
    return MAPPER.readValue(file, clazz);
  }
}
