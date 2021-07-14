package WidlerSuite;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.imageio.*;
import java.io.*;

public class WidlerSuiteDemo2 extends JFrame implements MouseListener, MouseMotionListener, ActionListener, KeyListener, WSConstants
{
   private LayoutPanel layoutPanel;
   private JTextArea testTextArea;
   private RogueTilePanel rogueTilePanel;
   private JPanel controlPanel;
   private JPanel shakePanel;
   private JButton shakeButton;
   private JButton scrollButton;
   private JComboBox<String> vertShakeDD;
   private JComboBox<String> horizShakeDD;
   private JComboBox<String> shakeDurDD;
   private JCheckBox borderButton;
   private JCheckBox scriptUSButton;
   private JCheckBox spiralSearchButton;
   private JComboBox<String> modeDD;
   private JComboBox<String> traceDD;
   private JComboBox<String> areaDD;
   private int displayMode = RECT_MODE;
   private boolean searchDiagonal = true;
   private boolean showFoV = false;
   private boolean showDijkstra = false;
   private boolean showBSP = false;
   private boolean showVoronoi = false;
   private boolean showCA = false;
   private boolean showNoise = false;
   private boolean showBillowingNoise = false;
   private boolean showRidgedNoise = false;
   private boolean showBlit = false;
   private boolean showCone = false;
   private boolean demoScrollingUp = false;
   private boolean demoScrollingDown = false;
   private double scrollSpeed = 0.05;
   public static final int COLUMNS = 40;
   public static final int ROWS = 40;
   private String[][] strMap;
   private boolean[][] passMap;
   private Color[][] bgMap;
   private Color[][] fgMap;
   private Color[][] noiseMap;
   private Color[][] billowingNoiseMap;
   private Color[][] ridgedNoiseMap;
   private Color[][] blitMap;
   private AStar aStar;
   private Coord atLoc;
   private ShadowFoVRect rectFoV;
   private ShadowFoVHex hexFoV;
   private DijkstraMap dijkstraMap;
   private boolean[][] caMap;
   private boolean[][] caHexMap;
   private Coord searchLoc;
   private UnboundString scriptUS;
   private Vector<Room> roomList;
   private Vector<Color> roomColorList;
   private boolean traceType = true;   // true is A*, false is StraightLine
   private static final String[] displayModeList = {"Rect Mode (8-Way)", "Rect Mode (4-Way)", "Hex Mode (Don't Trim)", "Hex Mode (Trim)"};
   private static final String[] traceList = {"No Trace", "A* Trace", "Line Trace"};
   private static final String[] areaList = {"No Area", "Show Shadowcasting", "Show Dijkstra", 
                                             "Show Binary Space Partitioning", "Show Voronoi Map", "Show CA Map", 
                                             "Show Noise", "Show Billowing Noise", "Show Ridged Noise", "Show Blit", "Show Watcher Cone"};
   private static final Vector<Coord> voronoiPoints = getVoronoiPoints();
   private static final Color[] voronoiColors = getVoronoiColors();
   private static final String BULLET_STR = "" + (char)8226;
   private static final String BLIT_IMAGE = "Ricci.png";
   private boolean upKeyHeld = false;    // for movement on hex grid with arrow keys
   private boolean downKeyHeld = false;  // for movement on hex grid with arrow keys
   private String[] shakeList = {"0.0", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1.0"};
   private String[] shakeDurList = {"3", "5", "8", "10", "15", "20", "25", "30"};
   private Color[] gradient = WSTools.getGradient(Color.BLUE, Color.BLACK, 21);
   private Coord watcherLoc = new Coord(15, 15);
   private ShadowFoVRect watcherFoV;
   private int watcherRadius = 20;
   
   // test function
   public WidlerSuiteDemo2()
   {
      super();
      setSize(1400, 1050);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setTitle("Widler Suite Demo " + WSConstants.VERSION);
      layoutPanel = new LayoutPanel(this);
      add(layoutPanel);
      
      strMap = new String[COLUMNS][ROWS];
      passMap = new boolean[COLUMNS][ROWS];
      bgMap = new Color[COLUMNS][ROWS];
      fgMap = new Color[COLUMNS][ROWS];
      aStar = new AStar();
      atLoc = new Coord(5, 4);
      searchLoc = new Coord();
      
      setTestMap();
      setCA();
      setNoiseMaps();
      setBlitMap();
      
      // rogueTilePanel
      TilePalette palette = new TilePalette("WidlerSuite/WSFont_16x16.png", 16, 16);
      rogueTilePanel = new RogueTilePanel(COLUMNS, ROWS, palette);
      rogueTilePanel.setIcon(5, 10, '@');
      layoutPanel.add(rogueTilePanel, .75, 1.0, 0.0, 0.0);
      
      // controlPanel
      controlPanel = new JPanel();
      controlPanel.setLayout(new GridLayout(10, 1));
      layoutPanel.add(controlPanel, .25, 1.0, .75, 0.0);
      
      // instructions
      JTextArea label = new JTextArea("Click or double-click on RoguePanel for UnboundStrings.\n" +
      "Arrow keys or numpad to move @.");
      label.setFocusable(false);
      label.setEditable(false);
      label.setWrapStyleWord(true);
      label.setLineWrap(true);
      label.setFont(new Font(label.getFont().getName(), Font.PLAIN, 18));
      controlPanel.add(label);
      
      // mouse metrics
      testTextArea = new JTextArea();
      testTextArea.setEditable(false);
      testTextArea.setFocusable(false);
      testTextArea.setFont(new Font(label.getFont().getName(), Font.PLAIN, 18));
      controlPanel.add(testTextArea);
      
      // set the screen shake panel
      setShakePanel(controlPanel);
      
      // scroll demo button
      scrollButton = new JButton("Demo Scrolling");
      scrollButton.addActionListener(this);
      scrollButton.setFocusable(false);
      JPanel scrollSubpanel = new JPanel();
      scrollSubpanel.setLayout(new GridLayout(3, 1));
      controlPanel.add(scrollSubpanel);
      scrollSubpanel.add(new JLabel());
      scrollSubpanel.add(scrollButton);
      scrollSubpanel.add(new JLabel());
      
      // border toggle button
      borderButton = new JCheckBox("Show Tile Borders");
      borderButton.addActionListener(this);
      borderButton.setFocusable(false);
      controlPanel.add(borderButton);
      
      // spiral search toggle button
      spiralSearchButton = new JCheckBox("SpiralSearch to Nearest >");
      spiralSearchButton.addActionListener(this);
      spiralSearchButton.setFocusable(false);
      controlPanel.add(spiralSearchButton);
      
      // movement script button
      scriptUSButton = new JCheckBox("Show Movement Scirpt");
      scriptUSButton.addActionListener(this);
      scriptUSButton.setFocusable(false);
      controlPanel.add(scriptUSButton);
      scriptUS = new UnboundString("!", Color.WHITE, 20, 20);
      scriptUS.setBGColor(Color.BLUE);
      scriptUS.setBackgroundBoxType(UnboundString.CIRCLE);
      scriptUS.setBackgroundBox(true);
      scriptUS.setAffectedByAge(false);
      scriptUS.setSpeed(-.3, 0.0);
      scriptUS.setBorder(Color.WHITE);
      scriptUS.setVisible(false);
      MovementScript script = new MovementScript(scriptUS);
      for(int i = 0; i < 30; i++)
      {
         script.setImpulse(i, .01, -.01);
         script.setImpulse(i + 30, .01, .01);
         script.setImpulse(i + 60, -.01, .01);
         script.setImpulse(i + 90, -.01, -.01);
      }
      script.setLoops(true);
      //rogueTilePanel.add(script);
     // rogueTilePanel.addNonlocking(scriptUS);
      
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
      /*
      String fontName = FontLoader.load("WidlerSuite/Px437_Wyse700b");
      if(fontName == null)
         fontName = FontLoader.load("Px437_Wyse700b");
      if(fontName != null)
      {
         rogueTilePanel.setFontName(fontName);
         rogueTilePanel.setTightFontBorders(true);
      }*/
      //rogueTilePanel.setFontName("Lucida Sans Regular");
      //rogueTilePanel.setFontName("Monospaced");
      
      javax.swing.Timer timer = new javax.swing.Timer(1000 / FRAMES_PER_SECOND, null);
      timer.addActionListener(this);
      timer.addActionListener(rogueTilePanel);
      setVisible(true);
      timer.start();
   }
   
   private void setShakePanel(JPanel parent)
   {
      shakePanel = new JPanel();
      JPanel subpanel = new JPanel();
      JPanel subpanel2 = new JPanel();
      shakePanel.setLayout(new GridLayout(3, 2));
      shakeButton = new JButton("Screen Shake");
      shakeButton.setFocusable(false);
      shakeButton.addActionListener(this);
      shakePanel.add(shakeButton);
      subpanel.setLayout(new GridLayout(1, 4));
      shakePanel.add(subpanel);
      vertShakeDD = new JComboBox<String>(shakeList);
      horizShakeDD = new JComboBox<String>(shakeList);
      subpanel.add(new JLabel("X Shake:"));
      subpanel.add(horizShakeDD);
      subpanel.add(new JLabel("Y Shake:"));
      subpanel.add(vertShakeDD);
      subpanel2.setLayout(new GridLayout(1, 2));
      shakeDurDD = new JComboBox<String>(shakeDurList);
      subpanel2.add(new JLabel("Shake Duration:"));
      subpanel2.add(shakeDurDD);
      shakePanel.add(subpanel2);
      vertShakeDD.setFocusable(false);
      horizShakeDD.setFocusable(false);
      shakeDurDD.setFocusable(false);
      vertShakeDD.setSelectedIndex(2);
      horizShakeDD.setSelectedIndex(2);
      shakeDurDD.setSelectedIndex(2);
      parent.add(shakePanel);
   }
   
   // update mouseLoc where needed. External listeners notified in the usual way.
   public void mouseMoved(MouseEvent me){dispLoc(me);}
   public void mouseDragged(MouseEvent me){}
   public void mouseEntered(MouseEvent me){}
   public void mouseExited(MouseEvent me){/*mouseLoc[0] = -1; mouseLoc[1] = -1; if(testMode) testClick(me);*/}
   public void mousePressed(MouseEvent me){dispLoc(me); testClick(me.getClickCount());}
   public void mouseReleased(MouseEvent me){}
   public void mouseClicked(MouseEvent me){}
   public void keyTyped(KeyEvent ke){}
   
   // update the mouse metrics text area when a mouse event occurs
   private void dispLoc(MouseEvent me)
   {
      String str = "Mouse pixel location: " + String.format("[%d, %d]\n", me.getX(), me.getY());
      str += "Mouse tile location: " + String.format("[%d, %d]\n", rogueTilePanel.mouseColumn(), rogueTilePanel.mouseRow());
      double x = WSTools.getHexX(rogueTilePanel.mouseColumn(), rogueTilePanel.mouseRow()) - WSTools.getHexX(atLoc);
      double y = rogueTilePanel.mouseRow() - atLoc.y;
      str += "Angle to mouseLoc: " + (WSTools.getAngle(x, y));
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
            UnboundString star = new UnboundString("*", WSTools.randomColor(), rogueTilePanel.mouseColumn(), rogueTilePanel.mouseRow());
            star.setLifespan(15);
            star.setSpeed((Math.random() * .4) - .2, -.5 + (Math.random() * .2));
            star.setAffectedByGravity(true);
            rogueTilePanel.add(star);
         }
      }
      // float effect on double click
      if(clicks == 2)
      {
         UnboundString str = new UnboundString("UnboundString", Color.WHITE, rogueTilePanel.mouseColumn(), rogueTilePanel.mouseRow());
         str.setLifespan(30);
         str.setSpeed(0.0, -.1);
         str.setBackgroundBox(true);
         rogueTilePanel.add(str);
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
                        rogueTilePanel.setTrimOddAxis(false);
                        break;
            case 3 :    displayMode = HEX_MODE;
                        rogueTilePanel.setTrimOddAxis(true);
                        break;
         }
         rogueTilePanel.setDisplayMode(displayMode);
         aStar.setMode(displayMode);
         aStar.setSearchDiagonal(searchDiagonal);
         StraightLine.setMode(displayMode);
         calcFoV();
         drawPath();
      }
      
      if(ae.getSource() == areaDD)
      {
         showFoV = false;
         showDijkstra = false;
         showBSP = false;
         showVoronoi = false;
         showCA = false;
         showNoise = false;
         showBillowingNoise = false;
         showRidgedNoise = false;
         showBlit = false;
         showCone = false;
         switch(areaDD.getSelectedIndex())
         {
            case 1 :    showFoV = true;
                        calcFoV();
                        break;
            case 2 :    showDijkstra = true;
                        break;
            case 3 :    showBSP = true;
                        break;
            case 4 :    showVoronoi = true;
                        break;
            case 5 :    showCA = true;
                        break;
            case 6 :    showNoise = true;
                        break;
            case 7 :    showBillowingNoise = true;
                        break;
            case 8 :    showRidgedNoise = true;
                        break;
            case 9 :    showBlit = true;
                        break;
            case 10:    showCone = true;
                        break;
            default :   break;
         }
         loadTestMap();
      }
      
      if(ae.getSource() == scrollButton)
      {
         if(demoScrollingUp == false && demoScrollingDown == false)
         {
            demoScrollingUp = true;
            rogueTilePanel.setScroll(scrollSpeed, scrollSpeed);
         }
      }
      
      if(ae.getSource() == borderButton)
      {
         rogueTilePanel.showTileBorders(borderButton.isSelected());
      }
      
      if(ae.getSource() == scriptUSButton)
      {
         scriptUS.setVisible(scriptUSButton.isSelected());
      }
      
      if(ae.getSource() == spiralSearchButton)
      {
         loadTestMap();
      }
      
      if(ae.getSource() == shakeButton)
      {
         try
         {
            double xShake = Double.parseDouble((String)horizShakeDD.getSelectedItem());
            double yShake = Double.parseDouble((String)vertShakeDD.getSelectedItem());
            int shakeD = Integer.parseInt((String)shakeDurDD.getSelectedItem());
            rogueTilePanel.setScreenShake(xShake, yShake, shakeD);
         }
         catch(Exception ex)
         {
            rogueTilePanel.setScreenShake(0, 0);
         }
      }
      
      // blinks, pulses, etc
      if(ae.getSource() instanceof javax.swing.Timer)
      {
         int row = ROWS - 1;
         rogueTilePanel.write(0, row, "Blink:", Color.WHITE);
         
         if(AnimationManager.slowBlink())
            rogueTilePanel.setBGColor(7, row, Color.BLACK);
         else
            rogueTilePanel.setBGColor(7, row, Color.BLUE);
         
         if(AnimationManager.mediumBlink())
            rogueTilePanel.setBGColor(9, row, Color.BLACK);
         else
            rogueTilePanel.setBGColor(9, row, Color.BLUE);
         
         if(AnimationManager.fastBlink())
            rogueTilePanel.setBGColor(11, row, Color.BLACK);
         else
            rogueTilePanel.setBGColor(11, row, Color.BLUE);
         
         // scrolling
         if(demoScrollingUp)
         {
            rogueTilePanel.setScroll(rogueTilePanel.getXScroll() + scrollSpeed, rogueTilePanel.getYScroll() + scrollSpeed);
            if(rogueTilePanel.getXScroll() >= 1.0)
            {
               demoScrollingUp = false;
               demoScrollingDown = true;
            }
         }
         else if(demoScrollingDown)
         {
            rogueTilePanel.setScroll(rogueTilePanel.getXScroll() - scrollSpeed, rogueTilePanel.getYScroll() - scrollSpeed);
            if(rogueTilePanel.getXScroll() <= 0.0)
            {
               demoScrollingDown = false;
               rogueTilePanel.setScroll(0.0, 0.0);
            }
         }
         
         rogueTilePanel.write(13, row, "Pulse:", Color.WHITE);
         rogueTilePanel.setBGColor(20, row, gradient[AnimationManager.slowPulse()]);
         rogueTilePanel.setBGColor(22, row, gradient[AnimationManager.mediumPulse()]);
         rogueTilePanel.setBGColor(24, row, gradient[AnimationManager.fastPulse()]);
      }
   }
   
   private void setBlitMap()
   {
      blitMap = new Color[COLUMNS][ROWS];
      BufferedImage img = null;
      try
      {
         img = ImageIO.read(new FileInputStream("./WidlerSuite/" + BLIT_IMAGE));
      }
      catch(Exception ex){}
      if(img == null)
      {
         try
         {
            img = ImageIO.read(this.getClass().getResourceAsStream("/" + BLIT_IMAGE));
         }
         catch(Exception ex){}
      }
      if(img == null)
      {
         for(int x = 0; x < COLUMNS; x++)
         for(int y = 0; y < ROWS; y++)
            blitMap[x][y] = Color.BLACK;
      }
      else
         blitMap = WSTools.getBlit(COLUMNS, ROWS, img);
   }
   
   private void setTestMap()
   {
      strMap = new String[COLUMNS][ROWS];
      passMap = new boolean[COLUMNS][ROWS];
      bgMap = new Color[COLUMNS][ROWS];
      fgMap = new Color[COLUMNS][ROWS];
      roomList = BinarySpacePartitioning.partition(new Coord(COLUMNS, ROWS), 5, 12);
      roomColorList = new Vector<Color>();
      for(int i = 0; i < roomList.size(); i++)
         roomColorList.add(WSTools.randomColor().darker().darker());
      
      for(int x = 0; x < COLUMNS; x++)
      for(int y = 0; y < ROWS; y++)
      {
         if(x < 2 || y < 2 || x > COLUMNS - 3 || y > ROWS - 3 || Math.random() < .05)
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
      
      // '>' for searching, '!' for the watcher
      strMap[4][4] = ">";
      strMap[4][ROWS - 5] = ">";
      strMap[COLUMNS - 5][4] = ">";
      strMap[COLUMNS - 5][ROWS - 5] = ">";
      strMap[watcherLoc.x][watcherLoc.y] = "!";
      passMap[4][4] = true;
      passMap[4][ROWS - 5] = true;
      passMap[COLUMNS - 5][4] = true;
      passMap[COLUMNS - 5][ROWS - 5] = true;
      rectFoV = new ShadowFoVRect(passMap);
      hexFoV = new ShadowFoVHex(passMap);
      dijkstraMap = new DijkstraMap(passMap);
      watcherFoV = new ShadowFoVRect(passMap);
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
      for(int y = 0; y < ROWS - 1; y++)
      {
            rogueTilePanel.setTile(x, y, strMap[x][y], fgMap[x][y], bgMap[x][y]);
         if(showFoV && displayMode == RECT_MODE)
         {
            if(rectFoV.isVisible(x, y) && bgMap[x][y] == Color.BLACK)
               rogueTilePanel.setTile(x, y, strMap[x][y], fgMap[x][y], Color.DARK_GRAY);
            else
               rogueTilePanel.setTile(x, y, strMap[x][y], fgMap[x][y], bgMap[x][y]);
         }
         else if(showFoV && displayMode == HEX_MODE)
         {
            if(hexFoV.isVisible(x, y) && bgMap[x][y] == Color.BLACK)
               rogueTilePanel.setTile(x, y, strMap[x][y], fgMap[x][y], Color.DARK_GRAY);
            else
               rogueTilePanel.setTile(x, y, strMap[x][y], fgMap[x][y], bgMap[x][y]);
         }
         else if(showDijkstra)
         {
            int val = dijkstraMap.getValue(x, y);
            if(val < 128 && bgMap[x][y] == Color.BLACK)
               rogueTilePanel.setTile(x, y, strMap[x][y], fgMap[x][y], new Color(0, 255 - (val * 2), 0));
            else
               rogueTilePanel.setTile(x, y, strMap[x][y], fgMap[x][y], bgMap[x][y]);
         }
         else if(showCA)
         {
            if(displayMode == RECT_MODE)
            {
               if(caMap[x][y])
                  rogueTilePanel.setBGColor(x, y, Color.BLUE);
               else
                  rogueTilePanel.setBGColor(x, y, Color.ORANGE);
            }
            else // hex mode
            {
               if(caHexMap[x][y])
                  rogueTilePanel.setBGColor(x, y, Color.BLUE);
               else
                  rogueTilePanel.setBGColor(x, y, Color.ORANGE);
            }
         }         
         else if(showCone)
         {
            if(watcherFoV.isVisible(x, y) && bgMap[x][y] == Color.BLACK)
               rogueTilePanel.setTile(x, y, strMap[x][y], fgMap[x][y], Color.DARK_GRAY);
            else
               rogueTilePanel.setTile(x, y, strMap[x][y], fgMap[x][y], bgMap[x][y]);
         }
      }
      rogueTilePanel.setTile(atLoc.x, atLoc.y, "@", Color.CYAN);
      
      // BSP
      if(showBSP)
      {
         for(int i = 0; i < roomList.size(); i++)
         {
            Room room = roomList.elementAt(i);
            if(room.isParent == false)
            {
               Color c = WSTools.randomColor().darker().darker();
               rogueTilePanel.setBGBox(room.origin, room.size, roomColorList.elementAt(i));
               for(int x = 0; x < COLUMNS; x++)
                  rogueTilePanel.setBGColor(x, ROWS - 1, Color.BLACK);
            }
         }
      }
         
      // Voronoi
      if(showVoronoi)
      {
         int[][] vMap = Voronoi.generate(voronoiPoints, COLUMNS, ROWS);
         for(int x = 0; x < COLUMNS; x++)
         for(int y = 0; y < ROWS - 1; y++)
         {
            rogueTilePanel.setBGColor(x, y, voronoiColors[vMap[x][y]]);
         }
         for(int i = 0; i < voronoiPoints.size(); i++)
         {
            rogueTilePanel.setString(voronoiPoints.elementAt(i).x, voronoiPoints.elementAt(i).y, BULLET_STR);
            rogueTilePanel.setFGColor(voronoiPoints.elementAt(i).x, voronoiPoints.elementAt(i).y, Color.CYAN);
         }
      }
      
      // if spiralSearch, execute search and invert colors of target (if any)
      if(spiralSearchButton.isSelected())
      {
         if(searchLoc != null)
         {
            Color fg = rogueTilePanel.getBGColor(searchLoc.x, searchLoc.y);
            Color bg = rogueTilePanel.getFGColor(searchLoc.x, searchLoc.y);
            rogueTilePanel.setBGColor(searchLoc.x, searchLoc.y, bg);
            rogueTilePanel.setFGColor(searchLoc.x, searchLoc.y, fg);
         }
      }
      
      // noise
      if(showNoise)
      {
         for(int x = 0; x < COLUMNS; x++)
         for(int y = 0; y < ROWS - 1; y++)
         {
            rogueTilePanel.setBGColor(x, y, noiseMap[x][y]);
         }
      }
      
      // billowing noise
      if(showBillowingNoise)
      {
         for(int x = 0; x < COLUMNS; x++)
         for(int y = 0; y < ROWS - 1; y++)
         {
            rogueTilePanel.setBGColor(x, y, billowingNoiseMap[x][y]);
         }
      }
      
      // ridged noise
      if(showRidgedNoise)
      {
         for(int x = 0; x < COLUMNS; x++)
         for(int y = 0; y < ROWS - 1; y++)
         {
            rogueTilePanel.setBGColor(x, y, ridgedNoiseMap[x][y]);
         }
      }
      
      // blit
      if(showBlit)
      {
         for(int x = 0; x < COLUMNS; x++)
         for(int y = 0; y < ROWS - 1; y++)
         {
            rogueTilePanel.setBGColor(x, y, blitMap[x][y]);
         }
      }
   }
   
   private void setNoiseMaps()
   {
      noiseMap = setNoiseMap(false, false);
      billowingNoiseMap = setNoiseMap(true, false);
      ridgedNoiseMap = setNoiseMap(false, true);
   }
   
   private Color[][] setNoiseMap(boolean billow, boolean ridged)
   {
      Color[][] nm = new Color[COLUMNS][ROWS];
      NoiseChoir noise = new NoiseChoir();
      if(billow)
         noise.applyBillow();
      if(ridged)
         noise.applyRidged();
      for(int x = 0; x < COLUMNS; x++)
      for(int y = 0; y < ROWS - 1; y++)
      {
         double subX = .1 * (double)x;
         double subY = .1 * (double)y;
         int intensity = (int)(noise.getValue(subX, subY) * 256);
         nm[x][y] = new Color(intensity, intensity, intensity);
      }
      for(int x = 0; x < COLUMNS; x++)
         nm[x][ROWS - 1] = Color.BLACK;
      return nm;
   }
   
   private void drawPath()
   {
      for(int x = 0; x < COLUMNS; x++)
      for(int y = 0; y < ROWS; y++)
         bgMap[x][y] = Color.BLACK;
      if(rogueTilePanel.mouseColumn() != -1 && rogueTilePanel.mouseRow() != -1)
      {
         if(traceDD.getSelectedIndex() == 1)   // A*
         {
            Vector<Coord> path = aStar.path(passMap, atLoc, new Coord(rogueTilePanel.mouseColumn(), rogueTilePanel.mouseRow()));
            for(Coord loc : path)
            {
               if(rogueTilePanel.isInBounds(loc.x, loc.y))
                  bgMap[loc.x][loc.y] = Color.BLUE;
            }
         }
         else if(traceDD.getSelectedIndex() == 2)        // StraightLine
         {
            Vector<Coord> path = StraightLine.findLine(atLoc, new Coord(rogueTilePanel.mouseColumn(), rogueTilePanel.mouseRow()));
            for(Coord loc : path)
            {
               if(rogueTilePanel.isInBounds(loc.x, loc.y))
                  bgMap[loc.x][loc.y] = Color.BLUE;
            }
         }
      }
      search();
      loadTestMap();
   }
   
   public void keyReleased(KeyEvent ke)
   {
      if(ke.getKeyCode() == KeyEvent.VK_UP)
         upKeyHeld = false;
      if(ke.getKeyCode() == KeyEvent.VK_DOWN)
         downKeyHeld = false;
   }
   
   public void keyPressed(KeyEvent ke)
   {
      if(ke.getKeyCode() == KeyEvent.VK_UP)
         upKeyHeld = true;
      if(ke.getKeyCode() == KeyEvent.VK_DOWN)
         downKeyHeld = true;
      
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
         
         // numpad keys
         switch(ke.getKeyCode())
         {
            case KeyEvent.VK_NUMPAD4 : atLoc.x += stepArr[W][0]; atLoc.y += stepArr[W][1]; break;
            case KeyEvent.VK_NUMPAD6 : atLoc.x += stepArr[E][0]; atLoc.y += stepArr[E][1]; break;
            case KeyEvent.VK_NUMPAD7 : atLoc.x += stepArr[NW][0]; atLoc.y += stepArr[NW][1]; break;
            case KeyEvent.VK_NUMPAD9 : atLoc.x += stepArr[NE][0]; atLoc.y += stepArr[NE][1]; break;
            case KeyEvent.VK_NUMPAD1 : atLoc.x += stepArr[SW][0]; atLoc.y += stepArr[SW][1]; break;
            case KeyEvent.VK_NUMPAD3 : atLoc.x += stepArr[SE][0]; atLoc.y += stepArr[SE][1]; break;
         }
         
         // diagonals in hex mode
         if(upKeyHeld)
         {
            if(ke.getKeyCode() == KeyEvent.VK_LEFT)
            {
               atLoc.x += stepArr[NW][0];
               atLoc.y += stepArr[NW][1];
            }
            else if(ke.getKeyCode() == KeyEvent.VK_RIGHT)
            {
               atLoc.x += stepArr[NE][0];
               atLoc.y += stepArr[NE][1];
            }
         }
         else if(downKeyHeld)
         {
            if(ke.getKeyCode() == KeyEvent.VK_LEFT)
            {
               atLoc.x += stepArr[SW][0];
               atLoc.y += stepArr[SW][1];
            }
            else if(ke.getKeyCode() == KeyEvent.VK_RIGHT)
            {
               atLoc.x += stepArr[SE][0];
               atLoc.y += stepArr[SE][1];
            }
         }
         else
         {
            if(ke.getKeyCode() == KeyEvent.VK_RIGHT)
               atLoc.x += 1;
            else if(ke.getKeyCode() == KeyEvent.VK_LEFT)
               atLoc.x -= 1;
         }
      }
      calcFoV();
      drawPath();
   }
   
   private static Vector<Coord> getVoronoiPoints()
   {
      int xIncrement = COLUMNS / 5;
      int yIncrement = ROWS / 5;
      int xInset = (COLUMNS / 10) - (4);
      int yInset = (ROWS / 10) - (2);
      Vector<Coord> list = new Vector<Coord>();
      
      for(int x = 0; x < 5; x++)
      for(int y = 0; y < 5; y++)
      {
         Coord c = new Coord((xInset + (xIncrement * x) + WSTools.random(8)), yInset + (yIncrement * y) + WSTools.random(4));
         c.x = WSTools.minMax(0, c.x, COLUMNS - 1);
         c.y = WSTools.minMax(0, c.y, ROWS - 1);
         list.add(c);
      }
      return list;
   }
   
   private static Color[] getVoronoiColors()
   {
      Color[] arr = new Color[25];
      for(int i = 0; i < 25; i++)
         arr[i] = WSTools.randomColor().darker().darker();
      return arr;
   }
   
   private void setCA()
   {
      caMap = new boolean[COLUMNS][ROWS - 1];
      caHexMap = new boolean[COLUMNS][ROWS - 1];
      
      for(int x = 0; x < COLUMNS; x++)
      for(int y = 0; y < ROWS - 1; y++)
      {
         caMap[x][y] = WSTools.random() < .55;
         caHexMap[x][y] = WSTools.random() < .55;
      }
      caMap = SmoothCA.smooth(caMap, 5);
      caHexMap = SmoothCA.smooth(caHexMap, 5, HEX_MODE);
   }
   
   private void calcFoV()
   {
      hexFoV.calcFoV(atLoc.x, atLoc.y, 12);
      rectFoV.calcFoV(atLoc.x, atLoc.y, 12);
      watcherFoV.calcCone(watcherLoc, watcherRadius, atLoc);
   }
   
   
   // test functions
   public static void main(String[] args)
   {
      WidlerSuiteDemo2 demoFrame = new WidlerSuiteDemo2();
      demoFrame.rogueTilePanel.addMouseListener(demoFrame);
      demoFrame.rogueTilePanel.addMouseMotionListener(demoFrame);
      demoFrame.rogueTilePanel.addKeyListener(demoFrame);
      demoFrame.addKeyListener(demoFrame);
      demoFrame.controlPanel.addKeyListener(demoFrame);
   }
}
