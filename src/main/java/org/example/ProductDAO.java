package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private static final String CONNECTION_URL = "jdbc:sqlite:shoestore.db";
    public static void initDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS products ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " name TEXT NOT NULL,"
                + " price TEXT NOT NULL,"
                + " brand TEXT NOT NULL,"
                + " description TEXT,"
                + " image_path TEXT"
                + ");";

        try (Connection conn = DriverManager.getConnection(CONNECTION_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);

            String checkSQL = "SELECT COUNT(*) FROM products";
            try (ResultSet rs = stmt.executeQuery(checkSQL)) {
                if (rs.next() && rs.getInt(1) == 0) {
                    conn.setAutoCommit(false);

                    String insertSQL = "INSERT INTO products (name, price, brand, description, image_path) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

                        String[][] defaultData = {
                                {"4DFWD PULSE SHOES", "$160.00", "Adidas", "This product is excluded from all promotional discounts.", "./img1.png"},
                                {"FORUM MID SHOES", "$100.00", "Adidas", "This product is excluded.", "./img2.png"},
                                {"SUPERNOVA SHOES", "$150.00", "Adidas", "NMD City Stock 2 series.", "./img3.png"},
                                {"Adidas Originals", "$160.00", "Adidas", "NMD City Stock 2 classic look.", "./img4.png"},
                                {"Adidas Dark Knight", "$120.00", "Adidas", "NMD City Stock 2 running shoes.", "./img5.png"},
                                {"4DFWD PULSE ORANGE", "$160.00", "Adidas", "Special limited orange edition.", "./img6.png"}
                        };

                        for (String[] row : defaultData) {
                            pstmt.setString(1, row[0]);
                            pstmt.setString(2, row[1]);
                            pstmt.setString(3, row[2]);
                            pstmt.setString(4, row[3]);
                            pstmt.setString(5, row[4]);
                            pstmt.addBatch();
                        }
                        pstmt.executeBatch();
                        conn.commit();
                        System.out.println(">>> ĐÃ GHI THÀNH CÔNG DỮ LIỆU MẪU XUỐNG CSDL!");
                    } catch (Exception e) {
                        conn.rollback();
                        e.printStackTrace();
                    }
                } else {
                    System.out.println(">>> CSDL đã có sẵn " + rs.getInt(1) + " sản phẩm.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khởi tạo cấu trúc CSDL: " + e.getMessage());
        }
    }

    public static List<ShoeStoreApp.Product> getAllProducts() {
        List<ShoeStoreApp.Product> list = new ArrayList<>();
        String sql = "SELECT name, price, brand, description, image_path FROM products";

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy thư viện SQLite JDBC Driver!");
            return list;
        }

        try (Connection conn = DriverManager.getConnection(CONNECTION_URL);
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String name = rs.getString("name");
                String price = rs.getString("price");
                String brand = rs.getString("brand");
                String desc = rs.getString("description");
                String imagePath = rs.getString("image_path");

                list.add(new ShoeStoreApp.Product(name, price, brand, desc, imagePath));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi thực hiện truy vấn danh sách sản phẩm: " + e.getMessage());
        }
        return list;
    }
}
