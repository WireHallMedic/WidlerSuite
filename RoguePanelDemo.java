package WidlerSuite;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class RoguePanelDemo extends JFrame implements MouseListener, MouseMotionListener, ActionListener, KeyListener, ComponentListener, WSConstants
{
    private JTextArea testTextArea;
    private RoguePanel roguePanel;
    private JPanel controlPanel;
    private JButton borderButton;
    private JButton spiralSearchButton;
    private JComboBox<String> modeDD;
    private JComboBox<String> traceDD;
    private JComboBox<String> areaDD;
    private int displayMode = RECT_MODE;
    private boolean searchDiagonal = true;
    private boolean showBorders = false;
    private boolean showFoV = false;
    private boolean showDijkstra = false;
    private boolean showSpiralSearch = false;
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
    private Coord searchLoc;
    private boolean traceType = true;   // true is A*, false is StraightLine
    private static final String[] displayModeList = {"Rect Mode (8-Way)", "Rect Mode (4-Way)", "Hex Mode"};
    private static final String[] traceList = {"No Trace", "A* Trace", "Line Trace"};
    private static final String[] areaList = {"No Area", "Show Shadowcasting", "Show Dijkstra"};
    
    // test function
    public RoguePanelDemo()
    {
        super();
        setSize(1400, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        addComponentListener(this); // for resizing
        
        strMap = new String[COLUMNS][ROWS];
        passMap = new boolean[COLUMNS][ROWS];
        bgMap = new Color[COLUMNS][ROWS];
        fgMap = new Color[COLUMNS][ROWS];
        aStar = new AStar();
        atLoc = new Coord(4, 4);
        searchLoc = new Coord();
        
        setTestMap();
        
        
        // roguePanel
        roguePanel = new RoguePanel();
        roguePanel.setColumnsAndRows(COLUMNS, ROWS);
        roguePanel.setString(5, 10, "@");
        roguePanel.setTileBorderColor(Color.ORANGE);
        
        add(roguePanel);
        
        // controlPanel
        controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(10, 1));
        this.add(controlPanel);
        
        // instructions
        JTextArea label = new JTextArea("Click or double-click on RoguePanel for UnboundStrings.\nSpiralSearch to find nearest '>'");
        label.setFocusable(false);
        label.setEditable(false);
        controlPanel.add(label);
        
        // mouse metrics
        testTextArea = new JTextArea();
        testTextArea.setEditable(false);
        testTextArea.setFocusable(false);
        controlPanel.add(testTextArea);
         
        // border toggle button
        borderButton = new JButton("Toggle Borders");
        borderButton.addActionListener(this);
        borderButton.setFocusable(false);
        controlPanel.add(borderButton);
         
        // spiral search toggle button
        spiralSearchButton = new JButton("Toggle SpiralSearch");
        spiralSearchButton.addActionListener(this);
        spiralSearchButton.setFocusable(false);
        controlPanel.add(spiralSearchButton);
        
        // display mode drop down (rect diagonal & orthogonal, rect orthogonal, hex)
        modeDD = new JComboBox<>(displayModeList);
        modeDD.addActionListener(this);
        modeDD.setFocusable(false);
        modeDD.setSelectedIndex(0);
        controlPanel.add(modeDD);
        
        // area display drop down (none/Shadowcasting/Dijkstra)
        areaDD = new JComboBox<>(areaList);
        areaDD.addActionListener(this);
        areaDD.setFocusable(false);
        areaDD.setSelectedIndex(0);
        controlPanel.add(areaDD);
        
        // trace display drop down (none/A*/straight line)
        traceDD = new JComboBox<>(traceList);
        traceDD.addActionListener(this);
        traceDD.setFocusable(false);
        traceDD.setSelectedIndex(0);
        controlPanel.add(traceDD);
        
        controlPanel.setFocusable(false);
        
        
        loadTestMap();
        javax.swing.Timer timer = new javax.swing.Timer(1000 / FRAMES_PER_SECOND, roguePanel);
        setVisible(true);
        timer.start();
    }
    
    // update the roguePanel and controlPanel sizes and locations when the frame is resized.
    private void setSizesAndLocs()
    {
        Insets insets = this.getInsets();
        int interiorWidth = this.getWidth() - (insets.left + insets.right);
        int interiorHeight = this.getHeight() - (insets.top + insets.bottom);
        int roguePanelSize = Math.min(interiorWidth, interiorHeight);
        roguePanel.setSize(roguePanelSize, roguePanelSize);
        controlPanel.setLocation(roguePanelSize, 0);
        controlPanel.setSize(interiorWidth - roguePanelSize, interiorHeight);
    }

    // update mouseLoc where needed. External listeners notified in the usual way.
    public void mouseMoved(MouseEvent me){dispLoc(me);}
    public void mouseDragged(MouseEvent me){}
    public void mouseEntered(MouseEvent me){}
    public void mouseExited(MouseEvent me){/*mouseLoc[0] = -1; mouseLoc[1] = -1; if(testMode) testClick(me);*/}
    public void mousePressed(MouseEvent me){dispLoc(me); testClick(me.getClickCount());}
    public void mouseReleased(MouseEvent me){}
    public void mouseClicked(MouseEvent me){}
    public void keyReleased(KeyEvent ke){}
    public void keyTyped(KeyEvent ke){}
    
    ///////////////////////////////////////////////////////////////////////
    // ComponentListener stuff
    public void componentHidden(ComponentEvent ce){}
    public void componentMoved(ComponentEvent ce){}
    public void componentShown(ComponentEvent ce){}
    
    // update metrics when resized
    public void componentResized(ComponentEvent ce)
    {
        setSizesAndLocs();
    }
    
    // update the mouse metrics text area when a mouse event occurs
    private void dispLoc(MouseEvent me)
    {
        String str = "Mouse pixel location: " + String.format("[%d, %d]\n", me.getX(), me.getY());
        str += "Mouse tile location: " + String.format("[%d, %d]\n", roguePanel.mouseColumn(), roguePanel.mouseRow());
        double x = MathTools.getHexX(roguePanel.mouseColumn(), roguePanel.mouseRow()) - MathTools.getHexX(atLoc);
        double y = roguePanel.mouseRow() - atLoc.y;
        str += "Angle to mouseLoc: " + (MathTools.getAngle(x, y));
        testTextArea.setText(str);
        drawPath();
    }
    
    // execute a spiral search for nearest >
    private void search()
    {
        SpiralSearch sSearch = new SpiralSearch(passMap, atLoc, displayMode, searchDiagonal);
        Coord target = sSearch.getNext();
        while(target != null)
        {
            if(strMap[target.x][target.y].equals(">"))
                break;
            target = sSearch.getNext();
        }
        searchLoc = target;
    }
    
    private void testClick(int clicks)
    {
        // fireworks on single click
        if(clicks == 1)
        {
            for(int i = 0; i < 16; i++)
            {
                UnboundString star = new UnboundString("*", randomColor(), roguePanel.mouseColumn(), roguePanel.mouseRow());
                star.setLifespan(15);
                star.setSpeed((Math.random() * .4) - .2, -.5 + (Math.random() * .2));
                star.setAffectedByGravity(true);
                roguePanel.add(star);
            }
        }
        // float effect on double click
        if(clicks == 2)
        {
            UnboundString str = new UnboundString("UnboundString", Color.WHITE, roguePanel.mouseColumn(), roguePanel.mouseRow());
            str.setLifespan(30);
            str.setSpeed(0.0, -.1);
            str.setBackgroundBox(true);
            roguePanel.add(str);
        }
    }
    
    public void actionPerformed(ActionEvent ae)
    {
        if(ae.getSource() == modeDD)
        {
            switch(modeDD.getSelectedIndex())
            {
                case 0 :    displayMode = RECT_MODE;
                            searchDiagonal = true;
                            break;
                case 1 :    displayMode = RECT_MODE;
                            searchDiagonal = false;
                            break;
                case 2 :    displayMode = HEX_MODE;
                            break;
            }
            roguePanel.setDisplayMode(displayMode);
            aStar.setMode(displayMode);
            aStar.setSearchDiagonal(searchDiagonal);
            StraightLine.setMode(displayMode);
            drawPath();
        }
        
        if(ae.getSource() == areaDD)
        {
            switch(areaDD.getSelectedIndex())
            {
                case 0 :    showFoV = false;
                            showDijkstra = false;
                            break;
                case 1 :    showFoV = true;
                            showDijkstra = false;
                            break;
                case 2 :    showFoV = false;
                            showDijkstra = true;
                            break;
            }
            loadTestMap();
        }
        
        if(ae.getSource() == borderButton)
        {
            showBorders = !showBorders;
            roguePanel.showTileBorders(showBorders);
        }
        
        if(ae.getSource() == spiralSearchButton)
        {
            showSpiralSearch = !showSpiralSearch;
            loadTestMap();
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
        
        // box
        for(int x = 5; x < 11; x++)
        {
            passMap[x][5] = false;
            passMap[x][10] = false;
        }
        for(int y = 5; y < 11; y++)
        {
            passMap[5][y] = false;
            passMap[10][y] = false;
        }
        for(int x = 6; x < 10; x++)
        for(int y = 6; y < 10; y++)
        {
            passMap[x][y] = true;
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
        
        // '>' for searching
        strMap[4][4] = ">";
        strMap[4][ROWS - 5] = ">";
        strMap[COLUMNS - 5][4] = ">";
        strMap[COLUMNS - 5][ROWS - 5] = ">";
        passMap[4][4] = true;
        passMap[4][ROWS - 5] = true;
        passMap[COLUMNS - 5][4] = true;
        passMap[COLUMNS - 5][ROWS - 5] = true;
        rectFoV = new ShadowFoV(passMap);
        hexFoV = new ShadowFoVHex(passMap);
        dijkstraMap = new DijkstraMap(passMap);
    }
    
    private void loadTestMap()
    {
        dijkstraMap = new DijkstraMap(passMap);
        dijkstraMap.setMode(displayMode);
        dijkstraMap.setSearchDiagonal(true);
        dijkstraMap.addGoal(atLoc.x, atLoc.y);
        dijkstraMap.setSearchDiagonal(searchDiagonal);
        dijkstraMap.process();
        for(int x = 0; x < COLUMNS; x++)
        for(int y = 0; y < ROWS; y++)
        {
            if(showFoV && displayMode == RECT_MODE)
            {
                if(rectFoV.canSee(x, y) && bgMap[x][y] == Color.BLACK)
                    roguePanel.setTile(x, y, strMap[x][y], fgMap[x][y], Color.DARK_GRAY);
                else
                    roguePanel.setTile(x, y, strMap[x][y], fgMap[x][y], bgMap[x][y]);
            }
            else if(showFoV && displayMode == HEX_MODE)
            {
                if(hexFoV.canSee(x, y) && bgMap[x][y] == Color.BLACK)
                    roguePanel.setTile(x, y, strMap[x][y], fgMap[x][y], Color.DARK_GRAY);
                else
                    roguePanel.setTile(x, y, strMap[x][y], fgMap[x][y], bgMap[x][y]);
            }
            else if(showDijkstra)
            {
                int val = dijkstraMap.getValue(x, y);
                if(val < 128 && bgMap[x][y] == Color.BLACK)
                    roguePanel.setTile(x, y, strMap[x][y], fgMap[x][y], new Color(0, 255 - (val * 2), 0));
                else
                    roguePanel.setTile(x, y, strMap[x][y], fgMap[x][y], bgMap[x][y]);
            }
            else
            {
                roguePanel.setTile(x, y, strMap[x][y], fgMap[x][y], bgMap[x][y]);
            }
        }
        roguePanel.setString(atLoc.x, atLoc.y, "@");
        
        // if spiralSearch, execute search and invert colors of target (if any)
        if(showSpiralSearch)
        {
            if(searchLoc != null)
            {
                Color fg = roguePanel.getBGColor(searchLoc.x, searchLoc.y);
                Color bg = roguePanel.getFGColor(searchLoc.x, searchLoc.y);
                roguePanel.setBGColor(searchLoc.x, searchLoc.y, bg);
                roguePanel.setFGColor(searchLoc.x, searchLoc.y, fg);
            }
        }
    }
    
    private void drawPath()
    {
        for(int x = 0; x < COLUMNS; x++)
        for(int y = 0; y < ROWS; y++)
            bgMap[x][y] = Color.BLACK;
        if(roguePanel.mouseColumn() != -1 && roguePanel.mouseRow() != -1)
        {
            if(traceDD.getSelectedIndex() == 1)   // A*
            {
                Vector<Coord> path = aStar.path(passMap, atLoc, new Coord(roguePanel.mouseColumn(), roguePanel.mouseRow()));
                for(Coord loc : path)
                {
                    if(roguePanel.isInBounds(loc.x, loc.y))
                        bgMap[loc.x][loc.y] = Color.BLUE;
                }
            }
            else if(traceDD.getSelectedIndex() == 2)        // StraightLine
            {
                Vector<Coord> path = StraightLine.findLine(atLoc, new Coord(roguePanel.mouseColumn(), roguePanel.mouseRow()));
                for(Coord loc : path)
                {
                    if(roguePanel.isInBounds(loc.x, loc.y))
                        bgMap[loc.x][loc.y] = Color.BLUE;
                }
            }
        }
        search();
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
            int[][] stepArr = HEX_EVEN_ROW;
            if(atLoc.y % 2 == 1)
                stepArr = HEX_ODD_ROW;
            
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
        demoFrame.roguePanel.addMouseListener(demoFrame);
        demoFrame.roguePanel.addMouseMotionListener(demoFrame);
        demoFrame.roguePanel.addKeyListener(demoFrame);
        demoFrame.addKeyListener(demoFrame);
        demoFrame.controlPanel.addKeyListener(demoFrame);
    }
    
    public void testMode()
    {
        setTestMap();         
        strMap[4][3] = "#"; 
        strMap[4][5] = "#"; 
        strMap[3][4] = "#"; 
        strMap[5][4] = "#";
        passMap[4][3] = false;
        passMap[4][5] = false;
        passMap[3][4] = false;
        passMap[5][4] = false;
    }
    
    // returns a random color, which skews towards being brighter
    public Color randomColor()
    {
        return new Color((float)Math.random(), (float)Math.random(), (float)Math.random()).brighter();
    }
}