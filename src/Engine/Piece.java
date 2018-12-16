package Engine;

public class Piece {
	
	public Piece(PieceType type, boolean isWhite) {
		this.type = type;
		this.isWhite = isWhite;
	}
	
	public PieceType type;
	public boolean isWhite;
	
	public String toString() {
		if (isWhite) {
			switch(type) {
			case ROOK: 		return "R";
			case BISHOP: 	return "B";
			case KNIGHT: 	return "N";
			case QUEEN: 	return "Q";
			case KING: 		return "K";
			case PAWN:		return "P";
			case EMPTY: 	return ".";
			}
		} else {
			switch(type) {
			case ROOK: 		return "r";
			case BISHOP: 	return "b";
			case KNIGHT: 	return "n";
			case QUEEN: 	return "q";
			case KING: 		return "k";
			case PAWN:		return "p";
			case EMPTY: 	return ".";
			}
		}
		System.out.println("Unrecognised piecetype in Piece.toString()");
		return null; //this really shouldn't happen
	}
	
	public double value() {
		if (isWhite) {
			return type.value;
		} else {
			return type.value * -1;
		}
	}
}
