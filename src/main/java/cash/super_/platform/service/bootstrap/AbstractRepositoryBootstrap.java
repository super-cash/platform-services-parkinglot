package cash.super_.platform.service.bootstrap;

import cash.super_.platform.autoconfig.ParkinglotServiceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Component;

/**
 * Updater for the instances
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
public abstract class AbstractRepositoryBootstrap {

	protected static final Logger LOG = LoggerFactory.getLogger(AbstractRepositoryBootstrap.class);

	@Autowired
	protected ParkinglotServiceProperties properties;

	@Autowired
 	protected DataSourceProperties dataSourceProperties;

}
