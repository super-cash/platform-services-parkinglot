package cash.super_.platform.service.parkinglot.ticket;

import cash.super_.platform.service.parkinglot.AbstractParkingLotProxyService;
import cash.super_.platform.service.parkinglot.model.ParkinglotTicket;
import cash.super_.platform.service.parkinglot.repository.ParkinglotTicketRepository;
import cash.super_.platform.utils.DateTimeUtil;
import cash.super_.platform.utils.JpaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Retrieve the list of tickets for a given user from our local cache
 *
 * @author marcellodesales
 *
 */
@Service
public class ParkinglotTicketsService extends AbstractParkingLotProxyService {

  @Autowired
  private ParkinglotTicketRepository parkinglotTicketRepository;

  /**
   * Retrieves tickets for a given user
   */
  public List<ParkinglotTicket> retrieveTickets(Optional<Long> createdAt,
                                                Optional<Integer> pageOffset, Optional<Integer> pageLimit) {
    // Verify if the ticket is new and just got scanned, and if so, it has 3 initial states
    Long userId = supercashRequestContext.getUserId();
    Long storeId = supercashRequestContext.getStoreId();

    List<ParkinglotTicket> userParkingTickets = new ArrayList<>();
    if (pageOffset.isPresent() || pageLimit.isPresent()) {
      int offset = pageOffset.isPresent() ? pageOffset.get() : 0;
      int limit = pageLimit.isPresent() ? pageLimit.get() : 10;
      PageRequest pageRequest = JpaUtil.makePageRequest(offset, limit, Sort.by("createdAt").descending());
      LOG.debug("Retrieving the tickets for the userId={} storeId={} from pageStart={} with pageLimit={}",
              userId, storeId, pageOffset.get(), pageLimit.get());
      Optional<Page<ParkinglotTicket>> allTickets = parkinglotTicketRepository.findAllByUserIdAndStoreId(
              userId, storeId, pageRequest);
      if (allTickets.isPresent()) {
        userParkingTickets.addAll(allTickets.get().toList());
      }

    } else if (createdAt.isPresent()) {
      long createdAtDateTime = createdAt.get();

      // Calculate the 00 hora and the 23:59 of the day the ticket was created
      // We need all tickets for the day, so the array has 2 elements only
      Long[] midnightAndLastMinuteOfDay = DateTimeUtil.getDateZeroHoraMidnightInterval(DateTimeUtil.getLocalDateTime(createdAtDateTime));

      // We fetch all the tickets between those
      Optional<List<ParkinglotTicket>> ticketsByUserAndDateSearch = parkinglotTicketRepository.findByUserIdAndStoreIdAndCreatedAtBetween(
              userId, storeId, midnightAndLastMinuteOfDay[0], midnightAndLastMinuteOfDay[1]);
      LOG.debug("Retrieving the tickets for the given day for userId={} storeId={} at createdAt={} (between {} and {})",
              userId, storeId, createdAtDateTime, DateTimeUtil.getLocalDateTime(midnightAndLastMinuteOfDay[0]), DateTimeUtil.getLocalDateTime(midnightAndLastMinuteOfDay[1]));

      // If there's anything, just return them all
      if (ticketsByUserAndDateSearch.isPresent() && ticketsByUserAndDateSearch.get().size() > 0) {
        userParkingTickets.addAll(ticketsByUserAndDateSearch.get());
      }

    } else {
      LOG.debug("Retrieving the latest 10 tickets for the userId={} storeId={}", userId);
      // Just a list of all tickets based on the userId
      Optional<List<ParkinglotTicket>> last10Tickets = parkinglotTicketRepository.findFirst10ByUserIdAndStoreIdOrderByCreatedAtDesc(
              Long.valueOf(userId), Long.valueOf(storeId));
      if (last10Tickets.isPresent()) {
        userParkingTickets.addAll(last10Tickets.get());
      }
    }

    // Ticket already has the exit transition recorded
    return userParkingTickets;
  }
}
