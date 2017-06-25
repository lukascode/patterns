

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

// pojedynczy kafelek
class Tile {

    // schowek na wartość logiczną
    private boolean value = false;
    // kolory
    private static final Color on = new Color(0xffd700),
            off = new Color(0x1e90ff);

    // odczyt koloru
    public Color getColor() {
        return value ? on : off;
    }

    //zmiana koloru
    public void flip() {
        value = !value;
    }
}



//macierz kafelków
class Kafelki extends JPanel implements Iterable<Tile> {

    private Tile[][] matrix;
    private int tilesize;
    // kafelek podświetlony (myszką)
    private int hx = -1, hy = -1;
    
    //kafelek klikniety
    private int cx = -1, cy = -1;

    // inicjalizacja macierzy
    public Kafelki(int cols, int rows, int tilesize) { 
        this.setPreferredSize(new Dimension(cols * tilesize, rows * tilesize));
        this.tilesize = tilesize;
        matrix = new Tile[rows][cols];
        for (int i = 0; i < matrix.length; ++i) {
            for (int j = 0; j < matrix[i].length; ++j) {
                matrix[i][j] = new Tile();
            }
        }
    }

    // rysowanie macierzy (oraz jednego podświetlonego)
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < matrix.length; ++i) {
            for (int j = 0; j < matrix[i].length; ++j) {
                if (i == hy && j == hx) {
                    g.setColor(matrix[i][j].getColor().brighter());
                } else {
                    g.setColor(matrix[i][j].getColor());
                }
                g.fillRect(j * tilesize, i * tilesize + 1, tilesize - 1, tilesize - 1);
            }
        }
    }

    // podświetl
    public void highlight(int x, int y) {
        hx = x;
        hy = y;
        repaint();
    }
    //ustaw klikniety
    public void clicked(int x, int y) {
    	cx = x;
    	cy = y;
    }

    // trzy poniższe metody znikną w finalnej wersj
    /*
    public int getRows() {
        return matrix.length;
    }

    public int getCols() {
        return matrix[0].length;
    }

    public Tile getAt(int row, int col) {
        return matrix[row][col];
    }
    
    //iterator
    public Iterator<Tile> iterator() {
    	return new IteratorBasic();
    }
    */
    
    
    private class BasicIterator implements Iterator<Tile> {
    	
    	private int x, y;
    	public BasicIterator() {
    		x = cx;
    		y = cy;
    	}
		@Override
		public boolean hasNext() {
			if(y < matrix.length) 
				return true;
			return false;
		}

		@Override
		public Tile next() {
			Tile t = matrix[y][x];
			if(x >= matrix[0].length-1) { x = 0; ++y; }
			else ++x;
			return t;
		}
    	
    }
    
    private class RandomIterator implements Iterator<Tile> {
    	private int x, y;
    	private Point[] rsource;
    	private int rtrack;
    	private int rindex;
    	public RandomIterator() {
    		x = cx;
    		y = cy;
    		rsource = new Point[matrix.length*matrix[0].length];
    		rtrack = rsource.length;
    		fillRSource();
    	}
		@Override
		public boolean hasNext() {
			return rtrack > 0;
		}
		@Override
		public Tile next() {
			Tile t = matrix[rsource[rindex].y][rsource[rindex].x];
			swap(rindex, rtrack-1); --rtrack;
			if(rtrack > 0) rindex = new Random().nextInt(rtrack);
			return t;
		}
		
		private void fillRSource() {
			int k = 0;
			for(int i=0; i<matrix.length; ++i) {
				for(int j=0; j<matrix[0].length; ++j) {
					rsource[k++] = new Point(j, i);
					if(j == x && i == y) rindex = k;
				}
			}
		}
		
		private void swap(int i, int j) {
			Point p = rsource[i];
			rsource[i] = rsource[j];
			rsource[j] = p;
		}
    }
    
    private class SnakeIterator implements Iterator<Tile> {
    	private int x, y;
    	int dir;
    	public SnakeIterator() {
    		x = cx;
    		y = cy;
    		dir = 1;
    	}
    	
		@Override
		public boolean hasNext() {
			if(y < matrix.length) 
				return true;
			return false;
		}

		@Override
		public Tile next() {
			Tile t = matrix[y][x];
			if(dir == 1) {
				if(x >= matrix[0].length-1) { ++y; dir = -1; }
				else ++x;
			}
			else {
				if(x == 0) { ++y; dir = 1; }
				else --x;
			}
			return t;
		}
    }
    
    private class SpiralIterator implements Iterator<Tile> {
    	
    	private int x,y;
    	private int k;
    	private int dir;
    	boolean flag;
    	private int counter;
    	
    	public SpiralIterator() {
    		x = cx;
    		y = cy;
    		k = 1;
    		counter = k;
    		dir = -1;
    		flag = true; //y step first
     	}
		@Override
		public boolean hasNext() {
			return (x >= 0 && x < matrix[0].length) && (y >= 0 && y < matrix.length);
		}

		@Override
		public Tile next() {
			Tile t = matrix[y][x];
			if(flag) { 
				y += dir;
				counter--;
				if(counter == 0) {
					flag = !flag;
					counter = k;
				}
			} 
			else {
				x += dir;
				counter--;
				if(counter == 0) {
					dir = -dir;
					counter = ++k;
					flag = !flag;
				}
			}
			return t;
		}
    	
    }
    
	@Override
	public Iterator<Tile> iterator() {
		switch(new Random().nextInt(4)) {
			case 0: return new BasicIterator();
			case 1:	return new RandomIterator();
			case 2: return new SpiralIterator();
			case 3: return new SnakeIterator();
		}
		return new BasicIterator();
	}
    
}

// ten wątek nie wykorzystuje iteratora
class Watek implements Runnable {

    private Kafelki p;
    private int x, y;

    // x, y to początkowa pozycja do iteracji
    public Watek(Kafelki k, int x, int y) {
        this.p = k;
        this.x = x;
        this.y = y;
    }

    public void run() {
        // klasyczna podwójna pętla do iteracji
        // tutaj kontrolujemy kolejność odwiedzin
        // zostanie to zastąpione pętlą z użyciem iteratora
    	/*
        for (int i = y; i < p.getRows(); ++i) {
            int j;
            if (i == y) {
                j = x;
            } else {
                j = 0;
            }
            for (; j < p.getCols(); ++j) {
                // a w środku - obracamy, odświeżamy i czekamy
                p.getAt(i, j).flip();
                p.repaint();
                try {
                    Thread.currentThread().sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }*/
    	p.clicked(x, y);
    	Iterator<Tile> iter = p.iterator();
    	while(iter.hasNext()) {
    		Tile t = iter.next();
    		t.flip();
    		p.repaint();
    		try {
                 Thread.currentThread().sleep(100);
            } catch (InterruptedException e) {
                 e.printStackTrace();
            }
    	}
    }
}

public class Ztp08 {

    static final int TILESIZE = 40;

    public static void main(String[] args) {

        // konstruowanie okna
        JFrame frame = new JFrame("Iterator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final Kafelki kafelki = new Kafelki(16, 9, TILESIZE);
        frame.getContentPane().add(kafelki);
        frame.pack();
        frame.setVisible(true);

        // reakcja na kliknięcie uruchomienie wątku z iteracją
        kafelki.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX() / TILESIZE;
                int y = e.getY() / TILESIZE;
                new Thread(new Watek(kafelki, x, y)).start();
            }
        });
        // reakcja na ruch - podświetlenie wskazanego kafelka
        kafelki.addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                int x = e.getX() / TILESIZE;
                int y = e.getY() / TILESIZE;
                kafelki.highlight(x, y);
            }
        });
    }
}