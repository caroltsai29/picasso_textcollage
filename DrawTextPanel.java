package textcollage;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 * A panel that contains a large drawing area where strings
 * can be drawn.  The strings are represented by objects of
 * type DrawTextItem.  An input box under the panel allows
 * the user to specify what string will be drawn when the
 * user clicks on the drawing area.
 */
public class DrawTextPanel extends JPanel  {
	
	// As it now stands, this class can only show one string at at
	// a time!  The data for that string is in the DrawTextItem object
	// named theString.  (If it's null, nothing is shown.  This
	// variable should be replaced by a variable of type
	// ArrayList<DrawStringItem> that can store multiple items.
	
	private ArrayList<DrawTextItem> theString; // ADDED an ArrayList with the type DrawTextItem
	
	private Color currentTextColor = Color.BLACK;  // Color applied to new strings.

	private Canvas canvas;  // the drawing area.
	private JTextField input;  // where the user inputs the string that will be added to the canvas
	private SimpleFileChooser fileChooser;  // for letting the user select files
	private JMenuBar menuBar; // a menu bar with command that affect this panel
	private MenuHandler menuHandler; // a listener that responds whenever the user selects a menu command
	private JMenuItem undoMenuItem;  // the "Remove Item" command from the edit menu
	
	/**
	 * An object of type Canvas is used for the drawing area.
	 * The canvas simply displays all the DrawTextItems that
	 * are stored in the ArrayList, strings.
	 */
	private class Canvas extends JPanel {
		Canvas() {
			setPreferredSize( new Dimension(800,600) );
			setBackground(Color.LIGHT_GRAY);
			setFont( new Font( "Serif", Font.BOLD, 24 ));
		}
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
					RenderingHints.VALUE_ANTIALIAS_ON);
			if (theString != null)
				for (DrawTextItem s: theString)
					s.draw(g);

		}
	}
	
	/**
	 * An object of type MenuHandler is registered as the ActionListener
	 * for all the commands in the menu bar.  The MenuHandler object
	 * simply calls doMenuCommand() when the user selects a command
	 * from the menu.
	 */
	private class MenuHandler implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			doMenuCommand( evt.getActionCommand());
		}
	}

	/**
	 * Creates a DrawTextPanel.  The panel has a large drawing area and
	 * a text input box where the user can specify a string.  When the
	 * user clicks the drawing area, the string is added to the drawing
	 * area at the point where the user clicked.
	 */
	public DrawTextPanel() {
		fileChooser = new SimpleFileChooser();
		undoMenuItem = new JMenuItem("Remove Item");
		undoMenuItem.setEnabled(false);
		menuHandler = new MenuHandler();
		setLayout(new BorderLayout(3,3));
		setBackground(Color.BLACK);
		setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		canvas = new Canvas();
		add(canvas, BorderLayout.CENTER);
		JPanel bottom = new JPanel();
		bottom.add(new JLabel("Text to add: "));
		input = new JTextField("Hello World!", 40);
		bottom.add(input);
		add(bottom, BorderLayout.SOUTH);
		
		JButton button = new JButton("Generate Random");
		bottom.add(button);
		
		canvas.addMouseListener( new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				doMousePress( e.getX(), e.getY() ); 
			}
		} );
		
		button.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				for (int i = 0; i < 150; i++) { 
					int X = new Random().nextInt(800); 
					int Y = new Random().nextInt(600);
					doMousePress(X, Y); 
				}
			}
		} );
	}
	
	/**
	 * This method is called when the user clicks the drawing area.
	 * A new string is added to the drawing area.  The center of
	 * the string is at the point where the user clicked.
	 * @param e the mouse event that was generated when the user clicked
	 */
	public void doMousePress(int x, int y) {
		String text = input.getText().trim();
		if (text.length() == 0) {
			input.setText("Hello World!");
			text = "Hello World!";
		}
		DrawTextItem s = new DrawTextItem( text, x, y );
		s.setTextColor(currentTextColor);  // Default is null, meaning default color of the canvas (black).
				
		randomStyle(s);
		
		if (theString == null)
			theString = new ArrayList<DrawTextItem>();
		theString.add(s);
		undoMenuItem.setEnabled(true);
		canvas.repaint();
		
	}
	
	/*
	 * Here, I have done 2 implementation to make this program cooler.
	 * First is generating random style of the font style which includes different font families, font type (like bold, italic and both),
	 * font size, the scale of the font tag, and the generating different colors for the tag background.
	 * 
	 */
	public void randomStyle(DrawTextItem s) {
		String[] fontFamilies = {"Monospace", "Helvetica", "TimesRoman", "Courier", "Serif", "Sans_Serif"}; 
		int fontType = new Random().nextInt(fontFamilies.length); 
		int fontSize = new Random().nextInt(15); 
		if (fontSize <= 6)
			fontSize = 7;
		int fontStyle;
		if (Math.random() > 0.25)
			fontStyle = Font.ITALIC;
		else if (Math.random() > 0.5)
			fontStyle = Font.BOLD;
		else
			fontStyle = Font.ITALIC + Font.BOLD;
		s.setFont( new Font(fontFamilies[fontType], fontStyle, fontSize )); 
		int angle = new Random().nextInt(359);
		s.setRotationAngle(angle);
		int scale = new Random().nextInt(5);
		if (scale == 0)
			scale = 1; 
		s.setMagnification(scale);
		if (Math.random() < 0.5)
			s.setBorder(true); 
		else
			s.setBorder(false);
		s.setTextTransparency(0.3);
		int r = new Random().nextInt(255);
		int g = new Random().nextInt(255);
		int b = new Random().nextInt(255);
		s.setBackground(new Color(r,g,b));
		s.setBackgroundTransparency(Math.random() * 0.90 + 0.10);
	}
	
	/**
	 * Returns a menu bar containing commands that affect this panel.  The menu
	 * bar is meant to appear in the same window that contains this panel.
	 */
	public JMenuBar getMenuBar() {
		if (menuBar == null) {
			menuBar = new JMenuBar();
			
			String commandKey; // for making keyboard accelerators for menu commands
			if (System.getProperty("mrj.version") == null)
				commandKey = "control ";  // command key for non-Mac OS
			else
				commandKey = "meta ";  // command key for Mac OS
			
			JMenu fileMenu = new JMenu("File");
			menuBar.add(fileMenu);
			
			JMenuItem saveItem = new JMenuItem("Save...");
			saveItem.setAccelerator(KeyStroke.getKeyStroke(commandKey + "S"));
			saveItem.addActionListener(menuHandler);
			fileMenu.add(saveItem);
			
			JMenuItem openItem = new JMenuItem("Open...");
			openItem.setAccelerator(KeyStroke.getKeyStroke(commandKey + "O"));
			openItem.addActionListener(menuHandler);
			fileMenu.add(openItem);
			
			fileMenu.addSeparator();
			
			JMenuItem saveImageItem = new JMenuItem("Save Image...");
			saveImageItem.setAccelerator(KeyStroke.getKeyStroke(commandKey + "shift S"));
			saveImageItem.addActionListener(menuHandler);
			fileMenu.add(saveImageItem);
			
			JMenu editMenu = new JMenu("Edit");
			menuBar.add(editMenu);
			undoMenuItem.addActionListener(menuHandler); 
			undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(commandKey + "Z"));
			editMenu.add(undoMenuItem);
			editMenu.addSeparator();
			JMenuItem clearItem = new JMenuItem("Clear");
			clearItem.addActionListener(menuHandler);
			editMenu.add(clearItem);
			
			JMenu optionsMenu = new JMenu("Options");
			menuBar.add(optionsMenu);
			JMenuItem colorItem = new JMenuItem("Set Text Color...");
			colorItem.setAccelerator(KeyStroke.getKeyStroke(commandKey + "T"));
			colorItem.addActionListener(menuHandler);
			optionsMenu.add(colorItem);
			JMenuItem bgColorItem = new JMenuItem("Set Background Color...");
			bgColorItem.addActionListener(menuHandler);
			optionsMenu.add(bgColorItem);
			
		}
		return menuBar;
	}
	
	/**
	 * Carry out one of the commands from the menu bar.
	 * @param command the text of the menu command.
	 */
	private void doMenuCommand(String command) {
		if (command.equals("Save...")) { 
			saveImageAsText();
		}
		else if (command.equals("Open...")) {
			openTextFile();
			canvas.repaint(); 
		}
		else if (command.equals("Clear")) {  // remove all strings
			theString = null;   // Remove the ONLY string from the canvas.
			undoMenuItem.setEnabled(false);
			canvas.repaint();
		}
		else if (command.equals("Remove Item")) { // remove the most recently added string
			// to apply undo in the program
			if (theString.size() > 0) {
				theString.remove(theString.size() - 1); 
			}
			if (theString.size() == 0)
				undoMenuItem.setEnabled(false); 
			canvas.repaint();
		}
		else if (command.equals("Set Text Color...")) {
			Color c = JColorChooser.showDialog(this, "Select Text Color", currentTextColor);
			if (c != null)
				currentTextColor = c;
		}
		else if (command.equals("Set Background Color...")) {
			Color c = JColorChooser.showDialog(this, "Select Background Color", canvas.getBackground());
			if (c != null) {
				canvas.setBackground(c);
				canvas.repaint();
			}
		}
		else if (command.equals("Save Image...")) {  // save a PNG image of the drawing area
			File imageFile = fileChooser.getOutputFile(this, "Select Image File Name", "textimage.png");
			if (imageFile == null)
				return;
			try {
				// Because the image is not available, I will make a new BufferedImage and
				// draw the same data to the BufferedImage as is shown in the panel.
				// A BufferedImage is an image that is stored in memory, not on the screen.
				// There is a convenient method for writing a BufferedImage to a file.
				BufferedImage image = new BufferedImage(canvas.getWidth(),canvas.getHeight(),
						BufferedImage.TYPE_INT_RGB);
				Graphics g = image.getGraphics();
				g.setFont(canvas.getFont());
				canvas.paintComponent(g);  // draws the canvas onto the BufferedImage, not the screen!
				boolean ok = ImageIO.write(image, "PNG", imageFile); // write to the file
				if (ok == false)
					throw new Exception("PNG format not supported (this shouldn't happen!).");
			}
			catch (Exception e) {
				JOptionPane.showMessageDialog(this, 
						"Sorry, an error occurred while trying to save the image:\n" + e);
			}
		}
	}
	
	/*
	 * Save the progress as a text file.
	 */
	private void saveImageAsText() {
		File textFile = fileChooser.getOutputFile(this, "Select Text File Name", "textImage.txt"); 
		if (textFile == null)
			return;
		try {
			PrintWriter write = new PrintWriter(textFile);
			write.println("New textImage");
			write.println(canvas.getBackground().getRed());
			write.println(canvas.getBackground().getGreen());
			write.println(canvas.getBackground().getBlue());
			if (theString != null)
				for (DrawTextItem s: theString) { 
					write.println("theString:"); 
					write.println(s.getString()); 
					write.println(s.getX());
					write.println(s.getY());
					write.println(s.getFont().getName());
					write.println(s.getFont().getStyle());
					write.println(s.getFont().getSize());
					write.println(s.getTextColor().getRed()); 
					write.println(s.getTextColor().getGreen());
					write.println(s.getTextColor().getBlue());
					write.println(s.getTextTransparency());
					if (s.getBackground() == null) {
						write.println(-1);
						write.println(-1);
						write.println(-1);
					} else {
						write.println(s.getBackground().getRed()); 
						write.println(s.getBackground().getGreen());
						write.println(s.getBackground().getBlue());
					}
					write.println(s.getBackgroundTransparency());
					write.println(s.getBorder())
					write.println(s.getMagnification());
					write.println(s.getRotationAngle());
				}
			write.close();

		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, 
					"Sorry, an error occurred while trying to save this image progress.\n" + "Error message: " + e);
		}
	}
	
	/*
	 * Open the save progress using a text file
	 */
	private void openTextFile() {
		File openTextFile = fileChooser.getInputFile(this, "Open Saved Text File"); 
		if (openTextFile == null)
			return;
		
		try {
			Scanner read = new Scanner(openTextFile); 
			if (!read.nextLine().equals("New textImage")) {
				JOptionPane.showMessageDialog(this, "Sorry, This is not an valid file. \nPlease try again."); 
				return;
			}
			
			Color savedBg = new Color(read.nextInt(),read.nextInt(),read.nextInt());
			ArrayList<DrawTextItem> newStrings = new ArrayList<DrawTextItem>(); 
			DrawTextItem newText;
			read.nextLine();
			while (read.hasNext() && read.nextLine().equals("theString:")) { 
				newText = new DrawTextItem(read.nextLine(), read.nextInt(), read.nextInt());
				read.nextLine();
				newText.setFont(new Font(read.nextLine(), read.nextInt(), read.nextInt()));
				newText.setTextColor(new Color(read.nextInt(), read.nextInt(), read.nextInt()));
				newText.setTextTransparency(read.nextDouble());
				
				int r = read.nextInt(); 
				int g = read.nextInt();
				int b = read.nextInt();
				if (r == -1)
					newText.setBackground(null); 
				
				else
					newText.setBackground(new Color(r, g, b));
				
				newText.setBackgroundTransparency(read.nextDouble()); 
				newText.setBorder(read.nextBoolean());
				newText.setMagnification(read.nextDouble());
				newText.setRotationAngle(read.nextDouble());
				read.nextLine();
				newStrings.add(newText); 
			}
			
			canvas.setBackground(savedBg);
			theString = newStrings;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, 
					"Sorry, an error occurred while trying to load the save progress.\n" + "Error message: " + e);
		}
	}
}
