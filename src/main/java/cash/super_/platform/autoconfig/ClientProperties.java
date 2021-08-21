package cash.super_.platform.autoconfig;

import feign.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.TimeZone;

/**
 * The properties for Feign Clients used by Supercash in general.
 * This applies to all clients in the service.
 * TODO: Move to a shared library.
 */
@Validated
@Component
@ConfigurationProperties(prefix = "cash.super.platform.client")
public class ClientProperties {

    @NotNull
    private Logger.Level logLevel;

    @NotNull
    private int retryMaxAttempt;

    @NotNull
    private long retryInterval;

    @NotNull
    private long retryMaxPeriod;

    @NotNull
    private String timeZone;

    @PostConstruct
    public void init(){
        // Setting Spring Boot SetTimeZone configured according to the client settings
        TimeZone.setDefault(TimeZone.getTimeZone(getTimeZone()));
    }

    public Logger.Level getLogLevel() {
        return logLevel;
    }
    public void setLogLevel(Logger.Level logLevel) {
        this.logLevel = logLevel;
    }

    public int getRetryMaxAttempt() {
        return retryMaxAttempt;
    }
    public void setRetryMaxAttempt(int retryMaxAttempt) {
        this.retryMaxAttempt = retryMaxAttempt;
    }

    public long getRetryInterval() {
        return retryInterval;
    }
    public void setRetryInterval(long retryInterval) {
        this.retryInterval = retryInterval;
    }

    public long getRetryMaxPeriod() {
        return retryMaxPeriod;
    }
    public void setRetryMaxPeriod(long retryMaxPeriod) {
        this.retryMaxPeriod = retryMaxPeriod;
    }

    public String getTimeZone() { return timeZone; }
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public String toString() {
        return "ClientProperties{" +
                "logLevel=" + logLevel +
                '}';
    }
}
