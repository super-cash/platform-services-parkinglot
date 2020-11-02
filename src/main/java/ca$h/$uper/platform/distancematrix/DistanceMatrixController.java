package ca$h.$uper.distancematrix;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.maps.DirectionsApi.RouteRestriction;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;

import io.swagger.annotations.ApiOperation;

@RestController
public class DistanceMatrixController {
	
	private static final Logger log = LoggerFactory.getLogger(DistanceMatrixApplication.class);

        // TODO: Change this with Application Properties as it cannot be used in config service auto-refresh	
	@Value("${google.gcp.distancematrix.credentials.apikey}")
	String distanceMatrixApiKey;
	
	// The name in swagger metadata is coming as "operationId":"distancematrixUsingPOST"
	// https://stackoverflow.com/questions/38821763/how-to-customize-the-value-of-operationid-generated-in-api-spec-with-swagger/59044919#59044919
	@ApiOperation(value = "", nickname = "/distancematrix")
	@PostMapping(
            value = "/distancematrix",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
	@ResponseBody
	public Map<String, Object> distancematrix(@RequestBody DistanceMatrixAddresses distanceMatrixAddresses) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		try {
			long distance_time[] = getDriveDist(distanceMatrixAddresses.getOriginAddress(), distanceMatrixAddresses.getDestinationAddress());
			result.put("distance", distance_time[0]);
			result.put("time", distance_time[1]);
			
			log.info("Distance: " + distance_time[0] + " meters");
			log.info("Time: " + distance_time[1] + " seconds");
			
			return result;
		} catch (Exception e) {
			log.error(e.getMessage());
			log.error("Error unpacking payload.");
			result.put("error", (long) 500);
			result.put("description", e.getMessage());
			return result;
		}
	}

	private long[] getDriveDist(String addrOne, String addrTwo) throws ApiException, InterruptedException, IOException {
		
		//set up key
	   	GeoApiContext distCalcer = new GeoApiContext.Builder()
			    .apiKey(distanceMatrixApiKey)
			    .build();

	   	DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(distCalcer); 
	   	DistanceMatrix result = req.origins(addrOne)
               .destinations(addrTwo)
               .mode(TravelMode.DRIVING)
               .avoid(RouteRestriction.TOLLS)
               .language("pt-BR")
               .await();
	     
		return new long[] {result.rows[0].elements[0].distance.inMeters, result.rows[0].elements[0].duration.inSeconds};
	}

}
