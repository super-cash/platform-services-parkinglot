package cash.super_.platform.service.distancematrix;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"cash.super.platform.service.distancematrix.googleMapsApiToken=fake-token"})
@AutoConfigureMockMvc
//@ActiveProfiles({"dev"})
public class ApplicationContextLoadTest {

  @Test
  public void contextLoads() {
  }
}
