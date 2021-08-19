package cash.super_.platform.adapter.feign;

import feign.Response;
import java.util.List;

public interface SupercashAbstractErrorHandler {

    public SupercashSimpleException handle(Response response, String responseBody);

    public List<String> getRetryableDestinationHosts();
}
