package filestreams;

import java.io.*;
import java.util.Objects;


public class Product implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final int NAME_LEN = 35;
    public static final int DESC_LEN = 75;
    public static final int ID_LEN   = 6;

    public static final int RECORD_BYTES =
            2 * (NAME_LEN + DESC_LEN + ID_LEN) + Double.BYTES;

    private String ID;
    private String Name;
    private String Description;
    private Double Cost;

    public Product(String ID, String Name, String Description, Double Cost) {
        this.ID          = fix(ID,   ID_LEN);
        this.Name        = fix(Name, NAME_LEN);
        this.Description = fix(Description, DESC_LEN);
        this.Cost        = Cost;
    }

    public void write(RandomAccessFile raf) throws IOException {
        writeFixed(raf, ID,          ID_LEN);
        writeFixed(raf, Name,        NAME_LEN);
        writeFixed(raf, Description, DESC_LEN);
        raf.writeDouble(Cost);
    }

    public static Product read(RandomAccessFile raf) throws IOException {
        String id   = readFixed(raf, ID_LEN);
        String name = readFixed(raf, NAME_LEN);
        String desc = readFixed(raf, DESC_LEN);
        double cost = raf.readDouble();
        return new Product(id, name, desc, cost);
    }

    public boolean matches(String partial) {
        return Name.trim().toLowerCase()
                .contains(partial.toLowerCase().trim());
    }

    public String getID()          { return ID.trim(); }
    public String getName()        { return Name.trim(); }
    public String getDescription() { return Description.trim(); }
    public Double getCost()        { return Cost; }

    public void setName(String n)        { Name = fix(n, NAME_LEN); }
    public void setDescription(String d) { Description = fix(d, DESC_LEN); }
    public void setCost(Double c)        { Cost = c; }

    public String toCSV() {
        return "%s, %s, %s, %.2f"
                .formatted(getID(), getName(), getDescription(), Cost);
    }
    public String toJSON() {
        return """
               {
                 "ID": "%s",
                 "Name": "%s",
                 "Description": "%s",
                 "Cost": %.2f
               }""".formatted(getID(), getName(), getDescription(), Cost);
    }
    public String toXML() {
        return """
               <Product>
                 <ID>%s</ID>
                 <Name>%s</Name>
                 <Description>%s</Description>
                 <Cost>%.2f</Cost>
               </Product>""".formatted(getID(), getName(), getDescription(), Cost);
    }

    @Override public String toString() {
        return "%-35s  $%7.2f  %-6s  %s"
                .formatted(getName(), Cost, getID(), getDescription());
    }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product p)) return false;
        return Objects.equals(getID(), p.getID()) &&
                Objects.equals(getName(), p.getName()) &&
                Objects.equals(getDescription(), p.getDescription()) &&
                Objects.equals(Cost, p.Cost);
    }
    @Override public int hashCode() {
        return Objects.hash(getID(), getName(), getDescription(), Cost);
    }

    private static String fix(String s, int len) {
        s = (s == null ? "" : s).trim();
        return (s.length() > len ? s.substring(0, len) : s + " ".repeat(len - s.length()));
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
