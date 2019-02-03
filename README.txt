
Monkfish is a toy  chess engine, written in Java, with a simple GUI.

The engine itself uses a simple 3 ply minimax search, and leaves are evaluated using piece values + a simple mobility modifier (or number of moves to forced mate)

It doesn't know that castling out of/through check is illegal, and will make threefold repetitions in clearly winning positions.

In other words, it is a very bad engine.

Still, it basically works, and it has a 1-0 record against people with biochemistry PhDs. :D



STUFF TO DO:

Basics:
- detect threefold draw (and 50 move draw?)
- Fixing castling.
- Rewriting the internal board representation to be less grossly inefficient. Should help with memory and help get more depth
- implement enough UCI to be able to test with pychess?

Eval function:
- Square dependent piece evaluations
- Reduce bonus for queen mobility
- Factor in king safety modifier

Search:
- Alpha beta pruning
- Iteratively deepening search with updates to console
- A transposition table table
- Search extensions: check, captures, etc.
- Late move reductions
- other pruning/extending heutristics?

Longer term ideas:
- endgame tablebases?
- clock management

GUI improvements:
- Promoting to non-queens
- Thinking indicator
- Export to pgn
- Score graph
- Clock
- Variations in notation
- Engine output window, showing:
  - Fen
  - PV for top ~5 candidates
  - Depth
  - Number of positions searched
  - Thinking time



Misc Bugs:
- Double tapping spacebar (starting two think() threads) causes the engine to a move, then remove the piece it just moved from the board. Need to disable the second thread.
