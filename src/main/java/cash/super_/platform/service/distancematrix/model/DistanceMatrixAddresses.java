package cash.super_.platform.service.distancematrix.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DistanceMatrixAddresses {

  private String originAddress;
  private String destinationAddress;

  public String getOriginAddress() {
    return originAddress;
  }

  public void setOriginAddress(String originAddress) {
    this.originAddress = originAddress;
  }

  public String getDestinationAddress() {
    return destinationAddress;
  }

  public void setDestinationAddress(String destinationAddress) {
    this.destinationAddress = destinationAddress;
  }

  @Override
  public String toString() {
    return "DistanceMatrixAddresses [originAddress=" + originAddress + ", destinationAddress="
        + destinationAddress + "]";
  }
}
