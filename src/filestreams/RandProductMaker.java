package filestreams;

import javax.swing.*;
import java.awt.*;
import java.io.RandomAccessFile;

public class RandProductMaker extends JFrame {
    private final JTextField tfName = new JTextField(35);
    private final JTextField tfDesc = new JTextField(35);
    private final JTextField tfID   = new JTextField(6);
    private final JTextField tfCost = new JTextField(10);
    private final JLabel     lblCnt = new JLabel("0");

    private static final String FILE = "products.dat";

    public RandProductMaker() {
        super("Random-Access Product Maker");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8,8));

        JPanel form = new JPanel(new GridLayout(5,2,6,6));
        form.add(new JLabel("Name (≤35):"));        form.add(tfName);
        form.add(new JLabel("Description (≤75):")); form.add(tfDesc);
        form.add(new JLabel("ID (≤6):"));           form.add(tfID);
        form.add(new JLabel("Cost:"));              form.add(tfCost);
        form.add(new JLabel("Record count:"));      form.add(lblCnt);

        JButton btnAdd  = new JButton("Add");
        JButton btnQuit = new JButton("Quit");

        btnAdd.addActionListener(e -> addRecord());
        btnQuit.addActionListener(e -> dispose());

        JPanel south = new JPanel(); south.add(btnAdd); south.add(btnQuit);

        add(form, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        updateCount();
    }

    private void addRecord() {
        try {
            String n = tfName.getText().trim();
            String d = tfDesc.getText().trim();
            String i = tfID.getText().trim();
            double c = Double.parseDouble(tfCost.getText().trim());

            if (n.isEmpty() || d.isEmpty() || i.isEmpty())
                throw new IllegalArgumentException("Fill every field!");

            Product p = new Product(i,n,d,c);
            try (RandomAccessFile raf = new RandomAccessFile(FILE,"rw")) {
                raf.seek(raf.length());      // append
                p.write(raf);
            }
            clear();
            updateCount();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clear() { tfName.setText(""); tfDesc.setText("");
        tfID.setText(""); tfCost.setText(""); }

    private void updateCount() {
        try (RandomAccessFile raf = new RandomAccessFile(FILE,"r")) {
            long recs = raf.length() / Product.RECORD_BYTES;
            lblCnt.setText(Long.toString(recs));
        } catch (Exception ignore) { lblCnt.setText("0"); }
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(RandProductMaker::new); }
}
