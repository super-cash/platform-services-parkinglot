package cash.super_.platform.service.parkinglot.ticket;

import cash.super_.platform.model.parkinglot.ParkingTicketState;
import cash.super_.platform.model.parkinglot.ParkinglotTicketStateTransition;
import cash.super_.platform.service.parkinglot.AbstractParkingLotProxyService;
import cash.super_.platform.model.parkinglot.ParkinglotTicket;
import cash.super_.platform.repository.ParkinglotTicketRepository;
import cash.super_.platform.util.DateTimeUtil;
import cash.super_.platform.util.FieldType;
import cash.super_.platform.util.JpaUtil;
import cash.super_.platform.util.NumberUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

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
  public List<ParkinglotTicket> retrieveTickets(Long parkinglotId, Optional<String> ticketNumber, Optional<Long> createdAt,
                                                Optional<Integer> pageOffset, Optional<Integer> pageLimit) {

    // Verify if the ticket is new and just got scanned, and if so, it has 3 initial states
    Long userId = supercashRequestContext.getUserId();
    Long storeId = supercashRequestContext.getStoreId();
    LOG.debug("Retrieving the tickets for the userId={} for parkinglotId={} exists!", userId, parkinglotId);

    final List<ParkinglotTicket> userParkingTickets = new ArrayList<>();

    // If the search is for a specific ticket number
    if (ticketNumber.isPresent()) {
      Long validTicketNumber = NumberUtil.stringIsLongWithException(FieldType.VALUE, ticketNumber.get(), "Numero Ticket");
      Optional<ParkinglotTicket> currentTicketStatus = parkinglotTicketRepository.findByTicketNumberAndUserIdAndStoreId(validTicketNumber, userId, storeId);
      currentTicketStatus.ifPresent( ticketStatus -> {
        userParkingTickets.addAll(Arrays.asList(ticketStatus));
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
          userParkingTickets.addAll(ticketPages.getContent());
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
          userParkingTickets.addAll(ticketsByUserAndDateSearch.get());
      }

    } else {
      LOG.debug("Retrieving the latest 10 tickets for the userId={} storeId={}", userId, storeId);
      // Just a list of all tickets based on the userId
      Optional<List<ParkinglotTicket>> last10Tickets = parkinglotTicketRepository.findFirst10ByUserIdAndStoreIdOrderByCreatedAtDesc(
              userId, storeId);

      // Add them to the result if anything was returned
      last10Tickets.ifPresent( latest10Tickets -> {
          userParkingTickets.addAll(latest10Tickets);
      });
    }

    // Ticket already has the exit transition recorded
    return userParkingTickets;
  }

  public void saveClone(ParkinglotTicket currentExitedTicket, Long storeId, Long userId) {
      // If they are different, we just create a copy of the entire state for the user, and add the info to the list
      ParkinglotTicket clonedTicket = new ParkinglotTicket();
      clonedTicket.setTicketNumber(currentExitedTicket.getTicketNumber());
      clonedTicket.setStoreId(storeId);
      clonedTicket.setUserId(userId);
      clonedTicket.setCreatedAt(currentExitedTicket.getCreatedAt());

      // Clone each transition
      Set<ParkinglotTicketStateTransition> clonedTransitions = new HashSet<>();
      currentExitedTicket.getStates().stream().forEach( transition -> {
          // Don't set the ID so that it creates a new one
          ParkinglotTicketStateTransition clonedTransition = new ParkinglotTicketStateTransition();
          clonedTransition.setTicketNumber(transition.getTicketNumber());
          clonedTransition.setStoreId(storeId);
          clonedTransition.setUserId(userId);
          clonedTransition.setParkinglotTicket(clonedTicket);
          // We can keep the same states as it was performed by another user
          clonedTransition.setState(transition.getState());
          if (ParkingTicketState.SCANNED.equals(transition.getState())) {
              clonedTransition.setDate(DateTimeUtil.getNow());

          } else {
              clonedTransition.setDate(transition.getDate());
          }
          clonedTransitions.add(clonedTransition);
      });
      clonedTicket.setStates(clonedTransitions);

      // As this user didn't pay for the ticket, just show them empty
      clonedTicket.setPayments(new HashSet<>());

      // Save a copy of the ticket scanned by someone else
      parkinglotTicketRepository.save(clonedTicket);
  }
}