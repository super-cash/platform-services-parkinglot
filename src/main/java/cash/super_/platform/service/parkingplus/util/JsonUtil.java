package cash.super_.platform.service.parkingplus.util;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility methods used all around.
 *
 * @author marcellodesales
 *
 */
public enum JsonUtil {
  ;

  private static final Logger LOG = LoggerFactory.getLogger(JsonUtil.class);

  /**
   * For JSON parsing
   */
  private static final ObjectMapper MAPPER = new ObjectMapper();

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

    } catch (IOException errorDeserializing) {
      LOG.error("While deserializing json String " + jsonObject, errorDeserializing);
      return null;
    }
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
  public static <T> T toObject(Optional<ByteBuffer> buffer, Class<T> clazz) {
    try {
      String jsonObject = StandardCharsets.UTF_8.decode(buffer.get()).toString();
      return MAPPER.readValue(jsonObject, clazz);

    } catch (IOException errorDeserializing) {
      LOG.error("While deserializing json String {}", errorDeserializing);
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
