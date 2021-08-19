package cash.super_.platform.util;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

public class SecretsUtil {

  private static final int RANGE_SIZE = 4;

  /**
   * @param secret the secret
   * @return The hidden secret with ****
   */
  public static String obsfucate(String secret) {
    if (secret.length() < RANGE_SIZE) {
      return secret;
    }
    return secret.substring(0, RANGE_SIZE) + "**********"
        + secret.substring(secret.length() - RANGE_SIZE - 1, secret.length() - 1);
  }

  @SuppressWarnings("deprecation")
  public static String makeApiKey(String ... values) {
    // https://github.com/google/guava/issues/2841#issuecomment-437944061
    return Hashing.sha1().hashString(String.join("", values), Charsets.UTF_8).toString();
  }
}
