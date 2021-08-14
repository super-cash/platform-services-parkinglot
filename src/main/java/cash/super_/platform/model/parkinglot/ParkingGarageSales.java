package cash.super_.platform.model.parkinglot;

import java.util.List;
import cash.super_.platform.client.parkingplus.model.Promocao;

/**
 * The current list of packing garage sales
 *
 * @author marcellodesales
 *
 */
public class ParkingGarageSales {

  private List<Promocao> current;

  // Used for deserialization
  public ParkingGarageSales() {
    
  }

  public ParkingGarageSales(List<Promocao> current) {
    this.current = current;
  }

  public List<Promocao> getCurrent() {
    return current;
  }

  public void setCurrent(List<Promocao> current) {
    this.current = current;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " [current=" + current + "]";
  }
}
