package cash.super_.platform.autoconfig;

import feign.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Validated
@Component
@org.springframework.boot.context.properties.ConfigurationProperties(prefix = "cash.super.platform.client")
public class ClientProperties {

    protected static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ClientProperties.class);

    @NotNull
    private Logger.Level clientLogLevel;

    public Logger.Level getClientLogLevel() {
        return clientLogLevel;
    }

    public void setClientLogLevel(Logger.Level clientLogLevel) {
        this.clientLogLevel = clientLogLevel;
    }

    @Override
    public String toString() {
        return "ClientProperties{" +
                "clientLogLevel=" + clientLogLevel +
                '}';
    }
}
