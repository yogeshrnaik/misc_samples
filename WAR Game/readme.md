Assignment—A War Game
The playing field (or the board).
First move
Either side can start the game by making the first move. Then, each player taking turns to make
the moves until game finish.
Game play
Battle is a two player game. One player controls blue tokens (also called pieces) and the other
player controls the yellow tokens. Tokens can only move diagonally forward along the dark
squares, one square at the time. There are two types of tokens, a lower strength token worth 10
units and a higher strength token worth 20-unit.
When the token moves into an occupied square (called the battle square), a battle takes place and
a losing token is removed from the game board. Who wins or looses the battle of the square is
decided according to the battle rules explained below.

The game finish when one player looses all the tokens or if there are no valid moves left. The
winner of the game is chosen on the basis of strength not on number of tokens left on the board.
The player with greatest strength wins the game. If both players finish with the same overall
strength then a draw is declared.
Battle rules
The total strength of the attacking (or defending) token is obtained by summing the strength of
all the friendly tokens in contact with the battle square. An element of luck is introduced through
a random strength multiplier. A random number integer between 5 and 10 (inclusively) is
obtained (use Maths.random() method) for the blue army and another random number for the
yellow army. The total strength of the blue army is then multiplied by their multiplier and,
similarly, the strength of the yellow army is multiplied by their own multiplier.
For example, if the blue token attacks the battle square, as shown in the diagram above, then the
blue army have the attacking strength of 40 units and the yellow army defends with 20 units as
indicated by the dotted squares. Let’s say the respective blue and yellow random multipliers are
6 and 10. The attacking strength is 240 and defending strength is 200 units so the blue token
wins and takes over the battle square. The yellow token is removed from the board. If the result
is a draw, blue token withdraws to the original square.
Moving Tokens
Use the mouse to “drag” the token where you want it to go. The token should be always centred
on its square. Of course, only the legal moves are allowed. Any attempts to drag the token to a
wrong square should result in an “Illegal move” error message displayed on the applet and when
the mouse button is released the token returns to the previous position. Mouse dragging should
be implemented via MouseListener class. The motion begins in the mousePressed() method and
ends in mouseReleased().
Note: Although not covered in the lecture notes, you may use mouseDragged() method but
there are no additional marks allocated for using this method.
Random Multiplier
Use a random number generator to calculate the integer multiplier with a minimum value of
value 5 and maximum value 10.

Buttons and Text Fields
At least one buttons is required to reset the game. Two text fields requesting the names the players (the
player can choose to leave it blank). Two more text fields (initiated to 0 at the start of the game)
indicating the strength of the blue and yellow tokens (including strength of their supporting tokens)
involved in the most recent battle. An additional text field is required for the various messages, e.g.,
“Illegal move” or “Blue wins” etc.
Comments
•
You are only allowed to use the library classes and methods covered in the lectures (wit a
exception of the MouseDragged() methods). You are permitted to use the standard Java arrays
and swing library. Be warned, you are likely to fail the assignment if you choose to break this
rules.
•
The Java code should be fully documented.
• Use loops were possible (and reasonable).
• Applet must have the board, the tokens with their the unit strength, text fields and buttons.
• Use an array of objects to represent the tokens. Define appropriate attributes (strength, colour,
location, old location etc.). Other useful objects and arrays, as required.
• You are encouraged to discuss the assignment with your fellow students but the actual design
and coding must be strictly your own work. Sharing code with other students is forbidden.
Plagiarism will not be tolerated, no excuses!
Assignment
1. Design an applet displaying the board and the tokens as shown in the diagram above. 6 marks
2. Design Token class. 4 marks
4. Design token movement, only legal moves allowed. 4 marks
3. Implement Token object and arrays. 4 marks
5. The battle square logic and the end game logic. 2 marks
Submission
•
Due date Friday 11, June 2004.
• Late assignments will be penalised 2 marks per day Submit the assignment only in an
electronic form—the java source code and the class files.
• Email your assignment to Dr J Szajman scm5800@melba.vu.edu.au. All emails will be
acknowledged within two working days. If you did not receive the acknowledgment within two
working days, please resend the assignment.
• Submit only the final version of the assignment. Subsequent emails with the updated files will
be ignored.
• The email must include the following information. Failure to supply the requested information
may result in a failure.
ID, last and first name,
subject code
course code
tutor’s name.
• All attached files must also have comment lines with the above information.
