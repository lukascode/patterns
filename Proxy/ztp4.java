import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;


// data stored in database
interface Data {
    public int get(int idx);
    public void set(int idx, int value);
    public int size();
}

// simple data implementation
class RealData implements Data {
	
	private List<Integer> table;
	
	public RealData(int size) {
		table = new ArrayList<Integer>();
		for(int i=0; i<size; ++i) {
			table.add(i, new Integer(0));
		}
	}
	
	@Override
	public int get(int idx) {
		return table.get(idx).intValue();
	}

	@Override
	public void set(int idx, int value) {
		table.set(idx, value);
	}

	@Override
	public int size() {
		return table.size();
	}
	
}

class TableAdapter extends AbstractTableModel {
	
	private Data data;
	
	public TableAdapter() {
		data = null;
	}
	
	public void setData(Data data) {
		this.data = data;
		fireTableStructureChanged();
	}
	
	@Override
	public int getRowCount() {
		if(data != null) return data.size();
		return 0;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(columnIndex == 0) return rowIndex;
		return data.get(rowIndex);
	}
	
	public String getColumnName(int column) {
		return (column == 0)?"index":"value";
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if(columnIndex == 0) return false;
		return true;
	}
	
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		data.set(rowIndex, (int)aValue);
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	public Class<?> getColumnClass(int columnIndex) {
		return getValueAt(0, columnIndex).getClass();
	} 
	
}

class ProxyData extends Observable implements Data {
	
	private Data data;
	private int size;
	
	public ProxyData(int size) {
		data = null;
		this.size = size;
	}
	
	@Override
	public int get(int idx) {
		if(data == null) return 0;
		return data.get(idx);
	}

	@Override
	public void set(int idx, int value) {
		setChanged();
		notifyObservers();
		deleteObservers();
		if(data == null) data = new RealData(size);
		data.set(idx, value);
	}

	@Override
	public int size() {
		return size;
	}
	
}

class CopyProxyData extends Observable implements Observer,Data {
	
	private Data original, copy;
	
	public CopyProxyData(Data original) {
		this.original = original;
		((Observable)original).addObserver(this);
		copy = null;
	}
	
	protected void createACopy() {
		if(copy != null) return;
		copy = new RealData(original.size());
		for(int i=0; i<original.size(); ++i) {
			copy.set(i, original.get(i));
		}
		original = null;
	}
	
	@Override
	public int get(int idx) {
		if(copy == null) return original.get(idx);
		return copy.get(idx);
	}

	@Override
	public void set(int idx, int value) {
		setChanged();
		notifyObservers(); 
		deleteObservers(); 
		if(copy == null) createACopy();
		copy.set(idx, value);
	}

	@Override
	public int size() {
		if(copy == null) return original.size();
		return copy.size();
	}

	@Override
	public void update(Observable o, Object arg) {
		createACopy();
		System.out.println("Detached from orginal in CopyProxyData");
	}
	
}

// baza danych - kolekcja Data
class Baza extends AbstractListModel {
    private ArrayList<Data> ar = new ArrayList<Data>();
    
    
    public void add(Data tab){
        ar.add(tab);
        fireIntervalAdded(this, ar.size()-1, ar.size()-1);
    }

    public void remove(int idx){
        ar.remove(idx);
        fireIntervalRemoved(this, idx, idx);
    }

    public int getSize() {
        return ar.size();
    }

    public Object getElementAt(int index) {
        return ar.get(index);
    }
}


public class ztp4 {

    public static void main(String[] args) {

        final Baza dane = new Baza();

        final JFrame frame = new JFrame("Ztp4 Proxy pattern");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JSplitPane splitPane = new JSplitPane();

        final JList list = new JList(dane);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Tables: "));
        splitPane.setLeftComponent(scrollPane);
        
        final TableAdapter tableAdapter = new TableAdapter();
        JTable table = new JTable(tableAdapter);
        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Contents: "));
        splitPane.setRightComponent(scrollPane);

        frame.getContentPane().add(splitPane);

        JMenuBar bar = new JMenuBar();
        JButton add = new JButton("Add table");
        JButton del = new JButton("Delete table");
        JButton copy = new JButton("Copy table");
        bar.add(add);
        bar.add(del);
        bar.add(copy);

        frame.setJMenuBar(bar);

        frame.setSize(600, 450);
        frame.setVisible(true);

        splitPane.setDividerLocation(0.5);

        add.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String value = JOptionPane.showInputDialog(frame,
                        "Specify the size",
                        "Add",
                        JOptionPane.INFORMATION_MESSAGE);
                try{
                    int size = Integer.parseInt(value);
                    dane.add(new ProxyData(size));
                    //dane.add(new RealData(size));
                } catch(Exception ex) { };
            }
        });
        del.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int idx = list.getSelectedIndex();
                try{
                    dane.remove(idx);
                    tableAdapter.setData(null);
                } catch(Exception ex) { };
            }
        });
        
        
        copy.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		int idx = list.getSelectedIndex();
        		if(idx >= 0) {
        			Object original = dane.getElementAt(idx);
        			dane.add(new CopyProxyData((Data)original));
        		}
        	}
        });

        // refresh table
        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int idx = list.getSelectedIndex();
                if (idx >= 0) {
                	Object data = dane.getElementAt(idx);
                	tableAdapter.setData((Data)data);
                }
            }
        });
    }
}