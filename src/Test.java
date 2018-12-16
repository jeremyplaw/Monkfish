import java.util.ArrayList;

import Engine.Move;
import Engine.PieceType;
import Engine.Position;
import Engine.PositionTree;
import Interface.MainFrame;


public class Test {
	
	public static void main(String[] args) {
		
		Position p;
		p = new Position(Position.PosType.INIT);
		
		/*
		p = new Position(
				new String[][] {
					{" "," "," "," "," "," "," ","k"},
					{" "," "," "," "," "," "," "," "},
					{" ","r"," "," "," "," "," "," "},
					{" "," "," "," "," "," "," "," "},
					{" "," "," "," "," "," "," "," "},
					{" "," "," "," "," "," "," "," "},
					{" "," "," "," "," ","P","P","P"},
					{" "," "," "," "," "," ","K"," "},
				},false);
		
		*/
		p = new Position(
				new String[][] {
					{" "," "," "," "," "," "," ","k"},
					{" "," "," "," "," "," "," "," "},
					{" "," "," "," "," "," "," "," "},
					{" "," "," "," "," "," "," "," "},
					{" "," "," "," "," "," "," "," "},
					{" "," "," "," "," "," "," ","P"},
					{" "," "," "," "," ","P","P"," "},
					{" ","r"," "," "," "," ","K"," "},
				},true);
		
		
		PositionTree pt = new PositionTree(p,3);
		
		System.out.println(pt.position);
		System.out.println(pt.evaluate());
		System.out.println("children: " + pt.getChildCount());
		System.out.println("leaves: " + pt.getLeaves().size());
		System.out.println(pt.topMove().toString(pt.position));
		System.out.println(pt.principleVariation());
		
		for (String str: pt.allPvs()) {
			System.out.println(str);
		}
	}
	
	static private void exampleFrame() {
		Position p = new Position(
				new String[][] {
					{" "," "," "," "," "," "," ","k"},
					{" "," "," "," "," "," "," "," "},
					{" ","r"," "," "," "," "," "," "},
					{" "," "," "," "," "," "," "," "},
					{" "," "," "," "," "," "," "," "},
					{" "," "," "," "," "," "," "," "},
					{" "," "," "," "," ","P","P","P"},
					{" "," "," "," "," "," ","K"," "},
				},false);
		
		MainFrame mf = new MainFrame();
		mf.setNewPosition(p);
		
	}
	
	
	//Sets up a rook odds position and makes a few moves
	//prints the board at every stage
	static private void exampleMoves() {
		Position p = new Position(Position.PosType.INIT);
		p.getPiece(0,0).type = PieceType.EMPTY;
		System.out.println(p);
		
		System.out.println(p.makeMove(new Move("e2","e4")));
		System.out.println(p.makeMove(new Move("e7","e5")));
		System.out.println(p.makeMove(new Move("f2","f4")));
		System.out.println(p.makeMove(new Move("e5","f4")));	
	}
	
	//Builds a tree 2 deep
	//Gets leaf nodes and picks a random one
	static private void buildTreePrintRandomLeaf(Position p, int depth) {
		
		System.out.println("Starting position:");
		System.out.println(p);
		
		PositionTree pt = new PositionTree(p,depth);
		
		System.out.println("Depth "+depth+" tree: " +
				pt.getLeaves().size() + " nodes:");
		
		//show a random leaf node
		System.out.println(
			pt.getLeaves().get(
				(int)(pt.getLeaves().size() * Math.random())
			).position
		);
	}
	
	//lists possible moves from a position
	static private void listMoves(Position p) {
		if (p == null) p = new Position(Position.PosType.INIT);
		
		System.out.println(p);
		
		ArrayList<Move> moves = p.legalMoves();
		System.out.println(moves.size() + " legal moves:");
		
		for (Move m:moves) {
			System.out.println(m.toString(p));
		}
	}
	
	//evaluates a position using minimax
	static private void evaluate(Position p, int depth, boolean printLeaves) {
		if (p == null) p = new Position(Position.PosType.INIT);
		
		System.out.println(p);
		
		PositionTree pt = new PositionTree(p,depth);
		
		System.out.println("Tree depth: " + depth);
		System.out.println(pt.getLeaves().size() + " leaf nodes");
		System.out.println("evaluation = " + pt.evaluate());
		
		//show all leaves
		if (printLeaves) {
			for (PositionTree leaf:pt.getLeaves()) {
				System.out.println(leaf.position.toString());
			}
		}
	}
}
