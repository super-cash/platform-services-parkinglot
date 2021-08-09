package cash.super_.platform.service.bootstrap;

import cash.super_.platform.autoconfig.ParkinglotServiceProperties;
import cash.super_.platform.service.parkinglot.model.Marketplace;
import cash.super_.platform.service.parkinglot.repository.MarketplaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * In-memory repository for testing tickets to be used by the app
 *
 * @author marcellodesales
 *
 */
@Component

// https://reflectoring.io/spring-boot-conditionals/ only loads if the property is defined
@ConditionalOnProperty(
		value=ParkinglotServiceProperties.PROPERTY_ROOT_PREFIX + ".bootstrapData",
		havingValue = "true",
		matchIfMissing = false)
public class MarketplaceRepositoryBootstrap extends  AbstractRepositoryBootstrap {

	@Autowired
	protected MarketplaceRepository marketplaceRepository;

	@PostConstruct
	private void setupInitialData() {
		LOG.info("Bootstrapping marketplace data into the database at {}", dataSourceProperties.getUrl());

		// TODO: Update with a better location for where the data come from...
		// Load them from Json in the resources
		Set<Marketplace> instances = new HashSet<>(Arrays.asList(
				new Marketplace(1L, "Supercash", "https://super.cash"),
				new Marketplace(6115L, "Maceió Shopping", "https://maceioshopping.com")
		));

		LOG.info("Starting to Bootstrap of Marketplace Repository");

		instances.forEach( instance -> {
			// Long id, String name, String url, String codeName
			try {
				marketplaceRepository.save(instance);
				LOG.info("Saved instance {}", instance);

			} catch (Exception errorSaving) {
				LOG.error("Error Saveing instance {}: {}", instance, errorSaving);
			}
		});

		LOG.info("Finished Bootstrapping of Marketplace Repository");
	}

}
