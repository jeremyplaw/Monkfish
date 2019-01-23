package Interface;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import Engine.Move;
import Engine.Position;

public class OpeningBook {
	
	//Associates FENs with policies on moves to play in that position
	private HashMap<String,Policy> book = new HashMap<String,Policy>();
	
	//For Testing
	public OpeningBook() {
		
		Policy pol1 = new Policy();
		pol1.add(new Move("d2","d4"), 0.5);
		pol1.add(new Move("e2","e4"), 0.5);
		book.put("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w",pol1);
		
		Policy pol2 = new Policy();
		pol2.add(new Move("e7","e5"), 0.5);
		pol2.add(new Move("c7","c5"), 0.5);
		book.put("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b",pol2);
		
	}
	
	//Loads from classpath
	public OpeningBook(String bookpath) {
		
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(bookpath);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		
		String line = "";
		
		try {
			while ((line = reader.readLine()) != null) {
	
	            String[] vals = line.split(","); // parse comma separated value line
	            
	            //String fen = vals[0]; first comment field not used , just for human readability of the file.
	            String fen = vals[1];
	            
	            Policy pol = new Policy();
	            
	            for (int ii=2;ii<vals.length;ii+=2) {
	            	
	            	double prob = Double.valueOf(vals[ii]); 
	            	String moveStr = vals[ii+1];
	            	
	            	moveStr = moveStr.replace("-", "").replace(" ", ""); // converts "e1-e3" or "d2 d4" or "g1f3"
	            	
	            	moveStr = moveStr.replace("a", "1").replace("b", "2").replace("c", "3").replace("d", "4").
	            			replace("e", "5").replace("f", "6").replace("g", "7").replace("h", "8"); //convert letters to numbers
	            	
	            	Move mv = new Move(
	            			Integer.valueOf(moveStr.substring(0,1))-1, //startfile
	            			8-Integer.valueOf(moveStr.substring(1,2)), //startrank
	            			Integer.valueOf(moveStr.substring(2,3))-1, //endfile
	            			8-Integer.valueOf(moveStr.substring(3,4)) //endrank
	            	);
	            	
	            	pol.add(mv, prob);
	            	
	            }
			}
			
		} catch (Exception e) { }
			
	}
	
	public boolean contains(Position position) {
		return book.containsKey(position.getFen());
	}
	
	public Move getMove(Position position) {
		Policy policy = book.get(position.getFen());
		return policy.getMove();
	}
	
	
	//policies are a list of moves to play in the position, associated with probabilities (or weights if sum!= 1) of playing each.	
	class Policy {
		
		private HashMap<Move,Double> moveprobs = new HashMap<Move,Double>();
		
		private void add(Move mv, double prob) { moveprobs.put(mv,prob); }
		 
		private Move getMove() {
			
			double totProb = 0;
			for (double prob : moveprobs.values()) {
				totProb += prob;
			}
			
			double index = Math.random() * totProb;
			
			
			for (Move mv : moveprobs.keySet()) {
				double prob = moveprobs.get(mv);
				if (index < prob)
					return mv;
				else
					index -= prob;
			}
			
			//shouldn't get to this point.
			return null; //moveprobs.keySet().toArray(new Move[0])[0];
		}
	}

}
