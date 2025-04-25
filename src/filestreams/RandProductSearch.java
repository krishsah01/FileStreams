package filestreams;

import javax.swing.*;
import java.awt.*;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class RandProductSearch extends JFrame {
    private final JTextField tfQuery = new JTextField(25);
    private final JTextArea  taOut   = new JTextArea(12,50);

    private static final String FILE = "products.dat";

    public RandProductSearch() {
        super("Product Search");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8,8));

        JButton btnGo   = new JButton("Search");
        btnGo.addActionListener(e -> search());

        JPanel north = new JPanel();
        north.add(new JLabel("Product name contains:"));
        north.add(tfQuery);
        north.add(btnGo);

        taOut.setEditable(false);
        add(north, BorderLayout.NORTH);
        add(new JScrollPane(taOut), BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void search() {
        taOut.setText("");
        String q = tfQuery.getText().trim();
        if (q.isEmpty()) return;

        List<Product> hits = new ArrayList<>();
        try (RandomAccessFile raf = new RandomAccessFile(FILE,"r")) {
            long recs = raf.length() / Product.RECORD_BYTES;
            for (long idx = 0; idx < recs; idx++) {
                raf.seek(idx * Product.RECORD_BYTES);
                Product p = Product.read(raf);
                if (p.matches(q)) hits.add(p);
            }
        } catch (Exception ex) {
            taOut.setText("Error: " + ex.getMessage());
            return;
        }

        if (hits.isEmpty()) {
            taOut.append("No matches.\n");
        } else {
            hits.forEach(p -> taOut.append(p + "\n"));
        }
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(RandProductSearch::new); }
}
