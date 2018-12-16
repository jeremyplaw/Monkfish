package Engine;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class PositionTree {
	
	
	ArrayList<PositionTree> children = new ArrayList<PositionTree>();
	public PositionTree parent;
	
	public Position position;
	
	private Move lastMove;
	
	public PositionTree(Position p) {
		position = p;
	}
	
	
	//builds a tree of all legal moves from a start position to a given depth
	public PositionTree(Position root, int depth) {
		position = root;
		
		if (depth > 0) {
			for (Move m:root.legalMoves()) {
				PositionTree child = new PositionTree(
					new Position(root).makeMove(m),
					depth - 1
				);
				addChild(child,m);
			}
		}
	}
	
	// ****** The crux of it! **************
	//uses the the minimax algorithm to evaluate the tree
	//TODO upgrade to alpha beta algorithm
	public double evaluate() {
		
		if (cachedEval != NOCACHE) {
			return cachedEval;
		}
		
		// If this is a leaf note, then use Raw eval. Leaves can be either:
		// - positions at max tree depth
		// - a position with no legal moves (stalemate/checkmate)
		if (children.size() == 0)
			return position.rawEval();
		
		//if this node has children, work out the best and worse cases 
		double  maxeval = -1000000,
				mineval = +1000000;
		
		for (PositionTree child:children) {
			double evaluation = child.evaluate();
			if (evaluation > maxeval) maxeval = evaluation;
			if (evaluation < mineval) mineval = evaluation;
		}
		
		Collections.sort(children, PositionTreeComparator.PTCOMP);
		if (position.whiteToMove) Collections.reverse(children);
		
		
		
		//if white to move, return the max evaluation of the children
		//if black to move, return the min evaluation of the children 
		if (position.whiteToMove) {
			//if eval is a mate sequence knock 1 off for each move away from mate
			//this persuades the computer to play a quickest mate
			if(maxeval > 500) maxeval -= 1;
			cachedEval = maxeval;
			return maxeval;
		} else {
			//if eval is a mate sequence knock 1 off for each move away from mate
			//this persuades the computer to play a quickest mate
			if(maxeval < -500) maxeval += 1;
			cachedEval = mineval;
			return mineval;
		}
	}
	
	private static final double NOCACHE = 123456789;
	private double cachedEval = NOCACHE;
	
	//List of leaves. Lazily created and cached after first getLeaves() call
	//NB this returns a reference to the original list, so don't fuck with it!
	//TODO sort out cacheing with changing the data
	public ArrayList<PositionTree> getLeaves() {
		if (null != leaves) return leaves;
		
		leaves = new ArrayList<PositionTree>();
		
		for (PositionTree child:children) {
			leaves.addAll(child.getLeaves()); //for(PositionTree leaf:child.getLeaves()) leaves.add(leaf);
		}
		
		if (children.size() == 0) leaves.add(this);
		
		return leaves;
	}
	
	public PositionTree favouriteChild() {
		
		if (children.size() == 0) return null;
		
		PositionTree favouriteChild = children.get(0);
		
		for (PositionTree child:children) {
			if (position.whiteToMove){
				if (child.evaluate() > favouriteChild.evaluate()) favouriteChild = child;
			} else { 
				if (child.evaluate() < favouriteChild.evaluate()) favouriteChild = child;
			}
		}
		return favouriteChild;
	}
	
	public Move topMove() {
		PositionTree favouriteChild = favouriteChild();
		if (favouriteChild == null) return null;
		else return favouriteChild().lastMove;
	}
	
	public String principleVariation() {
		if (children.size() == 0) {
			return "";
		} else {
			return topMove().toString(position) + "\t" + favouriteChild().principleVariation();
		}
	}
	
	public String[] allPvs() {
		String[] pvs = new String[children.size()];
		
		int ii = 0;
		for (PositionTree child:children) {
			pvs[ii] = child.evaluate() + " : " + child.lastMove.toString(position) +
					"\t" + child.principleVariation();
			ii++;
		}
		
		return pvs;
	}
	
	private ArrayList<PositionTree> leaves;
	
	public void addChild(PositionTree pt, Move lastMove) {
		children.add(pt);
		pt.parent = this;
		pt.lastMove = lastMove;
	}
	
	public int getChildCount() {
		return children.size();
	}
	
	public PositionTree getChild(int index) {
		return children.get(index);
	}


	public Move getLastMove() {
		return lastMove;
	}
	
	
}

class PositionTreeComparator implements Comparator<PositionTree> {
	@Override
	public int compare(PositionTree arg0, PositionTree arg1) {
		return Double.compare(arg0.evaluate(),arg1.evaluate());
	}
	static PositionTreeComparator PTCOMP = new PositionTreeComparator();
}

