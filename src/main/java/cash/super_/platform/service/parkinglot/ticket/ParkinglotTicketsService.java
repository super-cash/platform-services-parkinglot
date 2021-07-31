package cash.super_.platform.service.parkinglot.ticket;

import cash.super_.platform.service.parkinglot.AbstractParkingLotProxyService;
import cash.super_.platform.service.parkinglot.model.ParkinglotTicket;
import cash.super_.platform.service.parkinglot.repository.ParkinglotTicketRepository;
import cash.super_.platform.utils.IsNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Retrieve the list of tickets for a given user
 *
 * https://demonstracao.parkingplus.com.br/servicos/swagger-ui.html#!/servico-pagamento-ticket-2/getTicketUsingPOST
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
   * @param marketplaceId is the ticket number
   */
  public List<ParkinglotTicket> retrieveTickets(String marketplaceId, String userId, Optional<Long> createdAt, Optional<Long> createdAtOffset) {
    // Verify if the ticket is new and just got scanned, and if so, it has 3 initial states
    Long validUserId = IsNumber.stringIsLongWithException(userId, "Número Usuario");
    LOG.debug("Retrieving the tickets for the userId={}", userId);

    List<ParkinglotTicket> userParkingTickets = new ArrayList<>();
    if (createdAt.isPresent() && createdAtOffset.isPresent()) {
      LOG.debug("Retrieving the tickets for the userId={} between createdAt={} and createdAt={}", userId, createdAt.get(), createdAtOffset.get());
      Optional<List<ParkinglotTicket>> ticketsByUserBetweenDates = parkinglotTicketRepository.findByUserIdAndCreatedAtIsGreaterThanEqualAndCreatedAtIsLessThanEqual(
              Long.valueOf(userId), createdAt.get(), createdAtOffset.get()
      );
      if (ticketsByUserBetweenDates.isPresent()) {
        userParkingTickets.addAll(ticketsByUserBetweenDates.get());
      }

    } else if (createdAt.isPresent()) {
      long createdAtValue = createdAt.get();
      LOG.debug("Retrieving the tickets for the userId={} at createdAt={}", userId, createdAt.get());
      Optional<List<ParkinglotTicket>> ticketsByUserAndDateSearch = parkinglotTicketRepository.findByUserIdAndCreatedAt(
              validUserId, createdAtValue);
      if (ticketsByUserAndDateSearch.isPresent() && ticketsByUserAndDateSearch.get().size() > 0) {
        userParkingTickets.addAll(ticketsByUserAndDateSearch.get());
      }

    } else {
      LOG.debug("Retrieving the tickets for the userId={}", userId, createdAt.get());
      // Just a list of all tickets based on the userId
      Optional<List<ParkinglotTicket>> allicketSearch = parkinglotTicketRepository.findByUserId(validUserId);
      if (allicketSearch.isPresent()) {
        userParkingTickets.addAll(allicketSearch.get());
      }
    }

    // Ticket already has the exit transition recorded
    return userParkingTickets;
  }
}
