package main;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

public class FileUtilities {

  private static final boolean PRIVATE_LOCATION = true;
  private static final String MAP_LOCATION = "private/maps/exampleMap.json";
  private static final String MAP_LOCATION_PRIVATE = "private/maps/privateMap.json";

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static File createOrReadFileFromResources(String inputPath) {
    if (inputPath == null || inputPath.isEmpty()) {
      return null;
    }
    URL resource = Thread.currentThread().getContextClassLoader().getResource(inputPath);

    if (resource != null) {
      inputPath = resource.getPath();
      return new File(inputPath);
    }
    return null;
  }

  public static File createOrReadFile(String inputPath) {
    if (inputPath == null || inputPath.isEmpty()) {
      return null;
    }

    return new File(inputPath);
  }

  public static Map<String, String> readCustomMaps(String mapName) {
    File file;
    if (PRIVATE_LOCATION) {
      file = createOrReadFileFromResources(MAP_LOCATION_PRIVATE);
    } else {
      file = createOrReadFileFromResources(MAP_LOCATION);
    }

    try {
      JsonNode jsonNode = MAPPER.readTree(file);
      JsonNode jsonNodeWCorrectMap = jsonNode.get(mapName);

      return MAPPER.convertValue(jsonNodeWCorrectMap, new TypeReference<>() {});
    } catch (IOException e) {
      e.printStackTrace();
    }

    return Collections.emptyMap();
  }
}
