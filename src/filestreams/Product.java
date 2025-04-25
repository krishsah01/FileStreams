package filestreams;

import java.io.*;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    // ------------- fixed-length field sizes (chars) -------------
    public static final int NAME_LEN = 35;
    public static final int DESC_LEN = 75;
    public static final int ID_LEN   = 6;

    // one Java char = 2 bytes, so:
    public static final int RECORD_BYTES =
            2 * (NAME_LEN + DESC_LEN + ID_LEN) + Double.BYTES; // 232 bytes

    private String name, description, id;
    private double cost;

    // ---------- ctor ----------
    public Product(String name, String description, String id, double cost) {
        this.name        = fix(name, NAME_LEN);
        this.description = fix(description, DESC_LEN);
        this.id          = fix(id, ID_LEN);
        this.cost        = cost;
    }

    // ---------- random-access helpers ----------
    public void write(RandomAccessFile raf) throws IOException {
        writeFixed(raf, name, NAME_LEN);
        writeFixed(raf, description, DESC_LEN);
        writeFixed(raf, id, ID_LEN);
        raf.writeDouble(cost);
    }

    public static Product read(RandomAccessFile raf) throws IOException {
        String n   = readFixed(raf, NAME_LEN);
        String d   = readFixed(raf, DESC_LEN);
        String iid = readFixed(raf, ID_LEN);
        double c   = raf.readDouble();
        return new Product(n, d, iid, c);
    }

    public boolean matches(String partial) {
        return name.trim().toLowerCase()
                .contains(partial.toLowerCase().trim());
    }

    @Override public String toString() {
        return "%-35s  $%7.2f  %-6s  %s"
                .formatted(name.trim(), cost, id.trim(), description.trim());
    }

    // ---------- utility ----------
    private static String fix(String s, int len) {
        return (s == null ? "" : s).trim()
                .substring(0, Math.min(len, s.length()))
                + " ".repeat(Math.max(0, len - s.trim().length()));
    }
    private static void writeFixed(RandomAccessFile raf,
                                   String s, int len) throws IOException {
        for (int i = 0; i < len; i++)
            raf.writeChar(i < s.length() ? s.charAt(i) : ' ');
    }
    private static String readFixed(RandomAccessFile raf,
                                    int len) throws IOException {
        StringBuilder out = new StringBuilder(len);
        for (int i = 0; i < len; i++) out.append(raf.readChar());
        return out.toString();
    }
}
