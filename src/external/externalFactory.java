package external;

public class externalFactory {
	private static final String DEFAULT_PIPELINE = "restaurant";

	// Start different APIs based on the pipeline.
	public static externalAPI getExternalAPI(String pipeline) {
		switch (pipeline) {
		case "restaurant":
                                           // return new YelpAPI(); 
			return new YelpAPI();
		case "job":
                                           // return new LinkedInAPI(); 
			return null;
		case "news":
                                           // return new NewYorkTimesAPI(); 
			return null;
		case "ticketmaster":
			return new TicketMasterAPI();
		default:
			throw new IllegalArgumentException("Invalid pipeline " + pipeline);
		}
	}

	public static externalAPI getExternalAPI() {
		return getExternalAPI(DEFAULT_PIPELINE);
	}
}

