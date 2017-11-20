package rpc;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import algorithm.GeoRecommendation;
import entity.Item;
/**
 * Servlet implementation class RecommendItem
 */
@WebServlet("/recommendation")
public class RecommendItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RecommendItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		try {
			String userName = "";
			HttpSession httpSession = request.getSession();
			if(httpSession.getAttribute("user_name") == null){
				RpcHelper.writeJsonObject(response, new JSONObject().put("result", "FAILURE"));
				return;
			}else userName = (String) httpSession.getAttribute("user_name");
			double lat = Double.parseDouble(request.getParameter("lat"));
			double lon = Double.parseDouble(request.getParameter("lon"));
			GeoRecommendation recommendation = new GeoRecommendation();
			List<Item> items = recommendation.recommendItems(userName, lat, lon);

			JSONArray result = new JSONArray();
			for (Item item : items) {
				result.put(item.toJSONObject());
			}
			RpcHelper.writeJsonArray(response, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		


}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
