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
		
		/*
		 * This is the second feature that I added in this program.
		 * The second feature is that, when "Generate Random" button is pressed,
		 * the program will generate 150 times random DrawTextItem with different 
		 * coordinate, color, rotation, font families and style, and many more.
		 * 
		 * To implement this, I have change the parameter in doMousePress() function
		 * from (MouseEvent e) to (int x, int y) to be able to get random generated x and y values
		 * 
		 * Besides that, I have added a new button next to the text field
		 * 
		 */
		JButton button = new JButton("Generate Random");
		bottom.add(button);
		
		canvas.addMouseListener( new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				doMousePress( e.getX(), e.getY() ); // when the canvas is click, use MouseEvent e to get the current X and Y coordinate value
			}
		} );
		
		button.addActionListener(new ActionListener() { // now add a ActionListener for the button
			public void actionPerformed(ActionEvent e) { // when the button is clicked
				
				for (int i = 0; i < 150; i++) { // generate random integer values for the x and y coordinate
					int X = new Random().nextInt(800); // the range used here is according to the canvas width and height
					int Y = new Random().nextInt(600);
					
					doMousePress(X, Y); // call doMousePress function with generated X and Y values
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
	public void doMousePress(int x, int y) { // I have change the parameter (MouseEvent e) to (int x, int y) to be able to get random generated x and y values
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
		String[] fontFamilies = {"Monospace", "Helvetica", "TimesRoman", "Courier", "Serif", "Sans_Serif"}; // create an array which contains different font families
		int fontType = new Random().nextInt(fontFamilies.length); // generating index value to get random item from the fontFamilies array
		int fontSize = new Random().nextInt(15); // generating different font size from 0 to 15
		if (fontSize <= 6) // to avoid the font to be too small, set a condition
			fontSize = 7;// here, i set the smallest font size to be 7
		
		int fontStyle; // generating different fontStyle like Bold or Italic or both
		if (Math.random() > 0.25)
			fontStyle = Font.ITALIC;
		else if (Math.random() > 0.5)
			fontStyle = Font.BOLD;
		else
			fontStyle = Font.ITALIC + Font.BOLD;
		
		s.setFont( new Font(fontFamilies[fontType], fontStyle, fontSize )); // putting all together. Set the font using new Font with the random properties generate before. 

		
		int angle = new Random().nextInt(359); // generating different value for the word rotation. the range is from 0 to 359, as 360 degree == 0 degree
		s.setRotationAngle(angle);
		
		
		int scale = new Random().nextInt(5); // give different scale for the word "tag"
		if (scale == 0) // as the smallest value accepted for magnification is 1, hence, when the program generated a 0 value
			scale = 1; // set the value to 1
		s.setMagnification(scale);  // Default is 1, meaning no magnification.
		
		if (Math.random() < 0.5) // Give random condition to have border or not
			s.setBorder(true);  // Default is false, meaning don't draw a border.
		else
			s.setBorder(false);
		
		
		s.setTextTransparency(0.3); // Default is 0, meaning text is not at all transparent.
		
		
		// Generate random color for the word "tag" background color
		int r = new Random().nextInt(255); // generate random values for r,g,b from the range 0 to 255 (maximum value of rgb)
		int g = new Random().nextInt(255);
		int b = new Random().nextInt(255);
		
		s.setBackground(new Color(r,g,b));  // Default is null, meaning don't draw a background area.
		
		
		// Give random Tansparancy scale for the "tag" background
		s.setBackgroundTransparency(Math.random() * 0.90 + 0.10);  // Default is 0, meaning background is not transparent.
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
			saveImageItem.setAccelerator(KeyStroke.getKeyStroke(commandKey + "shift S")); // adding an extra shortcut key for Save Image... command
			saveImageItem.addActionListener(menuHandler);
			fileMenu.add(saveImageItem);
			
			JMenu editMenu = new JMenu("Edit");
			menuBar.add(editMenu);
			undoMenuItem.addActionListener(menuHandler); // undoItem was created in the constructor
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
			saveImageAsText(); // save all the string info to a file
		}
		else if (command.equals("Open...")) { // read a previously saved file, and reconstruct the list of strings
			openTextFile();
			canvas.repaint(); // (you'll need this to make the new list of strings take effect)
		}
		else if (command.equals("Clear")) {  // remove all strings
			theString = null;   // Remove the ONLY string from the canvas.
			undoMenuItem.setEnabled(false);
			canvas.repaint();
		}
		else if (command.equals("Remove Item")) { // remove the most recently added string
			// to apply undo in the program
			if (theString.size() > 0) { // when the list ArrayList size is more than 0
				theString.remove(theString.size() - 1); // remove the item from the back, by taking the size of ArrayList minus 1 
			}
			
			if (theString.size() == 0) // if the ArrayList empty
				undoMenuItem.setEnabled(false); // set undoMenuItem to false
			
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
		File textFile = fileChooser.getOutputFile(this, "Select Text File Name", "textImage.txt"); // get the desire textFile to save, this is to write the Text file
		if (textFile == null) // When the user did not choose any file
			return; // break from the function
		
		try {
			PrintWriter write = new PrintWriter(textFile); // once the file choose, write the text
			
			write.println("New textImage"); // write a line "New textImage". This is used to check whether the text file with is going to open later on is valid or not.
			
			// write done the canvas background color according to the sequence r,g, and b. This is important as later on, when we read the file, we will be following according to this order
			write.println(canvas.getBackground().getRed());
			write.println(canvas.getBackground().getGreen());
			write.println(canvas.getBackground().getBlue());
			
			
			if (theString != null)
				for (DrawTextItem s: theString) { // make a loop to go through the strings in ArrayList
					write.println("theString:"); // write "theString:" as the title to define the strings properties
					write.println(s.getString()); // first, get the string context
					write.println(s.getX()); // get the string x and y coordinate
					write.println(s.getY());
					write.println(s.getFont().getName()); // get the fontType (families) name, and write into the text file
					write.println(s.getFont().getStyle()); // get the fontStyle as in Bold, Italic, or both 
					write.println(s.getFont().getSize()); // get the fontSize
					write.println(s.getTextColor().getRed()); // get the TextColor in RGB sequence 
					write.println(s.getTextColor().getGreen());
					write.println(s.getTextColor().getBlue());
					write.println(s.getTextTransparency()); // get the fontTransparency
					
					if (s.getBackground() == null) { // When there's no Background color for the "tag"
						write.println(-1); // write -1 into the text file. We will be using this to set the background color of the tag later on
						write.println(-1);
						write.println(-1);
						
					} else { // when the color is found, write down the background RGB values into the textFile 
						write.println(s.getBackground().getRed()); 
						write.println(s.getBackground().getGreen());
						write.println(s.getBackground().getBlue());
					}
					
					write.println(s.getBackgroundTransparency()); // write down the "tag" BackgroundTransparency value
					write.println(s.getBorder()); // write down the boolean logic of the border 
					write.println(s.getMagnification()); // write down the "tag" Magnification scale value
					write.println(s.getRotationAngle()); // write down the "tag" Rotation angle value
				}
			
			// once all value is written down, close PrintWriter
			write.close();

		} catch (Exception e) { // if an error occur, pop up a warning window
			JOptionPane.showMessageDialog(this, 
					"Sorry, an error occurred while trying to save this image progress.\n" + "Error message: " + e);
		}
	}
	
	/*
	 * Open the save progress using a text file
	 */
	private void openTextFile() {
		File openTextFile = fileChooser.getInputFile(this, "Open Saved Text File"); // get the fileName that we want to open
		if (openTextFile == null) // if nothing was selected, break from the function
			return;
		
		try {
			Scanner read = new Scanner(openTextFile); // read the text file using Scanner class
			
			// First, we need to check is the text file that we are going to read is valid.
			if (!read.nextLine().equals("New textImage")) { // to check whether the text file that we are reading is the one that display the artwrok from before
				
					// when the string "New textImage" is not found, which means this text file is invalid, 
					// hence, display error message and break from the function
				JOptionPane.showMessageDialog(this, "Sorry, This is not an valid file. \nPlease try again."); 
				return;
			}
			
			Color savedBg = new Color(read.nextInt(),read.nextInt(),read.nextInt()); // first read for the canvas background color
							// this constructor is creating the color using RGB sequence, hence it is important to save it in the correct sequence in saveImageAsText() method
			
			ArrayList<DrawTextItem> newStrings = new ArrayList<DrawTextItem>(); // create a new ArrayList to temporary store all the Strings informations 
			DrawTextItem newText; // create a new DrawTextItem object
			
			read.nextLine(); // skip one line
			
			while (read.hasNext() && read.nextLine().equals("theString:")) { 
					// when the text file contains more information and has the line "theString:", set the newText style,and store it into the ArrayList
				
				newText = new DrawTextItem(read.nextLine(), read.nextInt(), read.nextInt());
				
				read.nextLine(); // skip one line
				
				newText.setFont(new Font(read.nextLine(), read.nextInt(), read.nextInt())); // set the font style by creating a new Font object. Read the values according to this sequence, 
																							// "fontFamily", fontStyle, and fontSize
				
				newText.setTextColor(new Color(read.nextInt(), read.nextInt(), read.nextInt())); // set the Text color by creating a new Color object
				// 						  ^ this constructor is creating the color using RGB sequence, hence it is important to save it in the correct sequence in saveImageAsText() method
				
				newText.setTextTransparency(read.nextDouble()); // set newText TextTransparency by reading the next double value
				
				int r = read.nextInt(); // read the r,g,b
				int g = read.nextInt();
				int b = read.nextInt();
				if (r == -1) // when the previous "tag" background is none
					newText.setBackground(null); // set newText Background to null
				
				else
					newText.setBackground(new Color(r, g, b)); // else, set the background of newText by using Color constructor
				
				newText.setBackgroundTransparency(read.nextDouble()); // set newText BackgroundTransparency by reading the next double value
				newText.setBorder(read.nextBoolean()); // set newText border by reading the next boolean value
				newText.setMagnification(read.nextDouble()); // set newText Magnification scale by reading the next double value
				newText.setRotationAngle(read.nextDouble()); // set newText rotation angle by reading the next double value
				
				read.nextLine(); // skip one line
				
				newStrings.add(newText); // and store it into the ArrayList
			}
			
			canvas.setBackground(savedBg); // set the canvas bg color to savedBg which we read it from the beginning
			theString = newStrings; // set the contents in theString to newStrings contents
			

		} catch (Exception e) { // if an error occur, pop up a warning window
			JOptionPane.showMessageDialog(this, 
					"Sorry, an error occurred while trying to load the save progress.\n" + "Error message: " + e);
		}
	}
}
