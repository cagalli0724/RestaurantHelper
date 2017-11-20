package external;
import java.util.List;

import entity.Item;

public interface externalAPI {
	public List<Item> search(double lat, double lon, String term);
}
