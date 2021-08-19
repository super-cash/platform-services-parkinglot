package cash.super_.platform.autoconfig;

import cash.super_.platform.util.URLUtils;
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
    private PlatformAdaptorProperties platformConfigurationProperties;

    @NotBlank
    private String apiVersion;

    @NotBlank
    private String baseUrl;

    @NotNull
    private List<String> retryableDestinationHosts = new ArrayList<String>();

    public PlatformAdaptorProperties getPlatformConfigurationProperties() {
        return platformConfigurationProperties;
    }

    public void setPlatformConfigurationProperties(PlatformAdaptorProperties platformConfigurationProperties) {
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
        URL url = URLUtils.validateURL(baseUrl);
        this.baseUrl = url.toString();
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

    @Override
    public String toString() {
        return "ServiceProperties{" +
                "platformConfigurationProperties=" + platformConfigurationProperties +
                ", apiVersion='" + apiVersion + '\'' +
                ", baseUrl='" + baseUrl + '\'' +
                ", retryableDestinationHosts=" + retryableDestinationHosts +
                '}';
    }
}
