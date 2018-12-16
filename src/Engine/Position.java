package Engine;
import java.util.ArrayList;


public class Position {
	
	/* 2D array representing the positions of pieces on the board
	*  file then rank, from bottom left:
	*  a1 is board[1][1]
	*  a8 is board[1],[8]
	*  h1 is board[8][1]
	*/
	private Piece[][] board = new Piece[8][8];
	
	public boolean whiteToMove;
	
	boolean[] enpassants = {
			false,false,false,false,
			false,false,false,false };
	
	//White K-side, White Q-side, Black K-side, Black Q-side 
	boolean[] castleRights = {true, true, true, true};
	
	
	// ******************** CONSTRUCTORS *****************************************
	
	//Constants to use in constructor args
	public enum PosType { BLANK, INIT }
	
	public Position(PosType posType){
		
		whiteToMove = true;
		
		switch(posType) {
			case BLANK:
				board = blankBoard();
				castleRights = new boolean[] {false, false, false, false};
				break;
			case INIT:
				board = initBoard();
				castleRights = new boolean[] {true, true, true, true};
		}
	}
	
	//creates new position with identical (copied) attributes to the old position
	public Position(Position p){
		whiteToMove = p.whiteToMove;
		
		for (int ii = 0; ii < 8; ii++) {
			for (int jj = 0; jj < 8; jj++ ) {
				board[ii][jj] = new Piece(
						p.board[ii][jj].type,
						p.board[ii][jj].isWhite
				);
			}
		}
		
		for (int ii = 0; ii < p.enpassants.length; ii++) {
			enpassants[ii] = p.enpassants[ii];
		}
		
		for (int ii = 0; ii < p.castleRights.length; ii++) {
			castleRights[ii] = p.castleRights[ii];
		}
	}
	
	//creates a new board from an array of strings representing the position
	public Position(String[][] brd, boolean whiteToMove) {
		this.whiteToMove = whiteToMove;
		
		for (int ii = 0; ii < 8; ii++) {
			for (int jj = 0; jj < 8; jj++ ) {
				board[ii][7-jj] = pieceFromStr(brd[jj][ii]);
			}
		}
		
	}
	
	//Returns an appropriate new piece object for "K", ".", "r", etc.
	//Should this be a constructor in Piece rather than a random factory method?
	private static Piece pieceFromStr(String string) {
		if		(string.equals("K")) return new Piece(PieceType.KING,	true);
		else if	(string.equals("Q")) return new Piece(PieceType.QUEEN,	true);
		else if	(string.equals("B")) return new Piece(PieceType.BISHOP,	true);
		else if	(string.equals("N")) return new Piece(PieceType.KNIGHT,	true);
		else if	(string.equals("R")) return new Piece(PieceType.ROOK,	true);
		else if	(string.equals("P")) return new Piece(PieceType.PAWN,	true);
		else if	(string.equals("k")) return new Piece(PieceType.KING,	false);
		else if	(string.equals("q")) return new Piece(PieceType.QUEEN,	false);
		else if	(string.equals("b")) return new Piece(PieceType.BISHOP,	false);
		else if	(string.equals("n")) return new Piece(PieceType.KNIGHT,	false);
		else if	(string.equals("r")) return new Piece(PieceType.ROOK,	false);
		else if	(string.equals("p")) return new Piece(PieceType.PAWN,	false);
		else 						 return new Piece(PieceType.EMPTY,	true);
	}

	private Piece[][] blankBoard() {
		
		Piece[][] brd = new Piece[8][8];
		
		for (int ii = 0; ii<8; ii++) {
			for (int jj = 0; jj<8; jj++ ) {
				brd[ii][jj] = new Piece(PieceType.EMPTY,true);
			}
		}
		
		return brd;
	}
	
	private Piece[][] initBoard() {
		Piece[][] brd = blankBoard();
		
		//fill up the 2nd and 7th ranks with pawns
		//set all pieces on the 7th and 8th ranks to be black
		for(int ii = 0; ii<8;ii++) {
			brd[ii][1].type = PieceType.PAWN;
			brd[ii][6].type = PieceType.PAWN;
			brd[ii][6].isWhite = false;
			brd[ii][7].isWhite = false;
		}
		
		initPiecesHelper(brd, 0, PieceType.ROOK);
		initPiecesHelper(brd, 1, PieceType.KNIGHT);
		initPiecesHelper(brd, 2, PieceType.BISHOP);
		initPiecesHelper(brd, 3, PieceType.QUEEN);
		
		brd[4][0].type = PieceType.KING;
		brd[4][7].type = PieceType.KING;
		
		return brd;
	}
	
	private void initPiecesHelper(Piece[][] brd, int offset, PieceType type) {
		brd[0 + offset][0].type = type;
		brd[7 - offset][0].type = type;
		brd[0 + offset][7].type = type;
		brd[7 - offset][7].type = type;
	}
	
	// **************** PUBLIC FUNCTIONS ***************************
	
	//Removes the piece from its old position and replaces destination square with it
	//returns the updated position (which is the same object)
	public Position makeMove(Move move) {
		PieceType tmp = board[move.startF][move.startR].type;
		boolean isWhite = board[move.startF][move.startR].isWhite;
		
		//Update the piece type if it is a promotion move
		//NB no pawn on 8th check - this is deliberately general to support variants
		if (move.promotion != null) tmp = move.promotion;
		
		board[move.startF][move.startR].type = PieceType.EMPTY;
		board[move.endF][move.endR].type = tmp;
		board[move.endF][move.endR].isWhite = isWhite;
		
		
		//If this move is long castling, also move the rook
		if (tmp == PieceType.KING && move.startF == 4 && move.endF == 2) {
			board[0][move.startR].type = PieceType.EMPTY;
			board[3][move.startR].type = PieceType.ROOK;
			board[move.endF][move.endR].isWhite = isWhite;
		}
		//If this move is short castling, also move the rook
		if (tmp == PieceType.KING && move.startF == 4 && move.endF == 6) {
			board[7][move.startR].type = PieceType.EMPTY;
			board[5][move.startR].type = PieceType.ROOK;
			board[move.endF][move.endR].isWhite = isWhite;
		}
		
		
		//Check if this move invalidates any castling rights
		if (tmp == PieceType.KING) {
			if (isWhite) {
				castleRights[0] = false;
				castleRights[1] = false;
			} else {
				castleRights[2] = false;
				castleRights[3] = false;
			}
		}
		
		
		if (move.startF == 7) {
			if (move.startR == 0) {
				castleRights[0] = false;
			} else if (move.startR == 7) {
				castleRights[2] = false;
			}
		}
		if (move.startF == 0) {
			if (move.startR == 0) {
				castleRights[1] = false;
			} else if (move.startR == 7) {
				castleRights[3] = false;
			}
		}
		
		
		//if this move is enpassant, remove the enpassanted pawn 
		if (move.enPassant) {
			if (whiteToMove) { board[move.endF][move.endR-1].type = PieceType.EMPTY; }
				else { board[move.endF][move.endR+1].type = PieceType.EMPTY; }
		}
		
		//set enpassant possibilities for next turn
		for (int ii=0; ii<8;ii++) enpassants[ii] = false;
		if (tmp == PieceType.PAWN && Math.abs(move.endR - move.startR) > 1)
			enpassants[move.endF] = true;
		
		
		whiteToMove = !whiteToMove;
		
		return this;
	}
	
	private ArrayList<Move> legalMoves;
	
	//returns a list of all legal moves from this position 
	public ArrayList<Move> legalMoves() {
		if (legalMoves != null) return legalMoves; 
		
		legalMoves = new ArrayList<Move>();
		
		//Iterate through the board's squares.
		//If it contains a piece of the side with the move,
		// look at which squares the piece can move to.
		for (int ii = 0; ii < 8; ii++) {
			for (int jj = 0; jj < 8; jj++ ) {
				if(board[ii][jj].isWhite == whiteToMove) {
					legalMoves.addAll(board[ii][jj].type.moves(ii,jj,this));
				}
			}
		}
		
		//enpassant
		for(int ii=0;ii<7;ii++) {
			if (whiteToMove &&
					enpassants[ii] &&
					board[ii+1][4].isWhite &&
					board[ii+1][4].type == PieceType.PAWN 
				){
				legalMoves.add(new Move(ii+1, 4, ii, 5,true));
			};
			
			if (whiteToMove &&
					enpassants[ii+1] &&
					board[ii][4].isWhite &&
					board[ii][4].type == PieceType.PAWN 
					){
				legalMoves.add(new Move(ii, 4, ii+1, 5,true));
			};
			if (!whiteToMove &&
					enpassants[ii] &&
					!board[ii+1][3].isWhite &&
					board[ii+1][3].type == PieceType.PAWN 
				){
				legalMoves.add(new Move(ii+1, 3, ii, 2,true));
			};
			
			if (!whiteToMove &&
					enpassants[ii+1] &&
					!board[ii][3].isWhite &&
					board[ii][3].type == PieceType.PAWN 
					){
				legalMoves.add(new Move(ii, 3, ii+1, 2,true));
			};
		
		}
		
		//weed out moves that result in an illegal position (i.e. in check)
		for(int ii =0 ;ii < legalMoves.size(); ii++) {
			if (endsInCheck(legalMoves.get(ii))) {
				legalMoves.remove(ii);
				ii--;
			}
		}
		
		
		return legalMoves;
	}
	
	private boolean endsInCheck(Move move) {
		
		Position p  = new Position(this);
		p.makeMove(move);
		
		//Iterate through the board's squares.
		//If it a piece of the side with the move can take the king, return true,
		for (int ii = 0; ii < 8; ii++) {
			for (int jj = 0; jj < 8; jj++ ) {
				if(p.board[ii][jj].isWhite == p.whiteToMove) { 
					for (Move m: p.board[ii][jj].type.moves(ii, jj, p) ) {
						if (p.board[m.endF][m.endR].type == PieceType.KING) return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean isCheck() {
		//Iterate through the board's squares.
		//If it a piece of the side with the move can take the king, return true,
		for (int ii = 0; ii < 8; ii++) {
			for (int jj = 0; jj < 8; jj++ ) {
				if(board[ii][jj].isWhite != whiteToMove) { 
					for (Move m: board[ii][jj].type.moves(ii, jj, this) ) {
						if (board[m.endF][m.endR].type == PieceType.KING) return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean isMate() {
		return ( isCheck() && legalMoves().isEmpty());
	}
	
	public String toString() {
		String str = "------------------- ";
		
		if (whiteToMove) str = str + " White to move\n";
			else str = str + " Black to move\n";
		
		for (int ii = 7; ii >= 0; ii--) {
			str = str + "|";
			for (int jj = 0; jj<8; jj++ ) {
				str = str + " "+ board[jj][ii];
			}
			str = str + " |\n";
		}
		
		str = str + "-------------------";
		
		str = str + "  Raw Evaluation = " + rawEval();
		
		return str;
	}
	
	// EVAL FUNCTION
	//TODO: Improve evaluation function
	public double rawEval() {
		
		//if it's Checkmate return large
		//if it's Stalemate return 0
		if (legalMoves().isEmpty()) {
			if (isCheck()) {
				if (whiteToMove) return -1000;
					else return 1000;
			} else {
				return 0;
			}
		}
		
		double eval = 0;
		
		//sum up material values
		for (Piece[] file:board) {
			for(Piece pc:file) {
				eval += pc.value();
			}
		}
		
		/*  Pawn structure:
		 *  + Far advanced pawns
		 *  + Central control
		 *  - Isolated pawns
		 *  - Backward pawns
		 *  - Doubled pawns
		 */
		
		
		/* Pieces:
		 * + Sum up No. squares controlled by the pieces
		 * + Bishop pair
		 */
		
		/* Dynamics:
		 * - Activity
		 * - Pins
		 * - Loose pieces
		 * - X-rays
		 * - King safety
		 */
		for (int ii = 0; ii < 8; ii++) {
			for (int jj = 0; jj < 8; jj++ ) {
				eval += board[ii][jj].type.moves(ii,jj,this).size() * 
							(board[ii][jj].isWhite ? 0.01 : -0.01);
			}
		}
		
		
		
		
		//slight bonus for side with move?
		//if (whiteToMove) eval += 0.1; else eval -= 0.1;
		
		return ((double)Math.round(eval*100))/100;
	}

	public Piece getPiece(int file, int rank) {
		return board[file][rank];
	}
	
}

