package Engine;
import java.util.ArrayList;
import java.util.Collection;

//lists possible piece types
//each has a method that lists the piece's legal moves in a given position 
public enum PieceType {
	
	EMPTY(0.0) {
		public String symbol() { return ""; }
		public Collection<Move> moves(int file, int rank, Position pos) {
			return new ArrayList<Move>();
		}
	},
	
	// ================================ PAWN ================================
	PAWN(1.0) { 
		public String symbol() { return ""; }
		public Collection<Move> moves(int file, int rank, Position pos) {
			return Moves.pawnMoves(file, rank, pos);
		}
	},
	
	// ================================ KNIGHT ================================
	KNIGHT(3.0) {
		public String symbol() { return "N"; }
		public Collection<Move> moves(int file, int rank, Position pos) {
			return Moves.knightMoves(file, rank, pos);
		}
	},
	
	// ================================ BISHOP ================================
	BISHOP(3.25) {
		public String symbol() { return "B"; }
		public Collection<Move> moves(int file, int rank, Position pos) {
			return Moves.bishopMoves(file, rank, pos);
		}
	},
	
	// ================================ ROOK ================================
	ROOK(5.0) {
		public String symbol() { return "R"; }
		public Collection<Move> moves(int file, int rank, Position pos) {
			return Moves.rookMoves(file, rank, pos);
		}
	},
	
	// ================================ QUEEN ================================
	QUEEN(9.0) {
		public String symbol() { return "Q"; }
		public Collection<Move> moves(int file, int rank, Position pos) {
			return Moves.queenMoves(file, rank, pos);
			}
	},
	
	// ================================ KING ================================
	KING(100.0) {
		public String symbol() { return "K"; }
		public Collection<Move> moves(int file, int rank, Position pos) {
			return Moves.kingMoves(file, rank, pos);
			}
	};
	
	public double value;
	
	private PieceType(double value) { this.value = value; }
	
	public abstract Collection<Move> moves(int file, int row, Position pos);
	
	public abstract String symbol();
	
}

class Moves {
	
	//Tests if a proposed move is obviously nonsense: 
	// - a piece can't move outside the bounds of the board.
	// - a piece can't move to a square occupied by a piece of the same colour
	static protected boolean isValidMove(
			int file, int rank, int endFile, int endRank, Position pos) {
		if (endFile > 7) return false;
		if (endFile < 0) return false;
		if (endRank > 7) return false;
		if (endRank < 0) return false;
		
		if (pos.getPiece(file,rank).isWhite == pos.getPiece(endFile,endRank).isWhite &&
				pos.getPiece(endFile,endRank).type != PieceType.EMPTY) {
			return false;
		}
		
		return true;
	}
	
	//adds moves for a piece moving repeatedly in one direction - i.e. B, R & Q
	//stops on contact with edge of the board or a piece (plus possible capture)
	//dx and dy specify the "steps" the piece moves in
	static protected void addMovesForRunner(
			int file, int rank, int dx, int dy,
			Collection<Move> moves, Position pos) {
		
		int endRank, endFile;
		for (int ii = 1; ; ii++) {
			endRank = rank + (ii * dy);
			endFile = file + (ii * dx);
			if (endFile < 0) break;
			if (endFile > 7) break;
			if (endRank < 0) break;
			if (endRank > 7) break;
			if (pos.getPiece(endFile,endRank).type == PieceType.EMPTY) {
				moves.add(new Move(file, rank, endFile, endRank));
			} else {
				if (pos.getPiece(endFile,endRank).isWhite != 
						pos.getPiece(file,rank).isWhite) {
					moves.add(new Move(file, rank, endFile, endRank));
				}
				break;
			}
		}
	}
	
	static public Collection<Move> pawnMoves(int file, int rank, Position pos) {
		Piece pc = pos.getPiece(file,rank);
		ArrayList<Move> moves = new ArrayList<Move>();
		int endRank;
		
		if(pos.whiteToMove) {
			endRank = rank + 1;
		} else {
			endRank = rank - 1;
		}
		
		//forward move
		if (pos.getPiece(file,endRank).type == PieceType.EMPTY) {
			
			//promotions
			if ((pc.isWhite && rank == 6) || !pc.isWhite && rank == 1) {
				moves.add(new Move(file, rank, file, endRank, PieceType.QUEEN));
				moves.add(new Move(file, rank, file, endRank, PieceType.ROOK));
				moves.add(new Move(file, rank, file, endRank, PieceType.KNIGHT));
				moves.add(new Move(file, rank, file, endRank, PieceType.BISHOP));
			} else {
				moves.add(new Move(file, rank, file, endRank));
				
				//double move forward
				if (pc.isWhite && rank == 1) {
					if (pos.getPiece(file,rank + 2).type == PieceType.EMPTY) {
						moves.add(new Move(file, rank, file, rank + 2));
					}
				}
				if (!pc.isWhite && rank == 6) {
					if (pos.getPiece(file,rank - 2).type == PieceType.EMPTY) {
						moves.add(new Move(file, rank, file, rank - 2));
					}
				}	
			}
		}
		
		//captures
		if (file + 1 < 8) {
			if ( (pos.getPiece(file + 1,endRank).type != PieceType.EMPTY) &&
				 (pos.getPiece(file + 1,endRank).isWhite != pc.isWhite) ) {
				if ((pc.isWhite && rank == 6) || !pc.isWhite && rank == 1) {
					moves.add(new Move(file, rank, file + 1, endRank, PieceType.QUEEN));
					moves.add(new Move(file, rank, file + 1, endRank, PieceType.ROOK));
					moves.add(new Move(file, rank, file + 1, endRank, PieceType.KNIGHT));
					moves.add(new Move(file, rank, file + 1, endRank, PieceType.BISHOP));
				} else moves.add(new Move(file, rank, file + 1, endRank));
			}
		}
		if (file -1 >= 0) {
			if ( (pos.getPiece(file - 1,endRank).type != PieceType.EMPTY) &&
				 (pos.getPiece(file - 1,endRank).isWhite != pc.isWhite) ) {
				if ((pc.isWhite && rank == 6) || !pc.isWhite && rank == 1) {
					moves.add(new Move(file, rank, file - 1, endRank, PieceType.QUEEN));
					moves.add(new Move(file, rank, file - 1, endRank, PieceType.ROOK));
					moves.add(new Move(file, rank, file - 1, endRank, PieceType.KNIGHT));
					moves.add(new Move(file, rank, file - 1, endRank, PieceType.BISHOP));
				} else moves.add(new Move(file, rank, file - 1, endRank));
			}
		}
		
		//TODO enpassant captures
		
		return moves;
	}
	
	static public Collection<Move> knightMoves(int file, int row, Position pos) {
		ArrayList<Move> moves = new ArrayList<Move>();
		
		//go through each of the 8 possible moves
		//they are not possible if out of bounds or occupied by an enemy piece
		if(isValidMove(file, row, file + 1, row + 2, pos)) {
			moves.add(new Move(file, row, file + 1, row + 2));
		}
		if(isValidMove(file, row, file + 2, row + 1, pos)) {
			moves.add(new Move(file, row, file + 2, row + 1));
		}
		if(isValidMove(file, row, file - 1, row - 2, pos)) {
			moves.add(new Move(file, row, file - 1, row - 2));
		}
		if(isValidMove(file, row, file - 2, row - 1, pos)) {
			moves.add(new Move(file, row, file - 2, row - 1));
		}
		if(isValidMove(file, row, file - 1, row + 2, pos)) {
			moves.add(new Move(file, row, file - 1, row + 2));
		}
		if(isValidMove(file, row, file - 2, row + 1, pos)) {
			moves.add(new Move(file, row, file - 2, row + 1));
		}
		if(isValidMove(file, row, file + 1, row - 2, pos)) {
			moves.add(new Move(file, row, file + 1, row - 2));
		}
		if(isValidMove(file, row, file + 2, row - 1, pos)) {
			moves.add(new Move(file, row, file + 2, row - 1));
		}
		return moves;
	}
	
	static public Collection<Move> bishopMoves(int file, int rank, Position pos) {
		ArrayList<Move> moves = new ArrayList<Move>();
		addMovesForRunner(file, rank,  1,  1, moves, pos);
		addMovesForRunner(file, rank, -1,  1, moves, pos);
		addMovesForRunner(file, rank, -1, -1, moves, pos);
		addMovesForRunner(file, rank,  1, -1, moves, pos);
		return moves;
	}
	
	static public Collection<Move> rookMoves(int file, int rank, Position pos) {
		ArrayList<Move> moves = new ArrayList<Move>();
		addMovesForRunner(file, rank,  1,  0, moves, pos);
		addMovesForRunner(file, rank,  0,  1, moves, pos);
		addMovesForRunner(file, rank, -1,  0, moves, pos);
		addMovesForRunner(file, rank,  0, -1, moves, pos);
		return moves;
	}
	
	static public Collection<Move> queenMoves(int file, int rank, Position pos) {
		ArrayList<Move> moves = new ArrayList<Move>();
		addMovesForRunner(file, rank,  1,  1, moves, pos);
		addMovesForRunner(file, rank, -1,  1, moves, pos);
		addMovesForRunner(file, rank, -1, -1, moves, pos);
		addMovesForRunner(file, rank,  1, -1, moves, pos);
		addMovesForRunner(file, rank,  1,  0, moves, pos);
		addMovesForRunner(file, rank,  0,  1, moves, pos);
		addMovesForRunner(file, rank, -1,  0, moves, pos);
		addMovesForRunner(file, rank,  0, -1, moves, pos);
		return moves;
	}
	
	static public Collection<Move> kingMoves(int file, int row, Position pos) {

		ArrayList<Move> moves = new ArrayList<Move>();
		
		//go through each of the 8 possible moves
		//they are not possible if out of bounds or occupied by an enemy piece
		if(isValidMove(file, row, file + 1, row, pos)) {
			moves.add(new Move(file, row, file + 1, row));
		}
		if(isValidMove(file, row, file - 1, row, pos)) {
			moves.add(new Move(file, row, file - 1, row));
		}
		if(isValidMove(file, row, file, row + 1, pos)) {
			moves.add(new Move(file, row, file, row + 1));
		}
		if(isValidMove(file, row, file, row - 1, pos)) {
			moves.add(new Move(file, row, file, row - 1));
		}
		if(isValidMove(file, row, file + 1, row + 1, pos)) {
			moves.add(new Move(file, row, file + 1, row + 1));
		}
		if(isValidMove(file, row, file - 1, row - 1, pos)) {
			moves.add(new Move(file, row, file - 1, row - 1));
		}
		if(isValidMove(file, row, file - 1, row + 1, pos)) {
			moves.add(new Move(file, row, file - 1, row + 1));
		}
		if(isValidMove(file, row, file + 1, row - 1, pos)) {
			moves.add(new Move(file, row, file + 1, row - 1));
		}
		//White K-side Castling
		//if (!pos.isCheck()) {
			if(		pos.castleRights[0] &&
					file == 4 && row == 0 &&
					pos.getPiece(5, 0).type == PieceType.EMPTY &&
					pos.getPiece(6, 0).type == PieceType.EMPTY &&
					pos.getPiece(7, 0).type == PieceType.ROOK &&
					pos.getPiece(7, 0).isWhite) {
				moves.add(new Move(4,0,6,0));
			}
			//White Q-side Castling
			if(		pos.castleRights[1] &&
					file == 4 && row == 0 &&
					pos.getPiece(1, 0).type == PieceType.EMPTY &&
					pos.getPiece(2, 0).type == PieceType.EMPTY &&
					pos.getPiece(3, 0).type == PieceType.EMPTY &&
					pos.getPiece(0, 0).type == PieceType.ROOK &&
					pos.getPiece(0, 0).isWhite) {
				moves.add(new Move(4,0,2,0));
			}
			//Black K-side Castling
			if(		pos.castleRights[2] &&
					file == 4 && row == 7 &&
					pos.getPiece(5, 7).type == PieceType.EMPTY &&
					pos.getPiece(6, 7).type == PieceType.EMPTY &&
					pos.getPiece(7, 7).type == PieceType.ROOK &&
					!pos.getPiece(7, 7).isWhite) {
				moves.add(new Move(4,7,6,7));
			}
			//Black Q-side Castling
			if(		pos.castleRights[3] &&
					file == 4 && row == 7 &&
					pos.getPiece(1, 7).type == PieceType.EMPTY &&
					pos.getPiece(2, 7).type == PieceType.EMPTY &&
					pos.getPiece(3, 7).type == PieceType.EMPTY &&
					pos.getPiece(0, 7).type == PieceType.ROOK &&
					!pos.getPiece(0, 7).isWhite) {
				moves.add(new Move(4,7,2,7));
			}
		//}
		//TODO rules to prevent castling out of or through check
		return moves;
	
	}
		
}