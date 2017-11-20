package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class UserService
 */
@WebServlet("/login")
public class UserLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserLogin() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		try {
			// Get request body and convert to JSONObject
			JSONObject input = RpcHelper.readJsonObject(request);

			String userName = input.getString("user_name");
			String userPassword = input.getString("user_password");

			boolean result = false;
			if(userName != null)
			{
				DBConnection conn = DBConnectionFactory.getDBConnection();
				result = conn.verifyLogin(userName, userPassword);
				conn.close();
				if(result) {
					HttpSession httpSession = request.getSession();
			        httpSession.setMaxInactiveInterval(180); // one week
			        httpSession.setAttribute("user_name", userName);
			        //response.setContentType("text/plain");  
			        //PrintWriter pw=response.getWriter(); 
			        //response.sendRedirect("./index.html");  
			        //pw.close(); 
					RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
					
			        return;
				}
			}
			if(result == false) {
				RpcHelper.writeJsonObject(response, new JSONObject().put("result", "FAILURE"));
			}
			// Add some save logic later

			// Return save result to client
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	
}
