package cash.super_.platform.service.distancematrix;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

// Without this property the build fails
@SpringBootTest(properties = {"cash.super.platform.service.distancematrix.googleMapsApiToken=fake-token"})
@AutoConfigureMockMvc
//@ActiveProfiles({"dev"})
@DisplayName("Basic Smoke tests")
public class ApplicationContextLoadTest {

  @Test
  @DisplayName("Load Context Test")
  public void contextLoads() {
  }
}
