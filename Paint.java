import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.undo.*;

public class Paint {
    
    //Frame
    JFrame frame;
    //Buttons
    JButton clear;
    JButton color;
    JButton erase;
    JButton brush;
    JButton rect;
    JButton oval;
    JButton fill;
    JButton line;
    //Menu
    JMenuItem menuNew;                                  //File>New
    JMenuItem menuOpen;                                 //File>Open
    JMenuItem menuSave;                                 //File>Save
    JMenuItem menuSaveAs;                               //File>Save As
    JMenuItem menuQuit;                                 //File>Quit
    //Misc stuff
    Color c = Color.black;                              //Foreground Color
    final DrawArea d;                                   //Paint Area
    File saveFile;                                      //Save File
    final UndoManager undoManager = new UndoManager();  //For undo
    
    //Handles tools
    ActionListener toolListener = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent ae) {
            if(ae.getSource() == clear) {
                d.clear();
            }else if(ae.getSource() == color) {
                c = JColorChooser.showDialog(null, "Choose a Color", c);
                d.setColor(c);
            }else if(ae.getSource() == erase) {
                d.setTool(Tool.ERASE);
            }else if(ae.getSource() == brush) {
                d.setTool(Tool.BRUSH);
            }else if(ae.getSource() == rect) {
                d.setTool(Tool.RECT);
            }else if(ae.getSource() == fill) {
                d.setTool(Tool.FILL);
            }else if(ae.getSource() == oval) {
                d.setTool(Tool.OVAL);
            }else if(ae.getSource() == line) {
                d.setTool(Tool.LINE);
            }
        }
    };
    
    //Handles menu
    ActionListener menuListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            if(ae.getSource() == menuNew) {
                //New window
                new Paint();
            }else if(ae.getSource() == menuOpen) {
                //Opens file for editing. Still very limited (window will not rescale)
                JFileChooser open = new JFileChooser();
                open.setAcceptAllFileFilterUsed(false);
                open.setDialogType(JFileChooser.SAVE_DIALOG);
                open.setDialogTitle("Open...");
                //BMP and jpg
                open.setFileFilter(new FileNameExtensionFilter("BMP & JPG Images", "bmp", "jpg"));
                if(open.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    saveFile = open.getSelectedFile();
                    open();
                }
            }else if(ae.getSource() == menuSave) {
                //checks if file exists, if it doesn't save as...
                if(saveFile == null) {
                    saveAs();
                }else {
                    save();
                }
            }else if(ae.getSource() == menuSaveAs) {
                //calls saveAs()
                saveAs();
            }else if(ae.getSource() == menuQuit) {
                //closes current window, will terminate if last window
                frame.dispose();
            }
        }
        
        //Open
        public void open() {
            try {
                d.setImage(ImageIO.read(saveFile));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Error Writing to File!", "Error!", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        //Save as
        public void saveAs() {
            //Make a file chooser
            JFileChooser save = new JFileChooser();
            save.setAcceptAllFileFilterUsed(false);
            save.setDialogType(JFileChooser.SAVE_DIALOG);
            save.setDialogTitle("Save As...");
            //BMP only, could also do JPG
            save.setFileFilter(new FileNameExtensionFilter("BMP Images", "bmp"));
            if(save.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                saveFile = save.getSelectedFile();
                save();
            }
        }
        
        public void save() {
            //Writes to file
            try {
                //Checking extension
                if(saveFile.getName().endsWith(".")) saveFile = new File(saveFile.getPath()+"bmp");
                else if(!saveFile.getName().endsWith(".bmp")) saveFile = new File(saveFile.getPath()+".bmp");
                //Writes to file
                ImageIO.write(d.getBufferedImage(), "BMP", saveFile);
            }catch(IOException e) {
                //Tells you something went wrong. RIP.
                JOptionPane.showMessageDialog(frame, "Error Writing to File!", "Error!", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        
    };
    
    //Creates GUI
    public Paint() {
        frame = new JFrame("Paint");
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());
        //Creating menu...
        JMenuBar menubar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        menuNew = new JMenuItem("New...");
        menuNew.addActionListener(menuListener);
        menuOpen = new JMenuItem("Open...");
        menuOpen.addActionListener(menuListener);
        menuSave = new JMenuItem("Save");
        menuSave.addActionListener(menuListener);
        menuSaveAs = new JMenuItem("Save as...");
        menuSaveAs.addActionListener(menuListener);;
        menuQuit = new JMenuItem("Quit");
        menuQuit.addActionListener(menuListener);
        fileMenu.add(menuNew);
        fileMenu.add(menuOpen);
        fileMenu.add(menuSave);
        fileMenu.add(menuSaveAs);
        fileMenu.add(menuQuit);
        menubar.add(fileMenu);
        content.add(menubar, BorderLayout.NORTH);
        //Menu added
        //Drawing area made
        d = new DrawArea();
        content.add(d, BorderLayout.CENTER);
        //Tools
        JPanel controls = new JPanel();
        clear = new JButton("Clear");
        clear.addActionListener(toolListener);
        color = new JButton("Pick Color");
        color.addActionListener(toolListener);
        erase = new JButton("Erase");
        erase.addActionListener(toolListener);
        brush = new JButton("Brush");
        brush.addActionListener(toolListener);
        rect = new JButton("Rectangle");
        rect.addActionListener(toolListener);
        fill = new JButton("Fill");
        fill.addActionListener(toolListener);
        oval = new JButton("Ellipse");
        oval.addActionListener(toolListener);
        line = new JButton("Line");
        line.addActionListener(toolListener);
        controls.setLayout(new GridLayout(0, 2));
        controls.add(brush);
        controls.add(erase);
        controls.add(clear);
        controls.add(color);
        controls.add(line);
        controls.add(oval);
        controls.add(rect);
        controls.add(fill);
        content.add(controls, BorderLayout.WEST);
        //Frame options
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setSize(600,400);
        frame.setResizable(false);
        frame.setVisible(true);
    }
    
    //Runs it
    public static void main(String[] args) {
       new Paint();
    }
    
}

//Defines shapes, for readibility
class Shapes {
    private Shapes(){}
    
    public static final int LINE = 0;
    public static final int RECT = 1;
    public static final int OVAL = 2;
}

//Tools
class Tool {
    
    private final int type;
    public Tool(int i){
        type = i;
    }
    
    //Applies the tool. O contains commands for fill
    public void apply(Graphics2D G, int oldX, int oldY, int curX, int curY, Object... O) {
        int dx = curX - oldX;
        int dy = curY - oldY;
        //Flip for the shapes
        if(type != Shapes.LINE && type != fill){
            if(dx < 0) {
                int temp = oldX;
                oldX = curX;
                curX = temp;
                dx *= -1;
            }
            if(dy < 0) {
                int temp = oldY;
                oldY = curY;
                curY = temp;
                dy *= -1;
            }
        }
        //Find out which tool we're using
        switch (type) {
            case Shapes.LINE:
                G.drawLine(oldX, oldY, curX, curY);
                break;
            case Shapes.OVAL:
                G.drawOval(oldX, oldY, dx, dy);
                break;
            case Shapes.RECT:
                G.drawRect(oldX, oldY, dx, dy);
                break;
            case clear:
                Color temp = G.getColor();
                G.setPaint(Color.white);
                G.fillRect(oldX, oldY, dx, dy);
                G.setPaint(temp);
                break;
            case fill:
                //Annoying BFS to fill shape
                BufferedImage bi = (BufferedImage)O[0];
                int orig = bi.getRGB(curX, curY);
                Queue<Point>Q = new LinkedList<>();
                Q.offer(new Point(curX, curY));
                Dimension dim = (Dimension)O[1];
                boolean vis[][]= new boolean[dim.width][dim.height];
                int[][]move = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
                while(!Q.isEmpty()) {
                    Point P = Q.poll();
                    if(!vis[P.x][P.y]&&bi.getRGB(P.x, P.y) == orig) {
                        G.fillRect(P.x, P.y, 1, 1);
                        vis[P.x][P.y] = true;
                        for(int[] i:move) {
                            if(P.x+i[0] >= 0 && P.x+i[0] < dim.width && P.y+i[1] >= 0 && P.y+i[1] < dim.height) {
                                Q.offer(new Point(P.x+i[0], P.y+i[1]));
                            }
                        }
                    }
                }
                G.fillRect(dx, dy, dy, brush);
            default:
                break;
        }
    }
    
    //Easier to understand
    public static final int clear = 3;
    public static final int brush = 4;
    public static final int erase = 5;
    public static final int fill = 6;
    
    //Used for comprehension
    public static final Tool LINE = new Tool(Shapes.LINE);
    public static final Tool OVAL = new Tool(Shapes.OVAL);
    public static final Tool RECT = new Tool(Shapes.RECT);
    public static final Tool CLEAR = new Tool(clear);
    public static final Tool BRUSH = new Tool(brush);
    public static final Tool ERASE = new Tool(erase);
    public static final Tool FILL = new Tool(fill);
}

//The actual paint area
class DrawArea extends JComponent {
    
    //Used to draw
    private Image image;
    private Graphics2D graphics;
    //current tool
    private Tool tool;
    
    //Co-ords
    int curX, curY, oldX, oldY;
    public DrawArea() {
        //Default tool is brush
        tool = Tool.BRUSH;
        setDoubleBuffered(false);
        
        //Gets x,y cord
        addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent me) {
                oldX = me.getX();
                oldY = me.getY();
            }
        });
        
        //For erase and brush
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent me) {
                if(tool == Tool.ERASE || tool == Tool.BRUSH) {
                    curX = me.getX();
                    curY = me.getY();
                    if(graphics != null) {
                        Color temp = graphics.getColor();
                        if(tool == Tool.ERASE)
                            graphics.setPaint(Color.white);
                        graphics.drawLine(oldX, oldY, curX, curY);
                        graphics.setPaint(temp);
                        repaint();
                        oldX = curX;
                        oldY = curY;
                    }
                }
            }
        });
        
        //For other tools
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent me) {
                curX = me.getX();
                curY = me.getY();
                //Overload is for the BFS, that was annoying
                tool.apply(graphics, oldX, oldY, curX, curY, image, getSize());
                repaint();
            }
        });
    }
    
    @Override
    //Draws the stuff
    protected void paintComponent(Graphics G) {
        if(image == null) {
            image = createImage(getSize().width, getSize().height);
            graphics = (Graphics2D) image.getGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            clear();
        }
        G.drawImage(image, 0, 0, null);
    }
    
    //Clears drawing area
    public void clear() {
        Color tempC = graphics.getColor();
        graphics.setPaint(Color.white);
        graphics.fillRect(0, 0, getSize().width, getSize().height);
        graphics.setPaint(tempC);
        repaint();
    }
    
    //Sets color, used by the JColorChooser
    public void setColor(Color C) {
        graphics.setPaint(C);
    }
    
    //Changes tool, used by the ActionListener
    public void setTool(Tool T) {
        tool = T;
    }
    
    //For saving
    public BufferedImage getBufferedImage() {
        return (BufferedImage)image;
    }
    
    //For open
    public void setImage(Image i) {
        image = createImage(getSize().width, getSize().height);
        graphics = (Graphics2D) image.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        clear();
        graphics.drawImage(i, 0, 0, null);
    }
}
/**
 * The DrawArea() code is based off of this: http://www.ssaurel.com/blog/learn-how-to-make-a-swing-painting-and-drawing-application/
 * However, there have been multiple edits, for example, the tools, the color chooser, etc. The MouseListeners.
 * Nonetheless, this could not have been done without consulting the video
 */
