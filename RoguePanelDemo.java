package WidlerSuite;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class RoguePanelDemo extends JFrame implements MouseListener, MouseMotionListener, ActionListener, KeyListener, WSConstants
{
    private JFrame testFrame;
    private JTextArea testTextArea;
    private RoguePanel panel;
    private JButton borderButton;
    private JButton dmButton;
    private JButton rnButton;
    private JButton scButton;
    private JButton sdButton;
    private JButton tpButton;
    private static int displayMode = RECT_MODE;
    private static boolean showBorders = false;
    private static boolean randomNonsense = false;
    private static boolean showFoV = false;
    private static boolean showDijkstra = false;
    public static final int COLUMNS = 40;
    public static final int ROWS = 40;
    private String[][] strMap;
    private boolean[][] passMap;
    private Color[][] bgMap;
    private Color[][] fgMap;
    private AStar aStar;
    private Coord atLoc;
    private ShadowFoV rectFoV;
    private ShadowFoVHex hexFoV;
    private DijkstraMap dijkstraMap;
    private boolean traceType = true;   // true is A*, false is StraightLine
    
    // test function
    public RoguePanelDemo()
    {
        super();
        setSize(1000, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        strMap = new String[COLUMNS][ROWS];
        passMap = new boolean[COLUMNS][ROWS];
        bgMap = new Color[COLUMNS][ROWS];
        fgMap = new Color[COLUMNS][ROWS];
        aStar = new AStar();
        
        atLoc = new Coord(4, 4);
        
        panel = new RoguePanel();
        panel.setColumnsAndRows(COLUMNS, ROWS);
        panel.setString(5, 10, "@");
        
        setTestMap();
        loadTestMap();
        
        add(panel);
        
        javax.swing.Timer timer = new javax.swing.Timer(1000 / 30, panel);
        
        testFrame = new JFrame();
        testFrame.setSize(300, 500);
        testFrame.setLocation(1100, 100);
        testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       // testFrame.setFocusable(false);
        testFrame.setLayout(new GridLayout(8, 1));
        
        JLabel label = new JLabel("Click on panel to add Unbound String.");
        label.setFocusable(false);
        testFrame.add(label);
        
        testTextArea = new JTextArea();
        testTextArea.setEditable(false);
        testTextArea.setFocusable(false);
        testFrame.add(testTextArea);
        
        dmButton = new JButton("Toggle Display Mode");
        dmButton.addActionListener(this);
        dmButton.setFocusable(false);
        testFrame.add(dmButton);
        
        borderButton = new JButton("Toggle Borders");
        borderButton.addActionListener(this);
        borderButton.setFocusable(false);
        testFrame.add(borderButton);
        
        rnButton = new JButton("Toggle Random Nonsense");
        rnButton.addActionListener(this);
        rnButton.setFocusable(false);
        testFrame.add(rnButton);
        
        scButton = new JButton("Toggle Shadowcasting");
        scButton.addActionListener(this);
        scButton.setFocusable(false);
        testFrame.add(scButton);
        
        sdButton = new JButton("Toggle Dijkstra");
        sdButton.addActionListener(this);
        sdButton.setFocusable(false);
        testFrame.add(sdButton);
        
        tpButton = new JButton("Trace: A*");
        tpButton.addActionListener(this);
        tpButton.setFocusable(false);
        testFrame.add(tpButton);
        
        testFrame.setVisible(true);
        
        setVisible(true);
        timer.start();
    }

    // update mouseLoc where needed. External listeners notified in the usual way.
    public void mouseMoved(MouseEvent me){dispLoc(me);}
    public void mouseDragged(MouseEvent me){}
    public void mouseEntered(MouseEvent me){}
    public void mouseExited(MouseEvent me){/*mouseLoc[0] = -1; mouseLoc[1] = -1; if(testMode) testClick(me);*/}
    public void mousePressed(MouseEvent me){dispLoc(me); testClick();}
    public void mouseReleased(MouseEvent me){}
    public void mouseClicked(MouseEvent me){}
    public void keyReleased(KeyEvent ke){}
    public void keyTyped(KeyEvent ke){}
    
    private void dispLoc(MouseEvent me)
    {
        String str = "Mouse pixel location: " + String.format("[%d, %d]\n", me.getX(), me.getY());
        str += "Mouse tile location: " + String.format("[%d, %d]\n", panel.mouseColumn(), panel.mouseRow());
        double x = MathTools.getHexX(panel.mouseColumn(), panel.mouseRow()) - MathTools.getHexX(atLoc);
        double y = panel.mouseRow() - atLoc.y;
        str += "Angle to mouseLoc: " + (MathTools.getAngle(x, y));
        testTextArea.setText(str);
        drawPath();
    }
    
    private void testClick()
    {
        UnboundString str = new UnboundString("UnboundString", Color.WHITE, panel.mouseColumn(), panel.mouseRow());
        str.setLifespan(30);
        str.setSpeed(0.0, -.1);
        str.setBackgroundBox(true);
        panel.add(str);
        for(int i = 0; i < 16; i++)
        {
            UnboundString star = new UnboundString("*", randomColor(), panel.mouseColumn(), panel.mouseRow());
            star.setLifespan(15);
            star.setSpeed((Math.random() * .4) - .2, (Math.random() * .4) - .2);
            panel.add(star);
        }
    }
    
    public void actionPerformed(ActionEvent ae)
    {
        if(ae.getSource() == dmButton)
        {
            if(displayMode == HEX_MODE)
                displayMode = RECT_MODE;
            else
                displayMode = HEX_MODE;
            panel.setDisplayMode(displayMode);
            aStar.setMode(displayMode);
            StraightLine.setMode(displayMode);
            loadTestMap();
        }
        
        if(ae.getSource() == borderButton)
        {
            showBorders = !showBorders;
            panel.showTileBorders(showBorders);
        }
        
        if(ae.getSource() == rnButton)
        {
            randomNonsense = !randomNonsense;
            if(randomNonsense)
                panel.randomize();
            else
                loadTestMap();
        }
        
        if(ae.getSource() == scButton)
        {
            showFoV = !showFoV;
            showDijkstra = false;
            loadTestMap();
        }
        
        if(ae.getSource() == sdButton)
        {
            showDijkstra = !showDijkstra;
            showFoV = false;
            loadTestMap();
        }
        
        if(ae.getSource() == tpButton)
        {
            traceType = !traceType;
            if(traceType)
                tpButton.setText("Trace: A*");
            else
                tpButton.setText("Trace: StraightLine");
        }
        hexFoV.calcFoV(atLoc.x, atLoc.y, 12);
        rectFoV.calcFoV(atLoc.x, atLoc.y, 12);
    }
    
    private void setTestMap()
    {
        strMap = new String[COLUMNS][ROWS];
        passMap = new boolean[COLUMNS][ROWS];
        bgMap = new Color[COLUMNS][ROWS];
        fgMap = new Color[COLUMNS][ROWS];
        
        for(int x = 0; x < COLUMNS; x++)
        for(int y = 0; y < ROWS; y++)
        {
            if(x < 2 || y < 2 || x > COLUMNS - 3 || y > ROWS - 3 || Math.random() < .15)
                passMap[x][y] = false;
            else
                passMap[x][y] = true;
            bgMap[x][y] = Color.BLACK;
            fgMap[x][y] = Color.WHITE;
        }
        
        // vertical wall
        int wallX = COLUMNS / 2;
        int wallY = (ROWS / 4) * 3;
        for(int y = wallY; y > ROWS / 4; y--)
            passMap[wallX][y] = false;
        // start and end locations
        passMap[4][4] = true;
        passMap[COLUMNS - 4][ROWS - 4] = true;
        for(int x = 0; x < COLUMNS; x++)
        for(int y = 0; y < ROWS; y++)
        {
            if(passMap[x][y])
                strMap[x][y] = ".";
            else
                strMap[x][y] = "#";
        }
        strMap[COLUMNS - 4][ROWS - 4] = ">";
        rectFoV = new ShadowFoV(passMap);
        hexFoV = new ShadowFoVHex(passMap);
        dijkstraMap = new DijkstraMap(passMap);
    }
    
    private void loadTestMap()
    {
        dijkstraMap = new DijkstraMap(passMap);
        dijkstraMap.setMode(displayMode);
        dijkstraMap.addGoal(atLoc.x, atLoc.y);
        dijkstraMap.process();
        for(int x = 0; x < COLUMNS; x++)
        for(int y = 0; y < ROWS; y++)
        {
            if(showFoV && displayMode == RECT_MODE)
            {
                if(rectFoV.canSee(x, y) && bgMap[x][y] == Color.BLACK)
                    panel.setTile(x, y, strMap[x][y], fgMap[x][y], Color.DARK_GRAY);
                else
                    panel.setTile(x, y, strMap[x][y], fgMap[x][y], bgMap[x][y]);
            }
            else if(showFoV && displayMode == HEX_MODE)
            {
                if(hexFoV.canSee(x, y) && bgMap[x][y] == Color.BLACK)
                    panel.setTile(x, y, strMap[x][y], fgMap[x][y], Color.DARK_GRAY);
                else
                    panel.setTile(x, y, strMap[x][y], fgMap[x][y], bgMap[x][y]);
            }
            else if(showDijkstra)
            {
                int val = dijkstraMap.getValue(x, y);
                if(val < 256 && bgMap[x][y] == Color.BLACK)
                    panel.setTile(x, y, strMap[x][y], fgMap[x][y], new Color(0, 255 - val, 0));
                else
                    panel.setTile(x, y, strMap[x][y], fgMap[x][y], bgMap[x][y]);
            }
            else
            {
                panel.setTile(x, y, strMap[x][y], fgMap[x][y], bgMap[x][y]);
            }
        }
        panel.setString(atLoc.x, atLoc.y, "@");
    }
    
    private void drawPath()
    {
        for(int x = 0; x < COLUMNS; x++)
        for(int y = 0; y < ROWS; y++)
            bgMap[x][y] = Color.BLACK;
        if(panel.mouseColumn() != -1 && panel.mouseRow() != -1)
        {
            if(traceType)   // A*
            {
                Vector<Coord> path = aStar.path(passMap, atLoc, new Coord(panel.mouseColumn(), panel.mouseRow()));
                for(Coord loc : path)
                {
                    if(panel.isInBounds(loc.x, loc.y))
                        bgMap[loc.x][loc.y] = Color.BLUE;
                }
            }
            else        // StraightLine
            {
                Vector<Coord> path = StraightLine.findLine(atLoc, new Coord(panel.mouseColumn(), panel.mouseRow()));
                for(Coord loc : path)
                {
                    if(panel.isInBounds(loc.x, loc.y))
                        bgMap[loc.x][loc.y] = Color.BLUE;
                }
            }
        }
        loadTestMap();
    }
    
    public void keyPressed(KeyEvent ke)
    {
        if(ke.getKeyCode() == KeyEvent.VK_SPACE)
            testMode();
        
        if(displayMode == RECT_MODE)
        {
            switch(ke.getKeyCode())
            {
                case KeyEvent.VK_NUMPAD8 :
                case KeyEvent.VK_UP      : atLoc.y--; break;
                case KeyEvent.VK_NUMPAD2 :
                case KeyEvent.VK_DOWN    : atLoc.y++; break;
                case KeyEvent.VK_NUMPAD4 :
                case KeyEvent.VK_LEFT    : atLoc.x--; break;
                case KeyEvent.VK_NUMPAD6 :
                case KeyEvent.VK_RIGHT   : atLoc.x++; break;
                case KeyEvent.VK_NUMPAD7 : atLoc.x--; atLoc.y--; break;
                case KeyEvent.VK_NUMPAD9 : atLoc.x++; atLoc.y--; break;
                case KeyEvent.VK_NUMPAD1 : atLoc.x--; atLoc.y++; break;
                case KeyEvent.VK_NUMPAD3 : atLoc.x++; atLoc.y++; break;
            }
        }
        else    // hex mode
        {
            int[][] stepArr = hexEvenRow;
            if(atLoc.y % 2 == 1)
                stepArr = hexOddRow;
            
            switch(ke.getKeyCode())
            {
                case KeyEvent.VK_NUMPAD4 : atLoc.x += stepArr[W][0]; atLoc.y += stepArr[W][1]; break;
                case KeyEvent.VK_NUMPAD6 : atLoc.x += stepArr[E][0]; atLoc.y += stepArr[E][1]; break;
                case KeyEvent.VK_NUMPAD7 : atLoc.x += stepArr[NW][0]; atLoc.y += stepArr[NW][1]; break;
                case KeyEvent.VK_NUMPAD9 : atLoc.x += stepArr[NE][0]; atLoc.y += stepArr[NE][1]; break;
                case KeyEvent.VK_NUMPAD1 : atLoc.x += stepArr[SW][0]; atLoc.y += stepArr[SW][1]; break;
                case KeyEvent.VK_NUMPAD3 : atLoc.x += stepArr[SE][0]; atLoc.y += stepArr[SE][1]; break;
            }
        }
        hexFoV.calcFoV(atLoc.x, atLoc.y, 12);
        rectFoV.calcFoV(atLoc.x, atLoc.y, 12);
        drawPath();
    }
    
    
    // test functions
    public static void main(String[] args)
    {
        RoguePanelDemo demoFrame = new RoguePanelDemo();
        demoFrame.panel.addMouseListener(demoFrame);
        demoFrame.panel.addMouseMotionListener(demoFrame);
        demoFrame.panel.addKeyListener(demoFrame);
        demoFrame.addKeyListener(demoFrame);
        demoFrame.testFrame.addKeyListener(demoFrame);
    }
    
    public void testMode()
    {
        dmButton.doClick();
        scButton.doClick();
        atLoc = new Coord(15, 15);
    }
    
    // returns a random color, which skews towards being brighter
    public Color randomColor()
    {
        return new Color((float)Math.random(), (float)Math.random(), (float)Math.random()).brighter();
    }
}