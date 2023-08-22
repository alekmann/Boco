package no.boco.backend.config;

import no.boco.backend.post.Post;
import no.boco.backend.rental.Rental;

import java.sql.Date;
import java.util.List;
import java.util.Set;

public class CustomH2 {
    /**
     * Method for calculating the distance between two points given their coordiantes.
     * @param lat1 the latitude of point 1
     * @param lat2 the latitude of point 2
     * @param lon1 the longitude of point 1
     * @param lon2 the longitude of point 2
     * @return the distance in meters.
     */
    public static double haversineDistance(double lat1, double lat2, double lon1, double lon2){
        double R = 6371.0710;
        double diffLat = (lat2 * (Math.PI/180))-(lat1 * (Math.PI/180));
        double diffLon = (lon2 - lon1) * ((Math.PI/180));

        return 2000 * R * Math.asin(Math.sqrt(Math.sin(diffLat/2)*Math.sin(diffLat/2)+Math.cos(lat1)*Math.cos(lat2)*Math.sin(diffLon/2)*Math.sin(diffLon/2)));
    }

}
