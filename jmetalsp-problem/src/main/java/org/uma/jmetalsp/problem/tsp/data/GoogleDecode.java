package org.uma.jmetalsp.problem.tsp.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



public class GoogleDecode implements Serializable {
	public static final String DISTANCE_QUERY = "https://maps.googleapis.com/maps/api/distancematrix/json?";
    public static final String GOOGLE_KEY = "AIzaSyAmRX7TkZwlsyqY4EI8G57OVL_B0I6uRps";
    
    public static Integer getDistance(Coord o, Coord d) throws Exception {
        try {
            String json = readUrl(DISTANCE_QUERY + "origins=" + o.getX() + "," + o.getY() + "&destinations=" + d.getX() + "," + d.getY() /*+ "&key=" + GOOGLE_KEY*/);

            JsonObject root = new JsonParser().parse(json).getAsJsonObject();
            JsonObject rowsObject = root.getAsJsonArray("rows").get(0).getAsJsonObject();
            JsonArray distanceArray = rowsObject.getAsJsonArray("elements");
            int min_distance = Integer.MAX_VALUE;
            for (int i = 0; i < distanceArray.size(); i++) {
                JsonObject distanceObject = distanceArray.get(i).getAsJsonObject().getAsJsonObject("distance");
                int distance = distanceObject.getAsJsonPrimitive("value").getAsInt();
                if (distance < min_distance) {
                    min_distance = distance;
                }
            }
            return min_distance;
        } catch (Exception e) {
            return null;
        }
    }
    
    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read); 

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }
    
    public static List<Coord> decode(final String encodedPath) {
        int len = encodedPath.length();

        // For speed we preallocate to an upper bound on the final length, then
        // truncate the array before returning.
        final List<Coord> path = new ArrayList<Coord>();
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < len) {
        	try{
            int result = 1;
            int shift = 0;
            int b;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            result = 1;
            shift = 0;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            path.add(new Coord(lat * 1e-5, lng * 1e-5));
        	}catch(Exception ex){
        		
        	}
        }

        return path;
    }
    

}
