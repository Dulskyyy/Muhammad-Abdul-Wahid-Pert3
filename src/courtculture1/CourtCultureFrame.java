package courtculture1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CourtCultureFrame extends JFrame {

    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtName, txtPrice, txtStock;
    private JComboBox<String> cbCategory;
    private JLabel lblTotalCart;

    private List<Product> productList = new ArrayList<>();
    private double cartTotal = 0;

    public CourtCultureFrame() {
        setTitle("Court Culture - Basketball Equipment Store Management");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(20, 24, 30));
        JLabel lblTitle = new JLabel("COURT CULTURE APP");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);
        add(headerPanel, BorderLayout.NORTH);

        // Center Panel - Table
        String[] columnNames = {"ID", "Nama Produk", "Kategori", "Harga (IDR)", "Stok"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table read-only
            }
        };
        productTable = new JTable(tableModel);
        productTable.setRowHeight(25);
        add(new JScrollPane(productTable), BorderLayout.CENTER);

        // West Panel - Form Input Produk
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder(" Form Input Produk "));
        formPanel.setPreferredSize(new Dimension(320, 0));

        formPanel.add(new JLabel("ID Produk:"));
        txtId = new JTextField();
        formPanel.add(txtId);

        formPanel.add(new JLabel("Nama Produk:"));
        txtName = new JTextField();
        formPanel.add(txtName);

        formPanel.add(new JLabel("Kategori:"));
        String[] categories = {"Footwear", "Apparel", "Basketballs", "Accessories"};
        cbCategory = new JComboBox<>(categories);
        formPanel.add(cbCategory);

        formPanel.add(new JLabel("Harga (Rp):"));
        txtPrice = new JTextField();
        formPanel.add(txtPrice);

        formPanel.add(new JLabel("Stok:"));
        txtStock = new JTextField();
        formPanel.add(txtStock);

        JButton btnAdd = new JButton("Tambah Produk");
        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(this::addProduct);
        formPanel.add(btnAdd);

        JButton btnDelete = new JButton("Hapus Baris");
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(this::deleteProduct);
        formPanel.add(btnDelete);

        add(formPanel, BorderLayout.WEST);

        // South Panel - Cart Action & Summary
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnAddToCart = new JButton("Beli / Tambah ke Keranjang");
        btnAddToCart.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnAddToCart.addActionListener(this::addToCart);
        bottomPanel.add(btnAddToCart, BorderLayout.WEST);

        lblTotalCart = new JLabel("Total Keranjang: Rp 0");
        lblTotalCart.setFont(new Font("SansSerif", Font.BOLD, 16));
        bottomPanel.add(lblTotalCart, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        // Load Data dari Database MySQL XAMPP
        loadDataFromDatabase();
    }

    // 1. Fungsi Load Data dari MySQL XAMPP
    private void loadDataFromDatabase() {
        tableModel.setRowCount(0);
        productList.clear();

        String query = "SELECT * FROM products";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Product p = new Product(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getInt("stock")
                );
                productList.add(p);
                tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getCategory(),
                    formatRupiah(p.getPrice()),
                    p.getStock()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal konek/load data dari Database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 2. Fungsi Tambah Produk (INSERT ke MySQL)
    private void addProduct(ActionEvent e) {
        try {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            String category = (String) cbCategory.getSelectedItem();
            double price = Double.parseDouble(txtPrice.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());

            if (id.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID dan Nama produk wajib diisi!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Simpan ke Database XAMPP
            String query = "INSERT INTO products (id, name, category, price, stock) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = Database.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)) {

                ps.setString(1, id);
                ps.setString(2, name);
                ps.setString(3, category);
                ps.setDouble(4, price);
                ps.setInt(5, stock);
                ps.executeUpdate();

                // Refresh Data Tabel GUI
                loadDataFromDatabase();

                // Reset Field Input
                txtId.setText("");
                txtName.setText("");
                txtPrice.setText("");
                txtStock.setText("");

                JOptionPane.showMessageDialog(this, "Produk berhasil disimpan ke Database XAMPP!");

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Gagal simpan ke Database: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga dan Stok harus berupa angka valid!", "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    // 3. Fungsi Hapus Produk (DELETE dari MySQL)
    private void deleteProduct(ActionEvent e) {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            String productId = (String) tableModel.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus produk " + productId + " dari Database?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String query = "DELETE FROM products WHERE id = ?";
                try (Connection conn = Database.getConnection();
                     PreparedStatement ps = conn.prepareStatement(query)) {

                    ps.setString(1, productId);
                    ps.executeUpdate();

                    // Refresh Tabel
                    loadDataFromDatabase();
                    JOptionPane.showMessageDialog(this, "Produk berhasil dihapus dari Database!");

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Gagal hapus data: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih baris tabel yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }

    // 4. Fungsi Beli / Tambah ke Keranjang (UPDATE Stok di MySQL)
    private void addToCart(ActionEvent e) {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            Product p = productList.get(selectedRow);

            if (p.getStock() > 0) {
                int newStock = p.getStock() - 1;

                // Update Stok di Database
                String query = "UPDATE products SET stock = ? WHERE id = ?";
                try (Connection conn = Database.getConnection();
                     PreparedStatement ps = conn.prepareStatement(query)) {

                    ps.setInt(1, newStock);
                    ps.setString(2, p.getId());
                    ps.executeUpdate();

                    // Update Keranjang & Refresh
                    cartTotal += p.getPrice();
                    lblTotalCart.setText("Total Keranjang: " + formatRupiah(cartTotal));

                    loadDataFromDatabase();

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Gagal update stok: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Stok produk ini sudah habis!", "Stok Kosong", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih produk dari tabel dulu!", "Informasi", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String formatRupiah(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatter.format(amount);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CourtCultureFrame().setVisible(true);
        });
    }
}