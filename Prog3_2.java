
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/cart")
public class Prog3_2 extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("user_id"));
        int productId = Integer.parseInt(request.getParameter("product_id"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
    	try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String jdbcUrl = "jdbc:mysql://localhost:3306/s";
        String dbUser = "root";
        String dbPassword = "";
        try {
			Connection c = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
            PreparedStatement ps = c.prepareStatement("INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?)");
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        response.sendRedirect("cart.html");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("user_id"));
        List<CartItem> cartItems = new ArrayList<>();
        
        try (Connection connection = DBUtil.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT p.id, p.name, p.price, c.quantity FROM products p JOIN cart c ON p.id = c.product_id WHERE c.user_id = ?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                CartItem item = new CartItem();
                item.setProductId(rs.getInt("id"));
                item.setProductName(rs.getString("name"));
                item.setProductPrice(rs.getDouble("price"));
                item.setQuantity(rs.getInt("quantity"));
                cartItems.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        request.setAttribute("cartItems", cartItems);
        request.getRequestDispatcher("cart.jsp").forward(request, response);
    }
}
