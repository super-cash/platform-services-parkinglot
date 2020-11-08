package cash.super_.platform.service.parkingplus.model;

/**
 * The parking ticket payments input for queries on GET /2/pagamentosEfetuados
 *
 * @author marcellodesales
 *
 */
public class ParkingTicketPaymentsMadeQuery {

  /**
   * The value of udid of the Parking plus
   */
  private String userId;
  /**
   * The value of inicio, pagination start key
   */
  private int paginationStart;
  /**
   * The value of limite for pagination
   */
  private int paginationLimit;

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public int getPaginationStart() {
    return paginationStart;
  }

  public void setPaginationStart(int paginationStart) {
    this.paginationStart = paginationStart;
  }

  public int getPaginationLimit() {
    return paginationLimit;
  }

  public void setPaginationLimit(int paginationLimit) {
    this.paginationLimit = paginationLimit;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "  [userId=" + userId + ", paginationStart=" + paginationStart
        + ", paginationLimit=" + paginationLimit + "]";
  }
}
