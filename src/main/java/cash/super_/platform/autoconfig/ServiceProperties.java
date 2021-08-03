package cash.super_.platform.autoconfig;

import cash.super_.platform.utils.URLUtils;
import feign.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class ServiceProperties {

    protected static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ServiceProperties.class);

    @Autowired
    private PlatformConfigurationProperties platformConfigurationProperties;

    @NotBlank
    private String apiVersion;

    @NotBlank
    private String baseUrl;

    private Logger.Level clientLogLevel = Logger.Level.BASIC;

    @NotNull
    private int retryMaxAttempt;

    @NotNull
    private long retryInterval;

    @NotNull
    private long retryMaxPeriod;

    @NotNull
    private List<String> retryableDestinationHosts = new ArrayList<String>();

    public PlatformConfigurationProperties getPlatformConfigurationProperties() {
        return platformConfigurationProperties;
    }

    public void setPlatformConfigurationProperties(PlatformConfigurationProperties platformConfigurationProperties) {
        this.platformConfigurationProperties = platformConfigurationProperties;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String pagseguroApiVersion) {
        this.apiVersion = pagseguroApiVersion;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        URL url = URLUtils.validateURL(baseUrl, platformConfigurationProperties.getDefaultHeathCheckPort());
        this.baseUrl = url.toString();
    }

    public Logger.Level getClientLogLevel() {
        return clientLogLevel;
    }

    public void setClientLogLevel(Logger.Level clientLogLevel) {
        this.clientLogLevel = clientLogLevel;
    }

    public List<String> getRetryableDestinationHosts() {
        return retryableDestinationHosts;
    }

    public void setRetryableDestinationHosts(List<String> retryableDestinationHosts) {
        this.retryableDestinationHosts = retryableDestinationHosts;
    }

    public void addRetryableDestinationHosts(String retryableDestinationHost) {
        this.retryableDestinationHosts.add(retryableDestinationHost);
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

    @Override
    public String toString() {
        return "ServiceProperties{" +
                "platformConfigurationProperties=" + platformConfigurationProperties +
                ", apiVersion='" + apiVersion + '\'' +
                ", baseUrl='" + baseUrl + '\'' +
                ", clientLogLevel=" + clientLogLevel +
                ", retryMaxAttempt=" + retryMaxAttempt +
                ", retryInterval=" + retryInterval +
                ", retryMaxPeriod=" + retryMaxPeriod +
                ", retryableDestinationHosts=" + retryableDestinationHosts +
                '}';
    }
}