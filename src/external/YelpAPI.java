package external;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;

public class YelpAPI implements externalAPI{
	
	private static final String API_HOST = "api.yelp.com";
	private static final String TOKEN_PATH = "/oauth2/token";
	private static final String SEARCH_PATH = "/v3/businesses/search";
	private static final String DEFAULT_TERM = "restaurants";  // just search restaurant.
	private static final String CLIENT_ID = "pkzaa-NjYW_utRwsTXKHZg";
	private static final String CLIENT_SECRET = "sYWvaCZLd96COEGu1y0zqIPIgSdtRNLNU0Kw8xUlbPNG7F12Rmo7V2EVHspOYh5W";
	private static final String GRANT_TYPE_NAME = "grant_type";
	private static final String DEFAULT_GRANT_TYPE = "client_credentials";
	private static final String CLIENT_ID_NAME = "client_id";
	private static final String CLIENT_SECRET_NAME = "client_secret";
	private static String access_token = "";
	private static final String ACCESS_TOKEN_NAME = "access_token";
	
	public YelpAPI()  {
		if(access_token != "") return;
		
		String url = "https://" + API_HOST + TOKEN_PATH;
				
		try {
			JSONObject info = new JSONObject();
			info.put(GRANT_TYPE_NAME, DEFAULT_GRANT_TYPE);
			info.put(CLIENT_ID_NAME, CLIENT_ID);
			info.put(CLIENT_SECRET_NAME, CLIENT_SECRET);

			// Create a HTTP connection between your Java application and TicketMaster based
			// on url
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			//connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestMethod("POST");

		     String param = "grant_type=client_credentials"
		         +"&client_id="+CLIENT_ID
		         +"&client_secret="+CLIENT_SECRET;
		      PrintWriter pr = new PrintWriter(connection.getOutputStream());
		      pr.print(param);
		      pr.close();

			// Now read response body to get events data
		      BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		      String str = "";
		      String line;
		      while((line = in.readLine()) != null){
		         str += line;
		      }
			in.close();

			JSONObject responseJson = new JSONObject(str.toString());
			access_token = (String) responseJson.get(ACCESS_TOKEN_NAME);
			System.out.println("Token : " + access_token);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private String getStringFieldOrNull(JSONObject event, String field) throws JSONException {
		return event.isNull(field) ? null : event.getString(field);
	}

	private double getNumericFieldOrNull(JSONObject event, String field) throws JSONException {
		return event.isNull(field) ? 0.0 : event.getDouble(field);
	}
	

	
	private Set<String> getCategories(JSONObject event) throws JSONException {
		Set<String> categories = new HashSet<>();
        if (!event.isNull("categories")) {
        		JSONArray classifications = (JSONArray) event.get("categories");
        		for (int j = 0; j < classifications.length(); j++) {
        			JSONObject classification = classifications.getJSONObject(j);
        			categories.add(getStringFieldOrNull(classification,"alias"));
        		}
        }

        return categories;


	}
	
    
    // return the first venue of an event object
	private String getVenue(JSONObject event) throws JSONException {
		if (!event.isNull("location")) {
			JSONObject location = (JSONObject) event.getJSONObject("location");
			JSONArray displayAddress = (JSONArray) location.getJSONArray("display_address");
			String str = "";
			for (int i = 0; i < displayAddress.length(); i++) {
				str += displayAddress.getString(i) + "\n";
			}
			return str;
		}
		return null;
	}
	
	private List<Item> getItemList(JSONArray events) throws JSONException {
		List<Item> itemList = new ArrayList<>();

		for (int i = 0; i < events.length(); i++) {
			JSONObject event = events.getJSONObject(i);
			ItemBuilder builder = new ItemBuilder();
			builder.setItemId(getStringFieldOrNull(event, "id"));
			builder.setName(getStringFieldOrNull(event, "name"));
			//builder.setDescription(getDescription(event));
			builder.setCategories(getCategories(event));
			builder.setImageUrl(getStringFieldOrNull(event,"image_url"));
			builder.setUrl(getStringFieldOrNull(event, "url"));

			builder.setAddress(getVenue(event));
			if (!event.isNull("coordinates")) {
				JSONObject location = event.getJSONObject("coordinates");
				builder.setLatitude(getNumericFieldOrNull(location, "latitude"));
				builder.setLongitude(getNumericFieldOrNull(location, "longitude"));
			}

			// Uses this builder pattern we can freely add fields.
			Item item = builder.build();
			itemList.add(item);
		}

		return itemList;
	}

	@Override
	public List<Item> search(double lat, double lon, String term) {
		// create a base url, based on API_HOST and SEARCH_PATH
		String url = "https://" + API_HOST + SEARCH_PATH;
		if (term == null) {
			term = DEFAULT_TERM;
		}
		// Encode term in url since it may contain special characters
		term = urlEncodeHelper(term);
		// Make your url query part like: "apikey=12345&geoPoint=abcd&keyword=music"
		String query = String.format("term=%s&latitude=%s&longitude=%s", term, lat, lon);
		try {
			// Create a HTTP connection between your Java application and TicketMaster based
			// on url
			HttpURLConnection connection = (HttpURLConnection) new URL(url + "?" + query).openConnection();
			// Set requrest method to GET
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Authorization", "Bearer "+ access_token);

			// Send request to TicketMaster and get response, response code could be
			// returned directly
			// response body is saved in InputStream of connection.
			int responseCode = connection.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url + "?" + query);
			System.out.println("Response Code : " + responseCode);

			// Now read response body to get events data
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			JSONObject responseJson = new JSONObject(response.toString());
			JSONArray array = (JSONArray) responseJson.get("businesses");
			return getItemList(array);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String urlEncodeHelper(String term) {
		try {
			term = java.net.URLEncoder.encode(term, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return term;
	}
	
	public static void main(String[] args)  {
		YelpAPI tmApi = new YelpAPI();
		tmApi.search(42.99,-76.13,null);
	}
}
