package Interface;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import Engine.*;

public class Board extends JPanel implements FocusListener { private static final long serialVersionUID = 1L;
	private static final int
		HEIGHT = 500,
		WIDTH = 500,
		BORDER = 20,
		LINE_WIDTH = 3;

	BoardPanel boardPanel;
	
	boolean[][] highlights = new boolean[8][8]; //NB initialises as false
	
	MainFrame parent;
	
	public Board(MainFrame parent) {
		this.parent = parent;
		
		setBackground(Color.GRAY);
		setPreferredSize(new Dimension(HEIGHT, WIDTH));
		
		setFocusable(true);
		addFocusListener(this);
		
		setLayout(new GridBagLayout());
		boardPanel = new BoardPanel();
		boardPanel.setPreferredSize(new Dimension(HEIGHT - BORDER, WIDTH - BORDER));
		add(boardPanel);
	}
	
	
	//hack because I CBA to set up a listener
	protected void paintComponent(Graphics arg0) {
		super.paintComponent(arg0);
		resizeBoardPanel();
		
		int margin = 5;
		if (hasFocus()) {
			arg0.setColor(Color.CYAN);
			arg0.drawRect(margin, margin,
					getWidth() - (margin*2) - 1, getHeight() - (margin*2) - 1);
		}
	}
	
	private void resizeBoardPanel() {
		int size = Math.min(getHeight(), getWidth()) - BORDER;
		boardPanel.setPreferredSize(new Dimension(size, size));
	}
	
	
	public void focusGained(FocusEvent e) {
		repaint();
	}
	
	public void focusLost(FocusEvent e) {
		repaint();
	}
	
	
	
	//Custom JPanel that draws the board and pieces
	//Also listens for mouse events dragging the pieces around
	class BoardPanel extends JPanel implements MouseListener, MouseMotionListener {
		private static final long serialVersionUID = 1L;
		
		private BufferedImage imgWhitePawn;
		private BufferedImage imgBlackPawn;
		private BufferedImage imgWhiteKnight;
		private BufferedImage imgBlackKnight;
		private BufferedImage imgWhiteBishop;
		private BufferedImage imgBlackBishop;
		private BufferedImage imgWhiteRook;
		private BufferedImage imgBlackRook;
		private BufferedImage imgWhiteQueen;
		private BufferedImage imgBlackQueen;
		private BufferedImage imgWhiteKing;
		private BufferedImage imgBlackKing;
		
		public BoardPanel () {
			addMouseListener(this);
			addMouseMotionListener(this);
			
			ClassLoader cl = getClass().getClassLoader();
			try {
				imgWhitePawn = ImageIO.read(cl.getResource("pieces/Chess_plt60.png"));
				imgBlackPawn = ImageIO.read(cl.getResource("pieces/Chess_pdt60.png"));
				imgWhiteKnight = ImageIO.read(cl.getResource("pieces/Chess_nlt60.png"));
				imgBlackKnight = ImageIO.read(cl.getResource("pieces/Chess_ndt60.png"));
				imgWhiteBishop = ImageIO.read(cl.getResource("pieces/Chess_blt60.png"));
				imgBlackBishop = ImageIO.read(cl.getResource("pieces/Chess_bdt60.png"));
				imgWhiteRook = ImageIO.read(cl.getResource("pieces/Chess_rlt60.png"));
				imgBlackRook = ImageIO.read(cl.getResource("pieces/Chess_rdt60.png"));
				imgWhiteQueen = ImageIO.read(cl.getResource("pieces/Chess_qlt60.png"));
				imgBlackQueen = ImageIO.read(cl.getResource("pieces/Chess_qdt60.png"));
				imgWhiteKing = ImageIO.read(cl.getResource("pieces/Chess_klt60.png"));
				imgBlackKing = ImageIO.read(cl.getResource("pieces/Chess_kdt60.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// ========================= Painting ===========================
		
		protected void paintComponent(Graphics arg0) {
			super.paintComponent(arg0);
			drawBoard((Graphics2D)arg0,getWidth());
		}
		
		private void drawBoard(Graphics2D g2d,int dim) {
			int size = dim - 1;
			
			g2d.setStroke(new BasicStroke(LINE_WIDTH));
			
			//fill in darksquares
			g2d.setColor(Color.LIGHT_GRAY);
			for (int ii = 0; ii < 8; ii++) {
				for (int jj = 0; jj < 8; jj++) {
					if ((ii + jj) % 2 == 1)
						g2d.fillRect(ii*size/8, jj*size/8, size/8, size/8);
				}
			}
			
			//highlight highlighted squares (possible move squares)
			g2d.setColor(Color.YELLOW);
			for (int ii = 0; ii < 8; ii++) {
				for (int jj = 0; jj < 8; jj++) {
					if (highlights[ii][7 - jj]) //NB invert y axis
						g2d.fillRect(ii * size / 8, jj * size / 8,
								size / 8, size / 8);
				}
			}
			
			//highlight last move
			g2d.setColor(Color.PINK);
			if (parent.getLastMove() != null) {
				int sf = parent.getLastMove().getStartFile();
				int ef = parent.getLastMove().getEndFile();
				int sr = parent.getLastMove().getStartRank();
				int er = parent.getLastMove().getEndRank();
				
				g2d.fillRect(sf * size / 8, (7 - sr) * size / 8,
						size / 8, size / 8);
				g2d.fillRect(ef * size / 8, (7 - er) * size / 8,
						size / 8, size / 8);
			}
			
			//draw gridlines
			g2d.setColor(Color.DARK_GRAY);
			for (int ii = 0; ii < 9; ii++) {
				g2d.drawLine(size*ii/8, 0, size*ii/8, size);
			}
			for (int ii = 0; ii < 9; ii++) {
				g2d.drawLine(0, size*ii/8, size, size*ii/8);
			}
			
			//draw pieces
			for (int ii = 0; ii < 8; ii++) {
				for (int jj = 0; jj < 8; jj++) {
					Piece pc = parent.getCurrentPosition().getPiece(ii, 7 - jj);
					if (dragging && pc.equals(draggedPiece)) { //stationary pieces
						int x = (ii * size / 8) + cx - ox; // start pos + delta
						int y = (jj * size / 8) + cy - oy; // start pos + delta
						drawPiece(g2d, draggedPiece, x, y, size / 8);
					} else { //dragged piece
						drawPiece(g2d, pc,
								ii * size / 8, jj * size / 8, size / 8);
					}
				}
			}
			
		}
		
		
		private void drawPiece(Graphics2D g2d, Piece piece, int x, int y, int size) {
			//TODO piece drawing
			g2d.setFont(new Font("Sans-Serif", Font.BOLD, size/10));
			if (piece.isWhite) {
				g2d.setColor(Color.GREEN);
			} else {
				g2d.setColor(Color.BLACK);
			}

			//if (piece.type != PieceType.EMPTY)
			//	g2d.drawString(piece.toString(), x + (size/5), y + size -(size/5));
			
			if (piece.isWhite) {
				switch(piece.type) {
				case PAWN:
					g2d.drawImage(imgWhitePawn, x, y,null);
					break;
				case KNIGHT:
					g2d.drawImage(imgWhiteKnight, x , y ,null);
					break;
				case BISHOP:
					g2d.drawImage(imgWhiteBishop, x, y,null);
					break;
				case ROOK:
					g2d.drawImage(imgWhiteRook, x, y,null);
					break;
				case QUEEN:
					g2d.drawImage(imgWhiteQueen, x, y,null);
					break;
				case KING:
					g2d.drawImage(imgWhiteKing, x, y,null);
				}
			} else {
				switch(piece.type) {
				case PAWN:
					g2d.drawImage(imgBlackPawn, x, y,null);
					break;
				case KNIGHT:
					g2d.drawImage(imgBlackKnight, x, y,null);
					break;
				case BISHOP:
					g2d.drawImage(imgBlackBishop, x, y,null);
					break;
				case ROOK:
					g2d.drawImage(imgBlackRook, x, y,null);
					break;
				case QUEEN:
					g2d.drawImage(imgBlackQueen, x, y,null);
					break;
				case KING:
					g2d.drawImage(imgBlackKing, x, y,null);
				}
			}
			}

		
		// ========================= Mouse bit ===============================
		
		//original and current dragging coordinates
		private boolean dragging;
		private int ox, oy, cx, cy;
		private Piece draggedPiece;
		
		public void mousePressed(MouseEvent arg0) {
			Board.this.requestFocus();
			
			int file = screenXtofile(arg0.getX());
			int rank = screenYtoRank(arg0.getY());
			
			ox = arg0.getX();
			oy = arg0.getY();
			cx = arg0.getX();
			cy = arg0.getY();
			dragging = true;
			draggedPiece = parent.getCurrentPosition().getPiece(file, rank);
			
			for (Move m:getPossibleMoves(rank, file))
				highlights[m.getEndFile()][m.getEndRank()] = true;
			
			repaint();
		}

		public void mouseDragged(MouseEvent arg0) {
			if (dragging) {
				cx = arg0.getX();
				cy = arg0.getY();
				repaint();
			}
		}
		
		public void mouseReleased(MouseEvent arg0) {
			int oFile = screenXtofile(ox);
			int oRank = screenYtoRank(oy);
			int endFile = screenXtofile(arg0.getX());
			int endRank = screenYtoRank(arg0.getY());
			dragging = false;
			Piece pc = parent.getCurrentPosition().getPiece(oFile, oRank);
			
			for (boolean[] a: highlights)
				Arrays.fill(a, false);
			
			boolean isLegal = false;
			
			for (Move m:parent.getCurrentPosition().legalMoves()) {
				if (m.getEndFile() == endFile &&
						m.getEndRank() == endRank &&
						m.getStartFile() == oFile &&
						m.getStartRank() == oRank ) {
					isLegal = true;
					break;
				}
			}
			
			//if its not human's turn to play, disregard dragged moves
			if ((pc.isWhite && parent.getMode() == MainFrame.CvH ) ||
				(!pc.isWhite && parent.getMode() == MainFrame.HvC ) || 
				parent.getMode() == MainFrame.CvC) {
				repaint();
				return;
			}
				
			
			if (endFile > 7 || endFile < 0) isLegal = false;
			if (endRank > 7 || endRank < 0) isLegal = false;
			if (endRank == oRank && endFile == oFile) isLegal = false;
			
			if (isLegal) {
				Move m = new Move(oFile, oRank, endFile, endRank);
				if ((pc.type == PieceType.PAWN) && ((pc.isWhite && endRank == 7) || !pc.isWhite && endRank == 0)){
					m.promotion = PieceType.QUEEN;
				}
				if ((pc.type == PieceType.PAWN) && (endFile != oFile) && 
						parent.getCurrentPosition().getPiece(endFile, endRank).type == PieceType.EMPTY) {
					m.enPassant = true;
				}
				parent.makeMove(m);
			}
			
			repaint();
			
		}
		
		public void mouseMoved(MouseEvent arg0) {}
		public void mouseClicked(MouseEvent arg0) {}
		public void mouseEntered(MouseEvent arg0) {}
		public void mouseExited(MouseEvent arg0) {}
		
		private int screenXtofile(int x) {
			return x * 8 / getWidth();
		}
		
		private int screenYtoRank(int y) {
			return 7 - (y * 8 / getWidth());
		}
		
		private ArrayList<Move> getPossibleMoves(int rank, int file) {
			ArrayList<Move> moves = parent.getCurrentPosition().legalMoves();
			
			ArrayList<Move> movesForSelected = new ArrayList<Move>(); 
			
			for (Move m: parent.getCurrentPosition().legalMoves()) {
				if ( m.getStartFile() == file && m.getStartRank() == rank ) {
					movesForSelected.add(m);
				}
			}
			
			return movesForSelected;
		}
	}
	
}
