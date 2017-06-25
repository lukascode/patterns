import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.util.*;

class Database extends AbstractTableModel {
    private List<TableHeader> headers;
    private List<List<TableData>> data;
    
    public Database() {
        headers = new ArrayList<TableHeader>();
        data = new ArrayList<List<TableData>>();
    }
    public void addRow() {
        List<TableData> row = new ArrayList<TableData>();
        for(TableHeader col:headers)
            row.add(col.createTableData()); // wywo³anie metody fabrykuj¹cej
        data.add(row);
        fireTableStructureChanged();
    }
    public void addCol(TableHeader type) {
        headers.add(type);
        for(List<TableData> row:data)
            row.add(type.createTableData()); // wywo³anie metody fabrykuj¹cej
        fireTableStructureChanged();
    }

    public int getRowCount() { return data.size(); }
    public int getColumnCount() { return headers.size(); }
    public String getColumnName(int column) {
        return headers.get(column).toString();
    }
    public Object getValueAt(int row, int column) {
        return data.get(row).get(column);
    }
}

interface TableData {
    final static Random rnd = new Random();
     String toString();
}

class TableDataInt implements TableData
{
    private int data;
    public TableDataInt() { data = rnd.nextInt(100); }
    public String toString() { return Integer.toString(data); }
}

//------------------------------------------------------------------
class TableDataDouble implements TableData
{
	private double data;
	public TableDataDouble() { data = rnd.nextDouble(); }
	public String toString() { return Double.toString(data); }
}

class TableDataChar implements TableData 
{
	private char data;
	public TableDataChar() { data = (char) (rnd.nextInt(26) + 65); }
	public String toString() { return Character.toString(data); }
}

class TableDataBoolean implements TableData 
{
	private boolean data;
	public TableDataBoolean() { data = rnd.nextBoolean(); }
	public String toString() { return Boolean.toString(data); }
	
}
//-----------------------------------------------------------------

abstract class TableHeader
{
    private String type;
    public TableHeader(String type) { this.type = type; }
    public String toString() { return type; }
    abstract public TableData createTableData();
}

class TableHeaderInt extends TableHeader 
{
	public TableHeaderInt(String type) { super(type); }
	public TableData createTableData() { return new TableDataInt(); }	
}

class TableHeaderChar extends TableHeader
{
	public TableHeaderChar(String type) { super(type); }
	public TableData createTableData() { return new TableDataChar(); }
}

class TableHeaderDouble extends TableHeader
{
	public TableHeaderDouble(String type) { super(type); }
	public TableData createTableData() { return new TableDataDouble(); }
}

class TableHeaderBoolean extends TableHeader
{
	public TableHeaderBoolean(String type) { super(type); }
	public TableData createTableData() { return new TableDataBoolean(); }
}

public class program03 {
    public static void main(String[] args) {
        final JFrame frame = new JFrame("Baza danych");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        final Database database = new Database();
        
        JTable table = new JTable(database);
        JMenuBar bar = new JMenuBar();
        
        JButton row = new JButton("Dodaj Wiersz");
        JButton col = new JButton("Dodaj Kolumnê");
        
        bar.add(row);
        bar.add(col);
        
        frame.add(new JScrollPane(table));
        frame.setJMenuBar(bar);
        
        frame.pack();
        frame.setVisible(true);
        
        row.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ev)
            {
                database.addRow();
            }
        });
        col.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ev)
            {
                Object option = JOptionPane.showInputDialog(
                    frame,
                    "Podaj typ kolumny",
                    "Dodaj Kolumnê",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new TableHeader[] {
                        new TableHeaderInt("INT"),
                        new TableHeaderDouble("DOUBLE"),
                        new TableHeaderChar("CHAR"),
                        new TableHeaderBoolean("BOOLEAN"),
                    }, null);
                if(option == null)
                    return;
                database.addCol((TableHeader)option);
            }
        });
    }
}