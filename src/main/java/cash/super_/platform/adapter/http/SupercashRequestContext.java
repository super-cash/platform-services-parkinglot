package cash.super_.platform.adapter.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * The scope of all calls for Supercash that's populated by the SupercashSecurityInterceptor.
 * Based on https://dzone.com/articles/using-requestscope-with-your-api, https://gitlab.com/johnjvester/request-scope.
 */
public class SupercashRequestContext {

    protected static final Logger LOG = LoggerFactory.getLogger(SupercashRequestContext.class);

    /**
     * The transactionId requested
     */
    private String transactionId;

    /**
     * The market place requested
     */
    private Long marketplaceId;

    /**
     * The storeId requested
     */
    private Long storeId;

    /**
     * The userId requested
     */
    private Long userId;

    /**
     * The version of the application requested
     */
    private Double requestedAppVersion;

    /**
     * Testing flag
     */
    private List<String> modulesInTestingMode;

    public SupercashRequestContext() {}

    public String getTransactionId() {
        return transactionId;
    }
    public Long getMarketplaceId() {
        return marketplaceId;
    }
    public Long getStoreId() {
        return storeId;
    }
    public Long getUserId() {
        return userId;
    }
    public Double getRequestedAppVersion() {
        return requestedAppVersion;
    }
    public List<String> getModulesInTestingMode() {
        return modulesInTestingMode;
    }
    public boolean isModuleInTestMode(String moduleName) {
        return this.modulesInTestingMode.contains(moduleName);
    }

    public void setContext(String transactionId, Long marketplaceId, Long storeId, Long userId,
                           Double requestedAppVersion, List<String> modulesInTestingMode) {
        this.transactionId = transactionId;
        this.marketplaceId = marketplaceId;
        this.storeId = storeId;
        this.userId = userId;
        this.requestedAppVersion = requestedAppVersion;
        this.modulesInTestingMode = modulesInTestingMode;

        // All requests pass through here.
        LOG.info("Initializing supercash request context transactionId={} marketplaceId={} storeId={} userId={} " +
                        "requestedAppVersion={}, testing={}",
                transactionId, marketplaceId, storeId, userId, requestedAppVersion, this.modulesInTestingMode);
    }
}
