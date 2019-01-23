
Monkfish is a toy  chess engine, written in Java, with a simple GUI.

The engine itself uses a simple 3 ply minimax search, and leaves are evaluated using piece values + a simple mobility modifier (or number of moves to forced mate)

It doesn't know that Castling out of/through check is illegal, and it doesn't know you can promote to things that aren't Queens.

In other words, it is a very bad engine.

Still, it basically works, and it has a 1-0 record against people with biochemistry PhDs. :D



STUFF TO DO:

Basics:
- Fixing castling and promoting.
- Rewriting the internal board representation to be less grossly inefficient. Should help with memory and help get more depth
- implement enough UCI to be able to test with pychess?

Eval function:
- Square dependent piece evaluations
- Reduce bonus for queen mobility
- Factor in king safety modifier

Search:
- Alpha beta pruning
- A transposition table table
- Search extensions: check, captures, etc.
- Late move reductions
- other pruning/extending heutristics?

Longer term ideas:
- opening book
- endgame tablebases?
- clock management

GUI improvements:
- Highlight last move
- Running the engine in a separate thread
- Clock
- Export to pgn
- variations
- Score graph


