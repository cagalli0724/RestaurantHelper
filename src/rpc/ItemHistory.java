package rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

/**
 * Servlet implementation class ItemHistory
 */
@WebServlet("/history")
public class ItemHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ItemHistory() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String userName = "";
		HttpSession httpSession = request.getSession();
		if(httpSession.getAttribute("user_name") == null){
			response.sendRedirect("login.html");
		}else userName = (String) httpSession.getAttribute("user_name");
		JSONArray array = new JSONArray();

		DBConnection conn = DBConnectionFactory.getDBConnection();
		Set<Item> items = conn.getFavoriteItems(userName);
		conn.close();
		for (Item item : items) {
			JSONObject obj = item.toJSONObject();
			try {
				obj.append("favorite", true);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			array.put(obj);
		}
		RpcHelper.writeJsonArray(response, array);
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// get user_name from session
		String userName = "";
		HttpSession httpSession = request.getSession();
		if(httpSession.getAttribute("user_name") == null){
			response.sendRedirect("login.html");
		}else userName = (String) httpSession.getAttribute("user_name");
		
		try {
			// Get request body and convert to JSONObject
			JSONObject input = RpcHelper.readJsonObject(request);

			// Get item_id from input
			JSONArray array = (JSONArray) input.get("favorite");

			List<String> histories = new ArrayList<>();
			for (int i = 0; i < array.length(); i++) {
				String itemId = (String) array.get(i);
				histories.add(itemId);
			}
			// Add some save logic later
			DBConnection conn = DBConnectionFactory.getDBConnection();
			conn.setFavoriteItems(userName, histories);
			conn.close();

			// Return save result to client
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
	
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		
		try {
			String userName = "";
			HttpSession httpSession = request.getSession();
			if(httpSession.getAttribute("user_name") == null){
				RpcHelper.writeJsonObject(response, new JSONObject().put("result", "FAILURE"));
				return;
			}else userName = (String) httpSession.getAttribute("user_name");
			// Get request body and convert to JSONObject
			JSONObject input = RpcHelper.readJsonObject(request);


			// Get user_id and item_id from input
			JSONArray array = (JSONArray) input.get("favorite");

			List<String> histories = new ArrayList<>();
			for (int i = 0; i < array.length(); i++) {
				String itemId = (String) array.get(i);
				histories.add(itemId);
			}
			// Add some save logic later
			DBConnection conn = DBConnectionFactory.getDBConnection();
			conn.unsetFavoriteItems(userName, histories);
			conn.close();

			// Return save result to client
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
