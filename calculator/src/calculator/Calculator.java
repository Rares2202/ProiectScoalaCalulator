package calculator;
// HOLY IMPORTS
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import java.util.ArrayList;
import java.util.Stack;
//Definirea clasei principale Calculator
public class Calculator extends JFrame implements ActionListener, ChangeListener {
    private static final long serialVersionUID = 7068187129092456057L;

    private JTextField display; //campul de text pentru afisarea rezultatelor
    private StringBuilder input = new StringBuilder();// String builder pt a stoca inputul utilizatorului
    private JPanel panel; //panou pt butoane
    private JRadioButton lightThemeButton; //buton light theme
    private JRadioButton darkThemeButton; //buton dark theme
    private JList<String> historyList; //lista pt istoricul calculelor
    private DefaultListModel<String> historyModel; //modelul listei de istoric
    private JSlider zoomSlider; //slider zoom
    private JMenuBar menuBar; //bara de meniuri
    private JMenu fontMenu; //meniu pt schimbare font
    private JPanel themePanel;
    private ArrayList<String> history = new ArrayList<>();//initializarea listei istoric

    private JSplitPane splitPane;
 
    //constructorul clasei calculator
    public Calculator() {
        // Crearea ferestrei principale
        setTitle("Calculator"); //setare titlu fereastra
        setSize(600, 600); //dimensiunea ferestrei
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //setare comportament inchidere fereastra
        getContentPane().setLayout(new BorderLayout()); //setare layout 

        // Creare display
        display = new JTextField();
        display.setEditable(false); //display non-editabil ( nu scriu direct pe display )
        display.setFont(new Font("Segoe UI", Font.PLAIN, 30)); //setare font implicit display
        display.setHorizontalAlignment(JTextField.RIGHT); //aliniere text in display
        display.setPreferredSize(new Dimension(0,80));
        getContentPane().add(display, BorderLayout.NORTH); //adaug display-ul pe fereastra, aliniind-ul sus
        

        // Creare meniu
        menuBar = new JMenuBar(); 
        fontMenu = new JMenu("Schimba Fontul"); //meniul schimbare font
        menuBar.add(fontMenu);
        setJMenuBar(menuBar); //setare bara de meniuri

        // Adaugare optiuni de font
        String[] fonts = {"Segoe UI", "Arial", "Verdana", "Tahoma"};
        for (String font : fonts) {
            JMenuItem menuItem = new JMenuItem(font); //crearea unui nou item de meniu pt fiecare font
            menuItem.addActionListener(this); //adaugare ascultator de actiune pt fiecare item de meniu
            fontMenu.add(menuItem); //adaugare item in meniu
        }

        // Creare slider pentru zoom
        zoomSlider = new JSlider(JSlider.HORIZONTAL, 50, 200, 100); //slider cu valori
        zoomSlider.addChangeListener(this);//ascultator de schimbari pe slider
      
        // Asculta tastatura
        display.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                char c = e.getKeyChar();
                if (Character.isDigit(c) || c == '.' || c == '+' || c == '-' || c == '*' || c == '/' || c == '^' || c == '(' || c == ')') {
                    input.append(c); //adaugarea caracterului in input
                    display.setText(input.toString()); // afisarea inputului in display
                } else if (c == '\b' && input.length() > 0) {
                    input.setLength(input.length() - 1); //stergerea ultimului caracter din input
                    display.setText(input.toString()); //actualizare display
                } else if (c == '\n') { // Enter
                    try {
                        double result = eval(input.toString()); //calculare rezultat
                        String resultString = input.toString() + " = " + result; //creare string rezultat
                        display.setText(String.valueOf(result)); //afisare rezultat
                        addHistory(resultString); //adaugare rezultat in istoric
                        input.setLength(0); // resetare input
                    } catch (Exception ex) {
                        display.setText("Error"); // afisare mesaj eroare
                        input.setLength(0); //resetarea input
                    }
                }
            }
        });

        // Creare butoane pentru tema
        themePanel = new JPanel(new GridLayout(1,2)); //creez panou tema
        lightThemeButton = new JRadioButton("Tema Luminoasa"); 
        darkThemeButton = new JRadioButton("Tema Intunecata");

        ButtonGroup themeGroup = new ButtonGroup();
        themeGroup.add(lightThemeButton);
        themeGroup.add(darkThemeButton);

        themePanel.add(lightThemeButton);
        themePanel.add(darkThemeButton);

 
        
        darkThemeButton.setSelected(true); // Seteaza tema intunecata ca implicita

        //adaug listener pentru butoanele de tema
        lightThemeButton.addActionListener(this);
        darkThemeButton.addActionListener(this);

      splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,zoomSlider, themePanel);
      splitPane.setDividerLocation(200);
      
        
        getContentPane().add(splitPane, BorderLayout.SOUTH);

        // Creare butoane
        String[] buttonLabels = {  //lista etichete butoane
            "%", "CE", "C", "←",
            "(", ")", "^", "÷",
            "7", "8", "9", "*",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "+/-", "0", ".", "="
        };

        panel = new JPanel();
        panel.setLayout(new GridLayout(6, 4)); //grid layout ca nu am chef sa impart pe foaie pixeli

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Segoe UI", Font.PLAIN, 24));
            button.addActionListener(this);
            
            button.setFocusPainted(false); //elimin chenarul din jurul etichetei butonului
            

            
          //  button.setBorderPainted(false);
            
            panel.add(button);
        }
        // pun butoanele in centru
        getContentPane().add(panel, BorderLayout.CENTER);

        // Creare lista istoric de tip historyModel
        historyModel = new DefaultListModel<>();
        historyList = new JList<>(historyModel);
        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyList.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        // Asculta click pe istoric
        historyList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedValue = historyList.getSelectedValue();
                if (selectedValue != null) {
                    input.setLength(0);
                    input.append(selectedValue.split(" = ")[0]); // Preia doar expresia
                    display.setText(input.toString());
                }
            }
        });

        JScrollPane historyScrollPane = new JScrollPane(historyList); 
        historyScrollPane.setPreferredSize(new Dimension(200, 600));
        getContentPane().add(historyScrollPane, BorderLayout.EAST);

        applyDarkTheme();  // Aplica tema intunecata implicit

        
        setVisible(true);
    }

    @Override
    //actiunile pe butoane
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        
        // Schimbare font
        if (command.equals("Segoe UI") || command.equals("Arial") || command.equals("Verdana") || command.equals("Tahoma")) {
            Font newFont = new Font(command, Font.PLAIN, 24);
            display.setFont(newFont.deriveFont(30f));
            historyList.setFont(newFont.deriveFont(18f));
            for (Component c : panel.getComponents()) {
                if (c instanceof JButton) {
                    JButton button = (JButton) c;
                    button.setFont(newFont);
                }
            }
        } else if (e.getSource() == lightThemeButton) { // buton de light theme
            applyLightTheme();
        } else if (e.getSource() == darkThemeButton) { //buton de dark theme
            applyDarkTheme();
        }else if(command.equals("+/-")) { // schimbarea semnului expresiei de pe display
        	try {
        		double currentValue = Double.parseDouble(display.getText());
        		currentValue = -currentValue;
        		display.setText(String.valueOf(currentValue));
        		input.setLength(0);
        		input.append(display.getText());
        	}catch(NumberFormatException ex) { //daca am exceptie, afisez error pe ecran
        		display.setText("Error");
        	}
        }
        else if ("0123456789.".contains(command)) {
            input.append(command);
            display.setText(input.toString());
        } else if (command.equals("C")) {
            input.setLength(0);
            display.setText("");
        }
        
        else if (command.equals("CE")) {
        	input.setLength(0);
        	display.setText("");
        	history.clear();
        	historyModel.clear();
        }
        
        else if (command.equals("←")) {
            if (input.length() > 0) {
                input.setLength(input.length() - 1);
                display.setText(input.toString());
            }
        } else if (command.equals("=")) {
            try {
                double result = eval(input.toString());
                String resultString = input.toString() + " = " + result;
                display.setText(String.valueOf(result));
                addHistory(resultString);
                input.setLength(0);
                display.requestFocusInWindow();
            } catch (Exception ex) {
                display.setText("Error");
                input.setLength(0);
                display.requestFocusInWindow();
            }
        } else {
            input.append(" ").append(command).append(" ");
            display.setText(input.toString());
            
        }
    }

    @Override //strict pt butonul de zoom, scalez tot din fereastra dupa nivelul respectiv de zoom
    public void stateChanged(ChangeEvent e) {
        int zoomLevel = zoomSlider.getValue();
        float zoomFactor = zoomLevel / 100.0f;

        display.setFont(display.getFont().deriveFont(30 * zoomFactor));
        historyList.setFont(historyList.getFont().deriveFont(18 * zoomFactor));
        
        for (Component c : panel.getComponents()) {
            if (c instanceof JButton) {
                JButton button = (JButton) c;
                button.setFont(button.getFont().deriveFont(24 * zoomFactor));
            }
        }

        // Redimensionarea ferestrei
        setSize((int)(600 * zoomFactor), (int)(600 * zoomFactor));
    }

    private void addHistory(String entry) { //aici am comentat liniile care scoteau din historyModel operatii cand acestea erau la numar mai multe de 10
  //      if (history.size() >= 10) {
 //           history.remove(0);
  //      }
        history.add(entry);
        historyModel.addElement(entry);
   //     if (historyModel.getSize() > 10) {
  //          historyModel.remove(0);
  //      }
    }

    private double eval(String expression) { //impart inputul in doua stive, una de valori numerice si una de operatori
        //inante de evaluarea expresiei, apelez metoda preprocessExpression pentru a adauga inmultire langa paranteze
    	// nu merge :(
    	//expression = preprocessExpression(expression);
    	
    	Stack<Double> values = new Stack<>();
        Stack<Character> ops = new Stack<>();
        //parcurg fiecare caracter al expresiei
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            //ignor spatiile albe
            if (c == ' ')
                continue;

            //construieste numerele
            if (Character.isDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.'))
                    sb.append(expression.charAt(i++));
                i--;
                values.push(Double.parseDouble(sb.toString())); //adaug numarul in stiva de valori
            } else if (c == '(') { //adaug parantezele deschise in stiva de valori
                ops.push(c);
            } else if (c == ')') { //adaug parantezele inchise in stiva de valori pana la paranteza deschisa
                while (ops.peek() != '(')
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                ops.pop(); //elimin paranteza deschisa din stiva
            } else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '^') {
                while (!ops.isEmpty() && hasPrecedence(c, ops.peek()))
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                ops.push(c); //adaug operatorul curent in stiva
            }
        }

        while (!ops.isEmpty()) //aplic operatorii ramasi in stiva 
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));
        			//returnez rezultatul final
        return values.pop();
    }
//metoda care verifica precedenta operatorilor 
    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') //parantezele au precedenta cea mai mica, deci orice operator are prioritate in fata lor
            return false;
        if ((op1 == '*' || op1 == '/' || op1 == '^') && (op2 == '+' || op2 == '-'))
            return false;
        if (op1 == '^' && (op2 == '*' || op2 == '/'))
            return false;
        return true;
    }

    private double applyOp(char op, double b, double a) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0)
                    throw new UnsupportedOperationException("Cannot divide by zero");
                return a / b;
            case '^':
                return Math.pow(a, b);
        }
        return 0;
    }

    private void applyLightTheme() {
        panel.setBackground(Color.WHITE);
        for (Component c : panel.getComponents()) {
            if (c instanceof JButton) {
                JButton button = (JButton) c;
                button.setBackground(Color.LIGHT_GRAY);
                button.setForeground(Color.BLACK);
                button.setFont(new Font("Segoe UI", Font.PLAIN, 24));
            }
        }
        display.setBackground(Color.WHITE);
        display.setForeground(Color.DARK_GRAY);
        display.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        historyList.setBackground(Color.WHITE);
        historyList.setForeground(Color.DARK_GRAY);
        historyList.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        
        //schimbare culori butoane tema
        lightThemeButton.setBackground(Color.WHITE);
        lightThemeButton.setForeground(Color.DARK_GRAY);
        darkThemeButton.setBackground(Color.WHITE);
        darkThemeButton.setForeground(Color.DARK_GRAY);
        
        splitPane.setBackground(Color.WHITE);
        splitPane.setForeground(Color.DARK_GRAY);
        splitPane.setUI(new BasicSplitPaneUI() {
        	@Override 
        	public BasicSplitPaneDivider createDefaultDivider() {
        		return new BasicSplitPaneDivider(this) {
        			@Override
        			public void setBackground(Color color) {
        				super.setBackground(Color.LIGHT_GRAY);
        			}
        			public void paint(Graphics g) {
        				super.paint(g);
        				g.setColor(Color.LIGHT_GRAY);
        				g.fillRect(0,0,getWidth(), getHeight());
        				g.setColor(Color.DARK_GRAY);
        				g.drawRect(0, 0, getWidth()-1, getHeight()-1);
        			}
        		};
        	}
        });
        
        
        zoomSlider.setBackground(Color.WHITE);
        zoomSlider.setForeground(Color.DARK_GRAY);
        menuBar.setBackground(Color.WHITE);

    }

    private void applyDarkTheme() {
    	
        panel.setBackground(Color.DARK_GRAY);
        for (Component c : panel.getComponents()) {
            if (c instanceof JButton) {
                JButton button = (JButton) c;
                button.setBackground(Color.DARK_GRAY);
                button.setForeground(Color.WHITE);
                button.setFont(new Font("Segoe UI", Font.PLAIN, 24));
            }
  
        }
        panel.setBackground(Color.DARK_GRAY);
        display.setBackground(Color.DARK_GRAY);
        display.setForeground(Color.WHITE);
        display.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        historyList.setBackground(Color.DARK_GRAY);
        historyList.setForeground(Color.WHITE);
        historyList.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        
        lightThemeButton.setBackground(Color.DARK_GRAY);
        lightThemeButton.setForeground(Color.WHITE);
        darkThemeButton.setBackground(Color.DARK_GRAY);
        darkThemeButton.setForeground(Color.WHITE);
        
        splitPane.setBackground(Color.DARK_GRAY);
        splitPane.setForeground(Color.WHITE);
        splitPane.setUI(new BasicSplitPaneUI() {
        	@Override 
        	public BasicSplitPaneDivider createDefaultDivider() {
        		return new BasicSplitPaneDivider(this) {
        			@Override
        			public void setBackground(Color color) {
        				super.setBackground(Color.DARK_GRAY);
        			}
        			public void paint(Graphics g) {
        				super.paint(g);
        				g.setColor(Color.GRAY.darker().darker());
        				g.fillRect(0, 0, getWidth(), getHeight());
        				g.setColor(Color.BLACK.brighter().brighter().brighter());
        				g.drawRect(0, 0, getWidth()-1, getHeight()-1);
        			}
        		};
        	}
        });
        
        zoomSlider.setBackground(Color.DARK_GRAY);
        zoomSlider.setForeground(Color.WHITE);
        menuBar.setBackground(Color.GRAY);
   
    }

 // cod pentru o metoda care sa preproceseze expresia din display astfel incat sa tratez 
 // o paranteza in absenta unui operator ca inmultire
	/*
	 * private String preprocessExpression(String expression) { StringBuilder
	 * preprocessed = new StringBuilder(); for (int i = 0; i < expression.length();
	 * i++) { char c = expression.charAt(i);
	 * 
	 * // Adaugă operatorul de înmulțire între număr/paranteză închisă și paranteză
	 * deschisă if (i > 0 && c == '(' && (Character.isDigit(expression.charAt(i -
	 * 1)) || expression.charAt(i - 1) == ')')) { preprocessed.append('*'); }
	 * 
	 * // Adaugă operatorul de înmulțire între paranteză închisă și număr if (i > 0
	 * && Character.isDigit(c) && expression.charAt(i - 1) == ')') {
	 * preprocessed.append('*'); }
	 * 
	 * preprocessed.append(c); } return preprocessed.toString(); }
	 */

}


