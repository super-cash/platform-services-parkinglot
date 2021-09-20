package cash.super_.platform.adapter.actuator.healthcheck;

import cash.super_.platform.autoconfig.ParkinglotServiceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database health contributor
 * https://reflectoring.io/spring-boot-health-check/
 */
@Component("SupercashPostgresDB")
public class PostgresDatabaseHealthContributor implements HealthIndicator, HealthContributor {

    private static final Logger LOG = LoggerFactory.getLogger(PostgresDatabaseHealthContributor.class);

    @Autowired
    private ParkinglotServiceProperties parkinglotServiceProperties;

    @Autowired
    private DataSource dataSource;

    // https://docs.spring.io/spring-boot/docs/2.1.18.RELEASE/reference/html/howto-data-access.html#howto-configure-a-datasource
    // https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#application-properties.data.spring.datasource.url
    @Autowired
    private DataSourceProperties dataSourceProperties;

    /**
     * If the URL is connected
     */
    private boolean isQueryValid;

    public boolean isQueryValidOnConnection() {
        return this.isQueryValid;
    }

    public String getUrl() {
        return this.dataSourceProperties.getUrl();
    }

    @Override
    public Health health() {
        try (Connection conn = dataSource.getConnection()) {
            LOG.debug("Connected to {}", dataSourceProperties.getUrl());

            final String probeSql = parkinglotServiceProperties.getHealthcheckPostgresProbeQuery();
            LOG.debug("Checking the database with probe Query: {}", probeSql);
            Statement stmt = conn.createStatement();
            // SELECT 1 already added by Spring
            //  Verify if there's a need to see if
            // https://stackoverflow.com/questions/3668506/efficient-sql-test-query-or-validation-query-that-will-work-across-all-or-most/3670000#3670000
            // https://docs.oracle.com/javase/tutorial/jdbc/basics/processingsqlstatements.html
            stmt.execute(probeSql);
            isQueryValid = true;
            LOG.debug("Probe executed successfully: {}", probeSql);

        } catch (SQLException ex) {
            LOG.error("Connection to the database at {} failed: {}", dataSourceProperties.getUrl(), ex.getMessage());
            isQueryValid = false;
            return Health.outOfService().withException(ex).build();
        }
        return Health.up().build();
    }
}
