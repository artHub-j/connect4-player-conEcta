# <img src="https://github.com/artHub-j/connect4-player-conEcta/assets/92806890/6f775a27-c30f-4a68-98ed-3ecfc284d0c8" width="40" /> connect4-player-conEcta
Connect4 AI player that uses a minimax algorithm to check for optimal moves. 
Winner of our class Connect 4 competition. 
Mark: 9,3 

## Heuristic Function
...

## Results against player Profe
![image](https://github.com/artHub-j/connect4-player-conEcta/assets/92806890/c35fe24c-8362-4d13-b40e-13e9564e1b77)

Regarding the percentage of wins/losses, we can see that in the cases where we have more difficulty winning are in odd depths. This is because in minimax trees it is preferable to go down an even number of levels because otherwise, we find ourselves at a depth where we do not take into account the opponent's response, that is, we find ourselves with boards after we roll. Not assessing the leaves of the boards where the opponent (who always seeks to minimize our chances of getting a winning move) responds to our moves leads to making wrong decisions on the part of our player.

And as expected, we also lose in games where we face Profe with greater depth than ours. This may be due to the fact that we do not choose the best moves since these are at deeper levels in our minimax tree that we never get to explore. The fairest thing would be to match the players at similar depths.

We also see that alpha-beta pruning has no repercussions on the number of wins and losses. Pruning "only" affects the computational cost of the algorithm. Also, we can see that being player 1 or 2 (being the first player to play) doesn't have much of an effect on losing/winning. Justifying that we managed to correctly apply our heuristic function on the color of the player making the move.


## Results against classmates
![TorneigConecta4](https://github.com/artHub-j/connect4-player-conEcta/assets/92806890/03727b8b-bed4-47a6-80ff-97f3e7fb9ee4)

## Time efficiency in searching for optimal moves:
![image](https://github.com/artHub-j/connect4-player-conEcta/assets/92806890/63247cb8-f2ab-40ba-b2cd-668a2a050c04)
