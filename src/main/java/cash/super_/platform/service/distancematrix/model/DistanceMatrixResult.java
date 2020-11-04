package cash.super_.platform.service.distancematrix.model;

import com.google.maps.model.DistanceMatrixElement;

/**
 * The distance result to be displayed for users.
 *
 * @author marcellodesales
 *
 */
public class DistanceMatrixResult {

  private long distance;
  private long time;

  // Used for deserialization
  public DistanceMatrixResult() {
    
  }

  public DistanceMatrixResult(long distance, long time) {
    this.distance = distance;
    this.time = time;
  }

  public DistanceMatrixResult(DistanceMatrixElement calculationResults) {
    this(calculationResults.distance.inMeters, calculationResults.duration.inSeconds);
  }

  public long getDistance() {
    return distance;
  }

  public void setDistance(long distance) {
    this.distance = distance;
  }

  public long getTime() {
    return time;
  }

  public void setTime(long time) {
    this.time = time;
  }

  @Override
  public String toString() {
    return "DistanceMatrixResult [distance=" + distance + ", time=" + time + "]";
  }
}
