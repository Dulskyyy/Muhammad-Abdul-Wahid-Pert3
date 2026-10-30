package courtculture1;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Main {

    public static void main(String[] args) {
        // List untuk menampung produk
        List<Product> productList = new ArrayList<>();
        double cartTotal = 0;

        // 1. Memasukkan Data Produk (Seed Data)
        productList.add(new Product("CC-001", "Nike Kobe 8 Protro", "Footwear", 2799000, 10));
        productList.add(new Product("CC-002", "Wilson NCAA Official Ball", "Basketballs", 850000, 25));
        productList.add(new Product("CC-003", "Court Culture Mesh Shorts", "Apparel", 350000, 50));

        // 2. Cetak Header ke Console NetBeans
        System.out.println("==========================================================================");
        System.out.println("                   COURT CULTURE - BASKETBALL STORE                      ");
        System.out.println("==========================================================================");
        System.out.printf("%-8s | %-28s | %-12s | %-14s | %-5s%n", "ID", "NAMA PRODUK", "KATEGORI", "HARGA", "STOK");
        System.out.println("--------------------------------------------------------------------------");

        // 3. Menampilkan Daftar Produk
        for (Product p : productList) {
            System.out.printf("%-8s | %-28s | %-12s | %-14s | %-5d%n",
                    p.getId(),
                    p.getName(),
                    p.getCategory(),
                    formatRupiah(p.getPrice()),
                    p.getStock());
        }

        System.out.println("==========================================================================");

        // 4. Simulasi Transaksi / Tambah ke Keranjang
        System.out.println("\n--- SIMULASI TRANSAKSI KERANJANG ---");
        
        // Membeli produk 1 (Nike Kobe 8)
        Product item1 = productList.get(0);
        item1.setStock(item1.getStock() - 1);
        cartTotal += item1.getPrice();  
        System.out.println("[+] Menambahkan ke keranjang: " + item1.getName());

        // Membeli produk 2 (Wilson Ball)
        Product item2 = productList.get(1);
        item2.setStock(item2.getStock() - 1);
        cartTotal += item2.getPrice();
        System.out.println("[+] Menambahkan ke keranjang: " + item2.getName());

        System.out.println("--------------------------------------------------------------------------");
        System.out.println("TOTAL BELANJA KERANJANG : " + formatRupiah(cartTotal));
        System.out.println("==========================================================================");
    }

    // Helper Format Rupiah
    private static String formatRupiah(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatter.format(amount);
    }
}