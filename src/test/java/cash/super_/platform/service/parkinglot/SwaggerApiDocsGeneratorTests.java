package cash.super_.platform.service.parkinglot;

import cash.super_.Application;
import cash.super_.platform.autoconfig.ParkingPlusServiceClientProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

// https://www.xenonstack.com/insights/what-are-assertion-frameworks

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * gradle clean test
 *
 * It will generate the following files:
 *
 * $ ls -la build/generated/sources/
 *  * swagger-api-docs.json:  the full swagger api in json
 *  * swagger-root-path.txt:  the string of the root path for Gitlab Environmens
 */
// https://spring.io/guides/gs/testing-web/
// https://reflectoring.io/spring-boot-test/ -> ALL TYPES OF TESTS,JPA, WEBSOCKET, ETC
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RestClientConfig.class)
// https://stackoverflow.com/questions/57609818/how-can-i-use-springboottestwebenvironment-with-datajpatest/57609911#57609911
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        // Loads all classes included in the application
        classes = Application.class
)
// Must be added to avoid https://newbedev.com/spring-boot-test-fails-saying-unable-to-start-servletwebserverapplicationcontext-due-to-missing-servletwebserverfactory-bean
@EnableAutoConfiguration
public class    SwaggerApiDocsGeneratorTests {

    protected static final Logger LOG = LoggerFactory.getLogger(SwaggerApiDocsGeneratorTests.class);
    public static final String GENERATED_SOURCES_DIR = "build/generated/sources/";
    public static final String SWAGGER_API_DOCS_JSON_FILE = GENERATED_SOURCES_DIR + "swagger-api-docs.json";
    public static final String ROOT_PATH_FILE = GENERATED_SOURCES_DIR + "swagger-root-path.txt";

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int serverPort;

    @Autowired
    private ParkingPlusServiceClientProperties properties;

    /**
     * Based on https://stackoverflow.com/questions/41808417/how-to-generate-swagger-json/47569970#47569970
     *
     * TODO: THIS WILL BREAK WHEN WE ADOPT SPRINGDOCS BECAUSE IT IS NOT WORKING IN TESTS
     *
     * @throws Exception
     */
    @Test
    public void testApiDocsPath() throws Exception {
        String url = "http://localhost:" + this.serverPort + "/swagger/docs/v2";
        String swaggerApiDocs = this.restTemplate.getForObject(url, String.class);

        // https://mkyong.com/java/java-how-to-create-and-write-to-a-file/
        Path swaggerDocsPath = new File(SWAGGER_API_DOCS_JSON_FILE).toPath();
        Files.write(swaggerDocsPath, swaggerApiDocs.getBytes(StandardCharsets.UTF_8));

        // https://stackoverflow.com/questions/23398736/custom-error-message-for-assertthat-in-junit
        // https://www.xenonstack.com/insights/what-are-assertion-frameworks
        // https://www.vogella.com/tutorials/Hamcrest/article.html
        assertThat("Swagger API file Docs must be created", swaggerDocsPath.toFile().exists(), is(equalTo(true)));

        String readJson = Files.readString(swaggerDocsPath);
        assertThat("Json written is different than loaded", readJson, is(equalTo(swaggerApiDocs)));

        Set<String> paths = parseAllPaths(swaggerApiDocs);
        String rootPath = paths.stream()
                .filter(resourcePath -> !resourcePath.contains("manage"))
                .sorted(Comparator.comparingInt(String::length))
                .findFirst()
                .get();

        String[] pathParts = rootPath.split("/");
        rootPath = String.format("/%s/%s", pathParts[1], pathParts[2]);

        if (!rootPath.contains(properties.getApiVersion())) {
            throw new IllegalStateException("The APIs must contain the declared version=" + properties.getApiVersion());
        }

        LOG.debug("The root path of the business logic is {}", rootPath);

        // https://mkyong.com/java/java-how-to-create-and-write-to-a-file/
        Path apiPath = new File(ROOT_PATH_FILE).toPath();
        Files.write(apiPath, rootPath.getBytes(StandardCharsets.UTF_8));
        assertThat("Root path file must be created", apiPath.toFile().exists(), is(equalTo(true)));

        String rootSavedContent = Files.readString(apiPath);
        assertThat("Json written is different than loaded", rootPath, is(equalTo(rootSavedContent)));
    }

    /**
     * http://www.masterspringboot.com/web/rest-services/parsing-json-in-spring-boot-using-jsonparser/
     * @param swaggerApiDocs
     * @return
     */
    private Set<String> parseAllPaths(String swaggerApiDocs) {
        Set<String> allPaths = new HashSet<>();
        JsonParser springParser = JsonParserFactory.getJsonParser();
        Map<String, Object> properties = springParser.parseMap(swaggerApiDocs);
        for (Map.Entry <String,Object > entry: properties.entrySet()) {
            if (entry.getKey().equals("paths")) {
                Map<String, Object> paths = (Map<String, Object>) entry.getValue();
                allPaths.addAll(paths.keySet());
                break;
            }
        }
        return allPaths;
    }
}
