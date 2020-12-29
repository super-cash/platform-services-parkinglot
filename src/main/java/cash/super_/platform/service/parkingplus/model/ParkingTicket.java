package cash.super_.platform.service.parkingplus.model;

import javax.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ParkingTicket {

  /**
   * The ticket number
   */
  @NotBlank
  private String ticketNumber;
  /**
   * Optional value to verify the status of a ticket
   */
  private Long saleId;

  public String getTicketNumber() {
    return ticketNumber;
  }

  public void setTicketNumber(String ticketNumber) {
    this.ticketNumber = ticketNumber;
  }

  public Long getSaleId() {
    return saleId;
  }

  public void setSaleId(Long saleId) {
    this.saleId = saleId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((saleId == null) ? 0 : saleId.hashCode());
    result = prime * result + ((ticketNumber == null) ? 0 : ticketNumber.hashCode());
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
    ParkingTicket other = (ParkingTicket) obj;
    if (saleId == null) {
      if (other.saleId != null)
        return false;
    } else if (!saleId.equals(other.saleId))
      return false;
    if (ticketNumber == null) {
      if (other.ticketNumber != null)
        return false;
    } else if (!ticketNumber.equals(other.ticketNumber))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ParkingTicket [ticketNumber=" + ticketNumber + ", saleId=" + saleId + "]";
  }

}
