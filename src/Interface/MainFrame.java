package Interface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import Engine.*;
import Engine.Position.PosType;


public class MainFrame extends JFrame implements KeyListener {
	private static final long serialVersionUID = 1L;
	
	private static final int
		MIN_W = 380,
		MIN_H = 200,
		SIDEBAR_W = 200;

	private Board board;
	private PositionTree gameTree;
	
	private JTextArea output;
	private JLabel evalLabel = new JLabel("Evaluation: "); 
	
	private int mode = HvH;
	public static final int
		HvH = 1,
		HvC = 2,
		CvH = 3,
		CvC = 4;
	
	private OpeningBook book;
	
	public static void main(String[] args) {
		
		System.out.println((new Position(Position.PosType.INIT)).getFen());
		
		new MainFrame();
	}
	
	public MainFrame() {
		setLayout(new BorderLayout());
		setTitle("Chess");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		
		doContents();
		doMenus();
		
		gameTree = new PositionTree(new Position(PosType.INIT));
		updateOutput();
		
		
		book = new OpeningBook();
		//TODO book = new OpeningBook("books/TestBook.csv");
		
		
		setMinimumSize(new Dimension(MIN_W,MIN_H));
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void doContents() {
		board = new Board(this);
		board.addKeyListener(this);
		
		add(board,BorderLayout.CENTER);
		
		JPanel sidePanel = new JPanel();
		
		sidePanel.setPreferredSize(new Dimension(SIDEBAR_W,0));
		sidePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		sidePanel.setLayout(new BorderLayout());
		JButton playMoveButton = new JButton("Play Move");
		playMoveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				think();
			}
		});
		sidePanel.add(playMoveButton,BorderLayout.NORTH);
		
		output = new JTextArea("");
		output.setEditable(false);
		output.setMargin(new Insets(10,10,10,10));
		output.setFont(new Font("SansSerif",Font.BOLD,16));
		output.setTabSize(3);
		JScrollPane scrollPane = new JScrollPane(output);
		sidePanel.add(scrollPane, BorderLayout.CENTER);
		
		JPanel bottomPanel = new JPanel(); 
		bottomPanel.add(evalLabel);
		sidePanel.add(bottomPanel, BorderLayout.SOUTH);
		
		add(sidePanel,BorderLayout.EAST);
		
	}
	
	private void doMenus() {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu gameMenu = new JMenu("Game");
		gameMenu.setMnemonic(KeyEvent.VK_G);
		
		JMenuItem menuItem = new JMenuItem("Reset Board", KeyEvent.VK_T);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				resetBoard();
		}});
		gameMenu.add(menuItem);
		gameMenu.addSeparator();
		menuItem = new JMenuItem("Undo move", KeyEvent.VK_U);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				undoMove();
		}});
		gameMenu.add(menuItem);
		menuItem = new JMenuItem("Redo move", KeyEvent.VK_R);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				redoMove();
		}});
		gameMenu.add(menuItem);
		gameMenu.addSeparator();
		menuItem = new JMenuItem("Flip baord", KeyEvent.VK_F);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				flipBoard();
		}});
		gameMenu.add(menuItem);
		menuBar.add(gameMenu);
		
		JMenu modeMenu = new JMenu("Mode");
		ButtonGroup group = new ButtonGroup();
		
		JRadioButtonMenuItem rbmenuItem = new JRadioButtonMenuItem("Computer plays neither");
		rbmenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mode = HvH;
		}});
		modeMenu.add(rbmenuItem);
		group.add(rbmenuItem);
		rbmenuItem.setSelected(true);
		rbmenuItem = new JRadioButtonMenuItem("Computer plays black");
		rbmenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mode = HvC;
		}});
		
		modeMenu.add(rbmenuItem);
		group.add(rbmenuItem);
		rbmenuItem = new JRadioButtonMenuItem("Computer plays white");
		rbmenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mode = CvH;
		}});
		modeMenu.add(rbmenuItem);
		group.add(rbmenuItem);
		
		gameMenu.addSeparator();
		menuItem = new JMenuItem("Computer plays next 10 moves");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for (int x = 0; x < 10 ; x++) {
					think();
				}
		}});
		modeMenu.add(menuItem);
		
		menuBar.add(modeMenu);
		
		setJMenuBar(menuBar);
	}
	
	public Position getCurrentPosition() {
		return gameTree.position;
	}
	
	public int getMode() {
		return mode;
	}
	
	public void makeMove(Move m) {
		
		PositionTree newTree = new PositionTree(new Position(gameTree.position).makeMove(m));
		gameTree.addChild(newTree, m);
		gameTree = newTree;
		updateOutput();
		
		if ((mode == HvC && !gameTree.position.whiteToMove) ||
				(mode == CvH && gameTree.position.whiteToMove)) {
			repaint();
			think();
		}
	}
	
	public void resetBoard() {
		gameTree = new PositionTree(new Position(PosType.INIT));
		output.setText("");
		updateOutput();
		repaint();
	}
	
	public void undoMove() {
		if (gameTree.parent != null) {
			gameTree = gameTree.parent;
			updateOutput();
			repaint();
		}
	}
	
	public void redoMove() {
		if (gameTree.getChildCount() > 0) {
			gameTree = gameTree.getChild(gameTree.getChildCount()-1);
			updateOutput();
			repaint();
		}
	}
	
	public void flipBoard() {
		//TODO flipBoard()
	}
	
	public void setNewPosition(Position p) {
		gameTree = new PositionTree(p);
		updateOutput();
		repaint();
	}
	
	public void updateOutput() {
		evalLabel.setText("Evaluation:   " + gameTree.position.rawEval());
		
		PositionTree node = gameTree;
		while (node.parent != null) node = node.parent;
		
		int moveNum = 1; 
		String outText;
		if (node.position.whiteToMove) {
			outText = "";
		} else {
			outText = moveNum + "        ...\t";
			moveNum++;
		}
		
		while (node.getChildCount() > 0 ) {
			node = node.getChild(node.getChildCount()-1);
			if (!node.position.whiteToMove) {
				outText = outText + moveNum + "    ";
				moveNum++;
			}
			outText = outText + node.getLastMove().toString(node.parent.position); //NEED NODE PARENT
			if (node.position.whiteToMove) {
				outText = outText + "\n";
			} else {
				outText = outText + "\t";
			}
		}
		
		output.setText(outText);
	}
	
	// =================== KeyListener bit =================
	@Override
	public void keyPressed(KeyEvent ke) {
		switch (ke.getKeyCode()) {
		case KeyEvent.VK_LEFT: 	undoMove();			break;
		case KeyEvent.VK_RIGHT: redoMove();		break;
		case KeyEvent.VK_SPACE: think();//resetBoard();		break;
		case KeyEvent.VK_F: 	flipBoard();		break;
		case KeyEvent.VK_E: 	updateOutput();	break;
		}
	}
	
	public void keyReleased(KeyEvent ke) {}
	public void keyTyped(KeyEvent ke) {}
	
	
	//================= Debug bit =========
	
	
	private void think() {
		
		if (book.contains(gameTree.position)) {			
			System.out.println("in book!");
			makeMove(book.getMove(gameTree.position));
			repaint();
		} else {
			new Thread( new Runnable() {
				
				public void run() {
					System.out.println();
					PositionTree pt = new PositionTree(gameTree.position,3);
					
					System.out.println(pt.position);
					System.out.println("children: " + pt.getChildCount());
					pt.evaluate();
					System.out.println("Analysis:");
					for (String str: pt.allPvs()) {
						System.out.println(str);
					}
					//JOptionPane.showMessageDialog(board, pt.topMove().toString(pt.position));
					
					Move topMove = pt.topMove();
					if (topMove != null) makeMove(topMove);
					repaint();
				}
			}).start();
		}
	}
}
