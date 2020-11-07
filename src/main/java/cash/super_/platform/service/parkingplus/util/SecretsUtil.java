package cash.super_.platform.service.distancematrix.util;

public class StringUtil {

  private static final int RANGE_SIZE = 4;

  /**
   * @param secret the secret
   * @return The hidden secret with ****
   */
  public static String obsfucate(String secret) {
    return secret.substring(0, RANGE_SIZE) + "**********"
        + secret.substring(secret.length() - RANGE_SIZE - 1, secret.length() - 1);
  }
}
