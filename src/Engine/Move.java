package Engine;

public class Move {
	
	//integers representing the start and end rows and files of the piece moved
	int startF,
		startR,
		endF,
		endR;
	
	public Move(int startF, int startR, int endF, int endR) {
		this.startF = startF;
		this.startR = startR;
		this.endF = endF;
		this.endR = endR;
	}
	
	public PieceType promotion;
	public boolean enPassant;
	
	//Accepts a string of start and end squares, e.g:
	// Move("e2","e4") or Move("g1","f3")
	//TODO error checking
	public Move(String startSq, String endSq) {
		startF = strToFileIndex(startSq.substring(0, 1));
		startR = Integer.parseInt(startSq.substring(1, 2)) - 1; 
		endF = strToFileIndex(endSq.substring(0, 1));
		endR = Integer.parseInt(endSq.substring(1, 2)) - 1;
	}
	
	//constructor for a move using normal algebraic notation.
	//requires a position so it can work out the implied starting squares
	public Move(String move, Position p) {
		//TODO: Algebraic move constructor
	}
	
	//constructors for promotion moves.
	//constructors without a PieceType argument leave this field null
	public Move(String move, Position p,PieceType promotion) {
		this(move,p);
		this.promotion = promotion;
	}
	
	public Move(String startSq, String endSq,PieceType promotion) {
		this(startSq, endSq);
		this.promotion = promotion;
	}
	
	public Move(int startF, int startR, int endF, int endR, PieceType promotion) {
		this(startF, startR, endF, endR);
		this.promotion = promotion;
	}
	
	public Move(int startF, int startR, int endF, int endR, boolean isEnpassant) {
		this(startF, startR, endF, endR);
		this.enPassant = true;
	}

	//a position agnostic move string of the form "a1 - c3"
	public String toString() {
		String str = fileIndexToStr(startF) + (startR+1) + " - " + fileIndexToStr(endF) + (endR+1);
		
		if (promotion != null) str = str + "=" + promotion.symbol();
		if (enPassant) str = str + " EP";
		
		return str;
	}
	
	//Gives a proper algebraic move string, requiring a starting position for context
	public String toString(Position pos) {
		Piece startPc = pos.getPiece(startF, startR);
		Piece endPc = pos.getPiece(endF, endR);
		
		String str = startPc.type.symbol();
		
		if (endPc.type != PieceType.EMPTY) {
			if (startPc.type == PieceType.PAWN) {
				str = str + fileIndexToStr(startF);
			}
			str = str + "x";
		}
		
		//TODO specify starting square for ambiguous moves 
		
		str = str + fileIndexToStr(endF) + (endR+1);
				
		if (promotion != null) {
			str = str + "=" + promotion.symbol();
		}
		
		//Castling
		if (startPc.type == PieceType.KING && startF == 4 && endF == 2) {
			str = "0-0-0";
		}
		if (startPc.type == PieceType.KING && startF == 4 && endF == 6) {
			str = "0-0";
		}
		return str;
	}
	
	private int strToFileIndex(String s) {
		if 		(s.equals("a")) return 0; 
		else if (s.equals("b")) return 1;
		else if (s.equals("c")) return 2;
		else if (s.equals("d")) return 3;
		else if (s.equals("e")) return 4;
		else if (s.equals("f")) return 5;
		else if (s.equals("g")) return 6;
		else if (s.equals("h")) return 7;
		
		System.out.println("halp - bad column");
		return -1; //
	}
	
	private String fileIndexToStr(int index) {
		switch (index) {
		case 0: return "a";
		case 1: return "b";
		case 2: return "c";
		case 3: return "d";
		case 4: return "e";
		case 5: return "f";
		case 6: return "g";
		case 7: return "h";
		}

		System.out.println("halp - bad column");
		return "halp"; //
	}

	public int getStartFile() { return startF; }
	public int getStartRank() { return startR; }
	public int getEndFile() { return endF; }
	public int getEndRank() { return endR; }
	
}
