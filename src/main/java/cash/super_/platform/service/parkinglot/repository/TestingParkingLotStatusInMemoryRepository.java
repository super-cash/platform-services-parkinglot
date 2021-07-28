package cash.super_.platform.service.parkinglot.repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import cash.super_.platform.client.parkingplus.model.RetornoPagamento;
import cash.super_.platform.error.supercash.SupercashInvalidValueException;
import cash.super_.platform.service.parkinglot.model.ParkingTicketAuthorizedPaymentStatus;
import cash.super_.platform.service.parkinglot.model.SupercashTicketStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cash.super_.platform.client.parkingplus.model.RetornoConsulta;
import cash.super_.platform.service.parkinglot.model.ParkingTicketStatus;

/**
 * In-memory repository for testing tickets to be used by the app
 *
 * @author marcellodesales
 *
 */
@Component
public class TestingParkingLotStatusInMemoryRepository {

	protected static final Logger LOG = LoggerFactory.getLogger(TestingParkingLotStatusInMemoryRepository.class);

	private static final Map<String, ParkingTicketStatus> statusCache = new ConcurrentHashMap<>();
	private static final Map<String, RetornoConsulta> queryResultsCache = new ConcurrentHashMap<>();
	public static final String ALWAYS_FREE_TICKET_NUMBER = "112233445566";
	public static final String NEEDS_PAYMENT_ONE_PAYMENT_LEAVES_LOT_TICKET_NUMBER = "010101010101";
	public static final String ALWAYS_NEEDS_PAYMENT_TICKET_NUMBER = "111111000000";

	// https://www.quora.com/How-do-I-create-a-thread-which-runs-every-one-minute-in-Java/answer/Anand-Dwivedi-4
	class PriceUpdaterTask extends TimerTask {

		public void run() {
			LOG.debug("Updating testing prices at specific schedule...");
			statusCache.values().forEach(ticketStatus -> {
				String ticketNumber = ticketStatus.getStatus().getNumeroTicket();
				// Always increment the price of the non-free tickets that need payment at every 3 minutes for testing
				if (!ALWAYS_FREE_TICKET_NUMBER.equals(ticketNumber)) {
					int priceBefore = ticketStatus.getStatus().getTarifa();
					int pricePlus15Percent = priceBefore + (int) (priceBefore * 0.15);
					ticketStatus.getStatus().setTarifa(pricePlus15Percent);
					ticketStatus.getStatus().setTarifaSemDesconto(pricePlus15Percent);
					LOG.debug("Updating prices for testing ticket={} from {} to {}", ticketNumber, priceBefore, pricePlus15Percent);
				}
			});
		}
	}

	@PostConstruct
    private void bootstrap() throws InterruptedException {
    	// Remove all
    	statusCache.clear();

    	// Create the free ticket
    	saveTicketStatusRetrieval(
    			createTicketRetrieval(ALWAYS_FREE_TICKET_NUMBER, 0),
				SupercashTicketStatus.FREE
		);
    	Thread.sleep(5000);

    	// Create the ticket to be paid
		saveTicketStatusRetrieval(
				createTicketRetrieval(NEEDS_PAYMENT_ONE_PAYMENT_LEAVES_LOT_TICKET_NUMBER, 400),
				SupercashTicketStatus.GRACE_PERIOD
		);
    	Thread.sleep(3500);

    	// Create the ticket that needs extra payments
		saveTicketStatusRetrieval(
				createTicketRetrieval(ALWAYS_NEEDS_PAYMENT_TICKET_NUMBER, 700),
				SupercashTicketStatus.GRACE_PERIOD
		);

		LOG.debug("The current size of the cache is {}", statusCache.size());

		Timer timer = new Timer();
		long threeMinutes = 1000 * 60 * 3;
		timer.schedule(new PriceUpdaterTask(), threeMinutes, threeMinutes);//3 Min
    }

	private static long getMillis(LocalDateTime dateTime) {
		return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	private static LocalDateTime fromMillisecondsToDateTime(long time) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), TimeZone.getDefault().toZoneId());
	}

    private RetornoConsulta createTicketRetrieval(String ticketNumber, int price) {
    	LOG.debug("Creating ticket {} with price {}", ticketNumber, price);
    	// Create a free ticket
    	RetornoConsulta statusRetrieval = new RetornoConsulta();
    	statusRetrieval.setCnpjGaragem("12.200.135/0001-80");
    	statusRetrieval.setDataDeEntrada(getMillis(LocalDateTime.now()));

    	// set data saida to 4 hrs after
    	// https://stackoverflow.com/questions/4348525/get-date-as-of-4-hours-ago/4348542#4348542
		if (price == 0) {
			// set to begin of time for free tickets
			statusRetrieval.setDataPermitidaSaida(0L);

		} else {
			// Set the max time to leave to 4 hours from now
			LocalDateTime fourHoursAhead = LocalDateTime.now().plusHours(4);
			statusRetrieval.setDataPermitidaSaida(getMillis(fourHoursAhead));
		}

    	//freeTicket.setDataPermitidaSaidaUltimoPagamento(null);

    	// When the ticket loads, set the time
    	statusRetrieval.setErrorCode(0);
    	statusRetrieval.setGaragem("MACEIO SHOPPING");
    	statusRetrieval.setIdGaragem(1L);
    	statusRetrieval.setMensagem("");
    	statusRetrieval.setNotas(new ArrayList<>());
    	statusRetrieval.setNumeroTicket(ticketNumber);
    	statusRetrieval.setPromocaoAtingida(false);
    	statusRetrieval.setPromocoesDisponiveis(false);
    	statusRetrieval.setSetor("ESTACIONAMENTO");
    	statusRetrieval.setTarifa(price);
    	statusRetrieval.setTarifaPaga(0);
    	statusRetrieval.setTarifaSemDesconto(price);
    	statusRetrieval.setTicketValido(true);
    	statusRetrieval.setValorDesconto(0);

    	return statusRetrieval;
    }
    
    private void saveTicketStatusRetrieval(RetornoConsulta statusRetrieval, SupercashTicketStatus state) {
    	LOG.debug("Saving testing sticket {}", statusRetrieval.getNumeroTicket());
		queryResultsCache.put(statusRetrieval.getNumeroTicket(), statusRetrieval);

    	ParkingTicketStatus status = new ParkingTicketStatus(statusRetrieval, state);
		statusCache.put(statusRetrieval.getNumeroTicket(), status);
    }

    public boolean containsTicket(String ticketNumber) {
    	return statusCache.containsKey(ticketNumber);
	}

	public ParkingTicketStatus getStatus(String ticketNumber) {
		if (containsTicket(ticketNumber)) {
			ParkingTicketStatus status = statusCache.get(ticketNumber);

			// The time that the client API bases the calculations
			status.getStatus().setDataConsulta(getMillis(LocalDateTime.now()));
			return status;
		}
		return null;
	}

	public RetornoConsulta getQueryResult(String ticketNumber) {
		if (!containsTicket(ticketNumber)) {
			return null;
		}

		// Let's update the ticket that's registered
		RetornoConsulta queryResult = queryResultsCache.get(ticketNumber);

		// The time that the client API bases the calculations
		queryResult.setDataConsulta(getMillis(LocalDateTime.now()));

		return queryResult;
	}

	/**
	 * Updates the current status with the supercash status computed
	 *
	 * @param ticketNumber
	 * @param supercashTicketStatus
	 * @return an instance of the ticket status.
	 */
	public ParkingTicketStatus updateStatus(String ticketNumber, SupercashTicketStatus supercashTicketStatus) {
		ParkingTicketStatus testingTicketStatus = this.getStatus(ticketNumber);

		// The ticket status is always free, testing weekends, holidays, etc
		// Make the whole transition of the status based on the type
		if (testingTicketStatus.getStatus().getMensagem() != null && testingTicketStatus.getStatus().getMensagem().isEmpty()) {
			switch (ticketNumber) {
				case ALWAYS_FREE_TICKET_NUMBER:
					testingTicketStatus.getStatus().setMensagem("TESTING TICKET, ALWAYS FREE!");
					break;

				case NEEDS_PAYMENT_ONE_PAYMENT_LEAVES_LOT_TICKET_NUMBER:
					testingTicketStatus.getStatus().setMensagem("TESTING TICKET, NEEDS SINGLE PAYMENT USER LEAVES THE LOT!");
					break;

				case ALWAYS_NEEDS_PAYMENT_TICKET_NUMBER:
					testingTicketStatus.getStatus().setMensagem("TESTING TICKET, NEEDS SINGLE PAYMENT!");
					break;
			}
		}

		// Update the value from grace period earlier than what's needed in 3 minutes
		if (SupercashTicketStatus.GRACE_PERIOD == testingTicketStatus.getSupercashTicketStatus()) {
			LocalDateTime entranceDateTime = fromMillisecondsToDateTime(testingTicketStatus.getStatus().getDataDeEntrada());
			LocalDateTime testingGracePeriod = entranceDateTime.plusMinutes(3);
			LOG.debug("The testing ticket {} is in grace period: entrance={} testingGracePeriod={}",
					ticketNumber, entranceDateTime, testingGracePeriod);

			// We can now update if the current time is greater than the grace period
			LocalDateTime now = LocalDateTime.now();
			if (now.isAfter(testingGracePeriod)) {
				LOG.debug("The testing ticket {}'s grace period timedout so setting it to NOT_PAID: entrance={} testingGracePeriod={} now={}",
						ticketNumber, entranceDateTime, testingGracePeriod, now);
				supercashTicketStatus = SupercashTicketStatus.NOT_PAID;
			}
		}

		testingTicketStatus.setSupercashTicketStatus(supercashTicketStatus);
		statusCache.put(ticketNumber, testingTicketStatus);
		return testingTicketStatus;
	}

}
