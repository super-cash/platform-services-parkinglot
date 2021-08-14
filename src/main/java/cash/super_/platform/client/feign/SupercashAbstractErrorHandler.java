package cash.super_.platform.client.feign;

import feign.Response;
import java.util.List;
import cash.super_.platform.error.supercash.SupercashSimpleException;

public interface SupercashAbstractErrorHandler {

    public SupercashSimpleException handle(Response response, String responseBody);

    public List<String> getRetryableDestinationHosts();
}
