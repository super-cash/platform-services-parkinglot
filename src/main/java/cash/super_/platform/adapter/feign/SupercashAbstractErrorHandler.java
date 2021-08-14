package cash.super_.platform.adapter.feign;

import feign.Response;
import java.util.List;
import cash.super_.platform.error.parkinglot.SupercashSimpleException;

public interface SupercashAbstractErrorHandler {

    public SupercashSimpleException handle(Response response, String responseBody);

    public List<String> getRetryableDestinationHosts();
}
