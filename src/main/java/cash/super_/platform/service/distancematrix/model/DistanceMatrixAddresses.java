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
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((destinationAddress == null) ? 0 : destinationAddress.hashCode());
    result = prime * result + ((originAddress == null) ? 0 : originAddress.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DistanceMatrixAddresses other = (DistanceMatrixAddresses) obj;
    if (destinationAddress == null) {
      if (other.destinationAddress != null)
        return false;
    } else if (!destinationAddress.equals(other.destinationAddress))
      return false;
    if (originAddress == null) {
      if (other.originAddress != null)
        return false;
    } else if (!originAddress.equals(other.originAddress))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "DistanceMatrixAddresses [originAddress=" + originAddress + ", destinationAddress="
        + destinationAddress + "]";
  }
}
