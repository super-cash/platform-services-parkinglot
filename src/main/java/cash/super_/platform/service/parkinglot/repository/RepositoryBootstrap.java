package cash.super_.platform.service.parkinglot.repository;

import cash.super_.platform.autoconfig.ParkinglotServiceProperties;
import cash.super_.platform.client.parkingplus.model.RetornoConsulta;
import cash.super_.platform.client.parkingplus.model.RetornoPagamento;
import cash.super_.platform.error.supercash.SupercashInvalidValueException;
import cash.super_.platform.service.parkinglot.model.Marketplace;
import cash.super_.platform.service.parkinglot.model.ParkingTicketAuthorizedPaymentStatus;
import cash.super_.platform.service.parkinglot.model.ParkingTicketState;
import cash.super_.platform.service.parkinglot.model.ParkingTicketStatus;
import cash.super_.platform.service.parkinglot.ticket.ParkingTicketsStateTransitionService;
import cash.super_.platform.utils.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory repository for testing tickets to be used by the app
 *
 * @author marcellodesales
 *
 */
@Component
public class RepositoryBootstrap {

	@Autowired
	protected ParkinglotServiceProperties properties;

	@Autowired
	protected MarketplaceRepository marketplaceRepository;

	@PostConstruct
	private void setupInitialData() {
		Marketplace maceioShopping = new Marketplace(6115L, "Maceio Shopping",
				"https://maceioshopping.com","cash.super.maceioshopping");

		try {
			// Long id, String name, String url, String codeName
			marketplaceRepository.save(maceioShopping);

		} catch (Exception e) {

		}



	}

}
