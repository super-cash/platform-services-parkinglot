package cash.super_.platform.service.parkinglot.ticket;

import cash.super_.platform.service.parkinglot.AbstractParkingLotProxyService;
import cash.super_.platform.model.parkinglot.ParkinglotTicket;
import cash.super_.platform.service.parkinglot.repository.ParkinglotTicketRepository;
import cash.super_.platform.utils.DateTimeUtil;
import cash.super_.platform.utils.JpaUtil;
import cash.super_.platform.utils.NumberUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

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
  public List<ParkinglotTicket> retrieveTickets(Optional<String> ticketNumber, Optional<Long> createdAt,
                                                Optional<Integer> pageOffset, Optional<Integer> pageLimit) {
    // Verify if the ticket is new and just got scanned, and if so, it has 3 initial states
    Long userId = supercashRequestContext.getUserId();
    Long storeId = supercashRequestContext.getStoreId();

    AtomicReference<List<ParkinglotTicket>> userParkingTickets = new AtomicReference<>();

    // If the search is for a specific ticket number
    if (ticketNumber.isPresent()) {
      Long validTicketNumber = NumberUtil.stringIsLongWithException(ticketNumber.get(), "Numero Ticket");
      Optional<ParkinglotTicket> currentTicketStatus = parkinglotTicketRepository.findByTicketNumberAndUserIdAndStoreId(validTicketNumber, userId, storeId);
      currentTicketStatus.ifPresent( ticketStatus -> {
        userParkingTickets.set(Arrays.asList(ticketStatus));
      });

    } else if (pageOffset.isPresent() || pageLimit.isPresent()) {
      // Define the paging
      int offset = pageOffset.isPresent() ? pageOffset.get() : 0;
      int limit = pageLimit.isPresent() ? pageLimit.get() : 10;

      // Query for the tickets
      PageRequest pageRequest = JpaUtil.makePageRequest(offset, limit, Sort.by("createdAt").descending());
      LOG.debug("Retrieving the tickets for the userId={} storeId={} from pageStart={} with pageLimit={}",
              userId, storeId, offset, limit);
      Optional<Page<ParkinglotTicket>> allTickets = parkinglotTicketRepository.findAllByUserIdAndStoreId(
              userId, storeId, pageRequest);

      // If present, add them to the result
      allTickets.ifPresent( ticketPages -> {
        userParkingTickets.set(ticketPages.getContent());
      });

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
        userParkingTickets.set(ticketsByUserAndDateSearch.get());
      }

    } else {
      LOG.debug("Retrieving the latest 10 tickets for the userId={} storeId={}", userId, storeId);
      // Just a list of all tickets based on the userId
      Optional<List<ParkinglotTicket>> last10Tickets = parkinglotTicketRepository.findFirst10ByUserIdAndStoreIdOrderByCreatedAtDesc(
              userId, storeId);

      // Add them to the result if anything was returned
      last10Tickets.ifPresent( latest10Tickets -> {
        userParkingTickets.set(latest10Tickets);
      });
    }

    // Ticket already has the exit transition recorded
    return userParkingTickets.get();
  }
}