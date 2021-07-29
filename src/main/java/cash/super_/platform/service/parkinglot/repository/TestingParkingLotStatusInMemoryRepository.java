package cash.super_.platform.service.parkinglot.repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import cash.super_.platform.autoconfig.ParkingPlusProperties;
import cash.super_.platform.client.parkingplus.model.RetornoPagamento;
import cash.super_.platform.error.supercash.SupercashInvalidValueException;
import cash.super_.platform.service.parkinglot.model.ParkingTicketAuthorizedPaymentStatus;
import cash.super_.platform.service.parkinglot.model.TicketState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
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
@Profile({"dev", "default"})
public class TestingParkingLotStatusInMemoryRepository {

	protected static final Logger LOG = LoggerFactory.getLogger(TestingParkingLotStatusInMemoryRepository.class);
	/**
	 * The cache of the ticket status
	 */
	private static final Map<String, ParkingTicketStatus> statusCache = new ConcurrentHashMap<>();
	/**
	 * The cache of the queries per ticket
	 */
	private static final Map<String, RetornoConsulta> queryResultsCache = new ConcurrentHashMap<>();
	/**
	 * The cache of the list of payments per ticket
	 */
	private static final Map<String, List<RetornoPagamento>> paymentsCache = new ConcurrentHashMap<>();
	/**
	 * The ticket number that never changes its state. It's always free!
	 */
	public static final String ALWAYS_FREE_TICKET_NUMBER = "112233445566";
	/**
	 * The ticket number whose price can be paid only once. After a while, it will be considered that the user left the parking lot
	 */
	public static final String NEEDS_PAYMENT_ONE_PAYMENT_LEAVES_LOT_TICKET_NUMBER = "010101010101";
	/**
	 * The ticket number whose price will always increase and the user can pay it multiple times without leaving the parking lot
	 */
	public static final String ALWAYS_NEEDS_PAYMENT_TICKET_NUMBER = "111111000000";

	@Autowired
	protected ParkingPlusProperties properties;

	// https://www.quora.com/How-do-I-create-a-thread-which-runs-every-one-minute-in-Java/answer/Anand-Dwivedi-4
	class PriceUpdaterTask extends TimerTask {

		public void run() {
			LOG.debug("Updating testing prices at specific schedule...");
			statusCache.values().forEach(ticketStatus -> {
				String ticketNumber = ticketStatus.getStatus().getNumeroTicket();
				// Always increment the price of the non-free tickets that need payment at every 3 minutes for testing
				if (!ALWAYS_FREE_TICKET_NUMBER.equals(ticketNumber)) {
					int priceBefore = ticketStatus.getStatus().getTarifa();
					int nextPrice = priceBefore + 2;
					ticketStatus.getStatus().setTarifa(nextPrice);
					ticketStatus.getStatus().setTarifaSemDesconto(nextPrice);
					LOG.debug("Updating prices for testing ticket={} from {} to {}", ticketNumber, priceBefore, nextPrice);
				}

			});
		}
	}

	@PostConstruct
    public void bootstrap() throws InterruptedException {
    	// Remove all
        statusCache.clear();
        queryResultsCache.clear();
        paymentsCache.clear();

    	// Create the free ticket
    	saveTicketStatusRetrieval(
    			createTicketRetrieval(ALWAYS_FREE_TICKET_NUMBER, 0),
				TicketState.FREE
		);
    	Thread.sleep(5000);

    	// Create the ticket to be paid
		saveTicketStatusRetrieval(
				createTicketRetrieval(NEEDS_PAYMENT_ONE_PAYMENT_LEAVES_LOT_TICKET_NUMBER, 400),
				TicketState.GRACE_PERIOD
		);
    	Thread.sleep(3500);

    	// Create the ticket that needs extra payments
		saveTicketStatusRetrieval(
				createTicketRetrieval(ALWAYS_NEEDS_PAYMENT_TICKET_NUMBER, 700),
				TicketState.GRACE_PERIOD
		);

		LOG.debug("The current size of the cache is {}", statusCache.size());

		Timer timer = new Timer();
		long threeMinutes = 1000 * 60 * 5;
		timer.schedule(new PriceUpdaterTask(), threeMinutes, threeMinutes);//3 Min
    }

    // https://stackoverflow.com/questions/23944370/how-to-get-milliseconds-from-localdatetime-in-java-8/23945792#23945792
	private static long getMillis(LocalDateTime dateTime) {
		return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	// https://stackoverflow.com/questions/23944370/how-to-get-milliseconds-from-localdatetime-in-java-8/23945792#23945792
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
    
    private void saveTicketStatusRetrieval(RetornoConsulta statusRetrieval, TicketState state) {
    	LOG.debug("Saving testing sticket {}", statusRetrieval.getNumeroTicket());
		queryResultsCache.put(statusRetrieval.getNumeroTicket(), statusRetrieval);

		LocalDateTime gracePeriodMaxTime = ParkingTicketStatus.calculateGracePeriod(statusRetrieval, properties.getGracePeriodInMinutes(), null);
		long gracePeriodMills = getMillis(gracePeriodMaxTime);
        ParkingTicketStatus status = new ParkingTicketStatus(statusRetrieval, state, gracePeriodMills);
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
	 * @return an instance of the ticket status.
	 */
	public ParkingTicketStatus updateStatus(String ticketNumber) {
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
		if (TicketState.GRACE_PERIOD == testingTicketStatus.getState()) {
			LocalDateTime entranceDateTime = fromMillisecondsToDateTime(testingTicketStatus.getStatus().getDataDeEntrada());
			LocalDateTime testingGracePeriod = entranceDateTime.plusMinutes(3);
			LOG.debug("The testing ticket {} is in grace period: entrance={} testingGracePeriod={}",
					ticketNumber, entranceDateTime, testingGracePeriod);

			// The the value of the grace period for the ticket
			if (testingTicketStatus.getGracePeriodMaxTime() == 0) {
				testingTicketStatus.setGracePeriodMaxTime(getMillis(testingGracePeriod));
			}

			// We can now update if the current time is greater than the grace period
			LocalDateTime now = LocalDateTime.now();
			if (now.isAfter(testingGracePeriod)) {
				LOG.debug("The testing ticket {}'s grace period timedout so setting it to NOT_PAID: entrance={} testingGracePeriod={} now={}",
						ticketNumber, entranceDateTime, testingGracePeriod, now);
				testingTicketStatus.setState(TicketState.NOT_PAID);
			}
		}
		return testingTicketStatus;
	}

	/**
	 * Just authenticate the test tickets
	 * @param ticketNumber
	 * @param paidAmount
	 * @return The authorized payment status for the testing ticket
	 */
	public ParkingTicketAuthorizedPaymentStatus authorizePayment(String ticketNumber, long paidAmount) {
		if (ticketNumber.equals(ALWAYS_FREE_TICKET_NUMBER)) {
			throw new SupercashInvalidValueException("Can't process a payment of a testing ticket that's always FREE");
		}

		// construct the ticket status for tickets that need payment
		ParkingTicketStatus ticketStatus = this.getStatus(ticketNumber);

		// record the payment on the ticket status and the exit time being 3 minutes later for testing
		LocalDateTime exitDateTimeAfterPayment = LocalDateTime.now().plusMinutes(3);
		ticketStatus.getStatus().setDataPermitidaSaidaUltimoPagamento(getMillis(exitDateTimeAfterPayment));

		// just a hack to make the prices to be the same to what it was submitted
		ticketStatus.getStatus().setTarifaPaga((int)paidAmount);
		ticketStatus.getStatus().setTarifa((int)paidAmount);
		ticketStatus.getStatus().setTarifaSemDesconto((int)paidAmount);

		// Update the state with the paid
		ticketStatus.setState(TicketState.PAID);

		// Create the fake payment done
		RetornoPagamento paymentDone = new RetornoPagamento();
		paymentDone.setDataHoraSaida(ticketStatus.getStatus().getDataPermitidaSaidaUltimoPagamento());
		paymentDone.setDataPagamento(getMillis(LocalDateTime.now()));
		paymentDone.setErrorCode(0);
		paymentDone.setMensagem("Pagamento efetuado com sucesso (ticket test)");
		paymentDone.setNumeroTicket(ticketNumber);
		paymentDone.setTicketPago(true);

		List<RetornoPagamento> paymentsMade = paymentsCache.get(ticketNumber);
		// Create a linked list of paymnets as the prices are paid in order
		if (paymentsMade == null) {
			paymentsMade = new LinkedList<>();
		}

		// Cache the payment made for the ticket
		paymentsMade.add(paymentDone);

		ParkingTicketAuthorizedPaymentStatus status = new ParkingTicketAuthorizedPaymentStatus();
		status.setStatus(paymentDone);

		// TODO Create a cache of the payments as well similar to what's implemented at cacheParkingTicketPayment
		return status;
	}

}
