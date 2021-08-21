package cash.super_.platform.service.parkinglot.ticket.testing;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import cash.super_.platform.autoconfig.ParkingPlusServiceClientProperties;
import cash.super_.platform.client.parkingplus.model.RetornoPagamento;
import cash.super_.platform.error.parkinglot.SupercashInvalidValueException;
import cash.super_.platform.model.parkinglot.ParkingTicketAuthorizedPaymentStatus;
import cash.super_.platform.model.parkinglot.ParkingTicketState;
import cash.super_.platform.service.parkinglot.ticket.ParkingTicketsStateTransitionService;
import cash.super_.platform.util.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import cash.super_.platform.client.parkingplus.model.RetornoConsulta;
import cash.super_.platform.model.parkinglot.ParkingTicketStatus;

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
	private Map<String, ParkingTicketStatus> statusCache = new ConcurrentHashMap<>();
	/**
	 * The cache of the queries per ticket
	 */
	private Map<String, RetornoConsulta> queryResultsCache = new ConcurrentHashMap<>();
	/**
	 * The cache of the list of payments per ticket
	 */
	private Map<String, List<RetornoPagamento>> paymentsCache = new ConcurrentHashMap<>();
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
	/**
	 * When the grace period expires in minutes during tests
	 */
	public static final int MIN_GRACE_PERIOD_DURING_TESTING = 1;
	/**
	 * When the price expires during tests
	 */
	public static final int MIN_PRICE_CHANGE_IN_MINUTES = 1;

	private Timer stateChangeTimer = new Timer();
	private PriceUpdaterTask priceUpdater;
	private Optional<Integer> optionalGracePeriod = Optional.empty();
	private Optional<Integer> optionalNextPrice = Optional.empty();

	// To display in the output of controllers
	public static String gracePeriodTimestamp;
	public static String nextUpdateTimestamp;

	@Autowired
	protected ParkingPlusServiceClientProperties properties;

	public static void addTestingHeaders(Map<String, String> headers) {
		String nextGracePeriod = TestingParkingLotStatusInMemoryRepository.gracePeriodTimestamp;
		headers.put("X-Supercash-Test-Grace-Period-Timeout", nextGracePeriod);

		String nextPriceUpdate = TestingParkingLotStatusInMemoryRepository.nextUpdateTimestamp;
		headers.put("X-Supercash-Test-Price-Change-Timeout", nextPriceUpdate);

		headers.put("X-Supercash-Test", "true");
	}

	// https://www.quora.com/How-do-I-create-a-thread-which-runs-every-one-minute-in-Java/answer/Anand-Dwivedi-4
	private class PriceUpdaterTask extends TimerTask {

		private PriceUpdaterTask() {
			LOG.debug("Initializing the price updater task...");
		}

		public void run() {
			LOG.debug("Updating testing prices at specific schedule...");
			statusCache.values().forEach(ticketStatus -> {
				String ticketNumber = ticketStatus.getStatus().getNumeroTicket();
				// Always increment the price of the non-free tickets that need payment at every PRICE_CHANGE_IN_MINUTES minutes for testing
				if (!ALWAYS_FREE_TICKET_NUMBER.equals(ticketNumber)) {
					int priceBefore = ticketStatus.getStatus().getTarifa();
					int nextPrice = priceBefore + 2;
					ticketStatus.getStatus().setTarifa(nextPrice);
					ticketStatus.getStatus().setTarifaSemDesconto(nextPrice);
					LOG.debug("Updating prices for testing ticket={} from {} to {}", ticketNumber, priceBefore, nextPrice);
				}

				// let's update the status if it hasn't changed get status
				if (ticketStatus.getState() == ParkingTicketState.GRACE_PERIOD) {
					updateStatus(ticketNumber, ParkingTicketState.NOT_PAID);
				}

				// The ticket just left the parking lot... set the values similar to ticketExitedParkingLot
				if (ticketStatus.getState() == ParkingTicketState.PAID && NEEDS_PAYMENT_ONE_PAYMENT_LEAVES_LOT_TICKET_NUMBER.equals(ticketNumber)) {
					LOG.debug("Updating ticket={} state as exit", ticketNumber);
					ticketStatus.setState(ParkingTicketState.EXITED_ON_PAID);
					ticketStatus.getStatus().setTicketValido(false);
					ticketStatus.getStatus().setTarifa(-1);
					// set the message with the ticket exit status as a hack
					// See the transition for details
					ticketStatus.getStatus().setMensagem(ticketStatus.getState().toString());
					// no need to set tarifaPaga because it was paid
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
				ParkingTicketState.FREE
		);
    	Thread.sleep(250);

    	// Create the ticket to be paid
		saveTicketStatusRetrieval(
				createTicketRetrieval(NEEDS_PAYMENT_ONE_PAYMENT_LEAVES_LOT_TICKET_NUMBER, 400),
				ParkingTicketState.GRACE_PERIOD
		);
    	Thread.sleep(300);

    	// Create the ticket that needs extra payments
		saveTicketStatusRetrieval(
				createTicketRetrieval(ALWAYS_NEEDS_PAYMENT_TICKET_NUMBER, 700),
				ParkingTicketState.GRACE_PERIOD
		);

		LOG.debug("The current size of the cache is {}", statusCache.size());

		ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDateTime.now(),
				ZoneId.of(DateTimeUtil.TIMEZONE_AMERICA_SAO_PAULO));
		gracePeriodTimestamp = DateTimeUtil.getFormatted(zonedDateTime.plusHours(3).plusMinutes(getGracePeriodInMinutes()).toInstant().toEpochMilli());
		nextUpdateTimestamp = DateTimeUtil.getFormatted(zonedDateTime.plusHours(3).plusMinutes(getNextPriceInMinutes()).toInstant().toEpochMilli());

		stateChangeTimer = new Timer();
		priceUpdater = new PriceUpdaterTask();
		// Initialize the timer with a new instance of the price updater task
		long initialExecution = 1000 * 60 * getGracePeriodInMinutes();
		long priceChangesRate = 1000 * 60 * getNextPriceInMinutes();
		stateChangeTimer.scheduleAtFixedRate(priceUpdater, initialExecution, priceChangesRate);
    }

    public int getGracePeriodInMinutes() {
		return this.optionalGracePeriod.orElse(MIN_GRACE_PERIOD_DURING_TESTING);
	}

	public int getNextPriceInMinutes() {
		return this.optionalNextPrice.orElse(MIN_PRICE_CHANGE_IN_MINUTES);
	}

	/**
	 * Reset the testing tickets starte back to how they are bootstrapped
	 */
	public void resetTestTickets(Optional<Integer> gracePeriodMin, Optional<Integer> nextPriceInMin) {
		resetChangeRates(gracePeriodMin, nextPriceInMin);

		LOG.info("Resetting values with gracePeriodMin={} nextPriceInMin={}", this.getGracePeriodInMinutes(),
				this.getNextPriceInMinutes());

		try {
			bootstrap();
			LOG.debug("Finished resetting testing tickets");

		} catch (InterruptedException error) {
			LOG.error("Couldn't reset testing tickets requested transactionId={} userId={}: {}", error.getMessage());
			throw new IllegalStateException("Couldn't reset the state of the testing tickets: " + error.getMessage());
		}
	}

	private void resetChangeRates(Optional<Integer> gracePeriodMin, Optional<Integer> nextPriceInMin) {
		if (!gracePeriodMin.isPresent() && !nextPriceInMin.isPresent()) {
			LOG.debug("Both Grace Period and Next Price were provided... keeping defaults: grace={}, and nextPrice={}",
					this.getGracePeriodInMinutes(), this.getNextPriceInMinutes());
			return;
		}

		Integer tempGracePeriod = gracePeriodMin.get();
		Integer tempPriceRate = gracePeriodMin.get();

		if (tempGracePeriod <= 0 || tempPriceRate <= 0) {
			LOG.debug("Both Grace Period and Next Price were provided... keeping defaults: grace={}, and nextPrice={}",
					this.getGracePeriodInMinutes(), this.getNextPriceInMinutes());
			throw new IllegalArgumentException(String.format("Can't use negative numbers to reset the testing server. " +
					"grace=%s , nextPrice=%s", tempGracePeriod, tempPriceRate));
		}

		if (tempGracePeriod <= MIN_GRACE_PERIOD_DURING_TESTING && tempPriceRate <= MIN_PRICE_CHANGE_IN_MINUTES) {
			LOG.warn("Both Grace Period and Next Price are still the same or equal to the default... " +
							"Increase the values: grace={}, and nextPrice={}", tempGracePeriod, tempPriceRate);
			throw new IllegalArgumentException(String.format("Both Grace Period and Next Price are still the " +
					"same or equal to the default... Increase the values: grace=%s, and nextPrice=%s",
					tempGracePeriod, tempPriceRate, tempGracePeriod, tempPriceRate));
		}

		this.optionalGracePeriod = gracePeriodMin;
		this.optionalNextPrice = nextPriceInMin;

		// Removes the current execution of the price updater, if any is running
		stateChangeTimer.cancel();
	}

    private RetornoConsulta createTicketRetrieval(String ticketNumber, int price) {
    	LOG.debug("Creating ticket {} with price {}", ticketNumber, price);
    	// Create a free ticket
    	RetornoConsulta statusRetrieval = new RetornoConsulta();
    	statusRetrieval.setCnpjGaragem("12.200.135/0001-80");
    	statusRetrieval.setDataDeEntrada(DateTimeUtil.getNow());
    	statusRetrieval.setDataConsulta(DateTimeUtil.getNow());

    	// set data saida to 4 hrs after
    	// https://stackoverflow.com/questions/4348525/get-date-as-of-4-hours-ago/4348542#4348542
		if (price == 0) {
			// set to begin of time for free tickets
			statusRetrieval.setDataPermitidaSaida(0L);

		} else {
			// Set the max time to leave to 4 hours from now
			LocalDateTime fourHoursAhead = DateTimeUtil.getNowLocalDateTime().plusHours(4);
			statusRetrieval.setDataPermitidaSaida(DateTimeUtil.getMillis(fourHoursAhead));
		}

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
    
    private void saveTicketStatusRetrieval(RetornoConsulta statusRetrieval, ParkingTicketState state) {
    	LOG.debug("Saving testing sticket {}", statusRetrieval.getNumeroTicket());
		queryResultsCache.put(statusRetrieval.getNumeroTicket(), statusRetrieval);

		// Calculate the grace period
		LocalDateTime entryDateTime = DateTimeUtil.getLocalDateTime(statusRetrieval.getDataDeEntrada());
		LocalDateTime gracePeriodMaxTime = entryDateTime.plusMinutes(MIN_GRACE_PERIOD_DURING_TESTING);
		long gracePeriodMillis = DateTimeUtil.getMillis(gracePeriodMaxTime);

		// Save the status
        ParkingTicketStatus status = new ParkingTicketStatus(statusRetrieval, state, gracePeriodMillis);
		statusCache.put(statusRetrieval.getNumeroTicket(), status);
    }

    public boolean containsTicket(String ticketNumber) {
		return statusCache.containsKey(ticketNumber);
	}

	public ParkingTicketStatus getStatus(String ticketNumber) {
		if (containsTicket(ticketNumber)) {
			ParkingTicketStatus status = statusCache.get(ticketNumber);

			// The time that the client API bases the calculations
			status.getStatus().setDataConsulta(DateTimeUtil.getMillis(DateTimeUtil.convertToTimezone(DateTimeUtil.getNowLocalDateTime())));
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
		queryResult.setDataConsulta(DateTimeUtil.getNow());

		return queryResult;
	}

	/**
	 * Updates the current status with the supercash status computed
	 *
	 * @param ticketNumber
	 * @param parkingTicketState
	 * @return an instance of the ticket status.
	 */
	public ParkingTicketStatus updateStatus(String ticketNumber, ParkingTicketState parkingTicketState) {
		ParkingTicketStatus testingTicketStatus = this.getStatus(ticketNumber);
		if (parkingTicketState != null) {
			testingTicketStatus.setState(parkingTicketState);
		}
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
					testingTicketStatus.getStatus().setMensagem("TESTING TICKET, NEEDS MULTIPLE PAYMENTS NEVER EXISTS THE LOT!");
					break;
			}
		}

		// Update the value from grace period earlier than what's needed in 3 minutes
		// The calculate method used the production grace period, so no we just update it
		if (ParkingTicketState.GRACE_PERIOD == testingTicketStatus.getState()) {
			LocalDateTime entranceDateTime = DateTimeUtil.getLocalDateTime(testingTicketStatus.getStatus().getDataDeEntrada());
			LocalDateTime testingGracePeriod = entranceDateTime.plusMinutes(MIN_GRACE_PERIOD_DURING_TESTING);
			LOG.debug("The testing ticket {} is in grace period: entrance={} testingGracePeriod={}",
					ticketNumber, entranceDateTime, testingGracePeriod);

			// The the value of the grace period for the ticket
			if (testingTicketStatus.getGracePeriodMaxTime() == 0) {
				testingTicketStatus.setGracePeriodMaxTime(DateTimeUtil.getMillis(testingGracePeriod));
			}

			// We can now update if the current time is greater than the grace period
			LocalDateTime now = DateTimeUtil.getNowLocalDateTime();
			LocalDateTime gracePeriodMax = testingGracePeriod.minusSeconds(ParkingTicketsStateTransitionService.GRACE_PERIOD_MINUS_SECONDS_OFFSET);
			if (now.isAfter(gracePeriodMax)) {
				LOG.debug("The testing ticket {}'s grace period timedout so setting it to NOT_PAID: entrance={} testingGracePeriod={} now={}",
						ticketNumber, entranceDateTime, testingGracePeriod, now);
				testingTicketStatus.setState(ParkingTicketState.NOT_PAID);
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
		LocalDateTime exitDateTimeAfterPayment = DateTimeUtil.getNowLocalDateTime().plusMinutes(3);
		ticketStatus.getStatus().setDataPermitidaSaidaUltimoPagamento(DateTimeUtil.getMillis(exitDateTimeAfterPayment));

		// just a hack to make the prices to be the same to what it was submitted
		ticketStatus.getStatus().setTarifaPaga((int)paidAmount);
		ticketStatus.getStatus().setTarifa((int)paidAmount);
		ticketStatus.getStatus().setTarifaSemDesconto((int)paidAmount);

		// Update the state with the paid
		ticketStatus.setState(ParkingTicketState.PAID);

		// Create the fake payment done
		RetornoPagamento paymentDone = new RetornoPagamento();
		paymentDone.setDataHoraSaida(ticketStatus.getStatus().getDataPermitidaSaidaUltimoPagamento());
		paymentDone.setDataPagamento(DateTimeUtil.getNow());
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
