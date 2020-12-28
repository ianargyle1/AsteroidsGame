# AsteroidsGame

## About the Project
This is a java clone of the 1980's arcade game "Asteroids". The project was was created for CS1410, some of the code was given to us, most required extensive modification. I personally wrote ~80% of the code in the .java files contained in src/asteroids/.

## Installation
1. Make sure you have Java installed (https://www.java.com/en/)
2. Clone or download this repository
3. Run AsteroidsDemo.jar to play the game

## Usage
*There is a on-screen display showing lives remaining, level, and score. The ship gets three lives initially.
*The ship does not move autonomously. Instead, you can accelerate it in the direction it is pointing with the up-arrow or W key. There is a maximum speed to which a ship can accelerate.
*You can turn left with either the left-arrow or A key. You can turn right with either the right-arrow or D key.
*You can fire bullets by pressing the down-arrow, S, or space key. There can be at most eight bullets flying around at any given time. A bullet disappears when it hits something or when it has reached its maximum range.
*When a large asteroid collides with a bullet or a ship, the asteroid splits into two medium asteroids. When a medium asteroid collides, it splits into two small asteroids. When a small asteroid collides, it disappears.
*The game score goes up by 20 points when a large asteroid splits, by 50 points when a medium asteroid splits, and by 100 points when a small asteroid disappears.
*Each time a new level is reached (i.e., each time all the asteroids on the screen are destroyed), new large asteroids appear. The number of these large asteroids increases by one from the previous level. At the beginning of the game, there are four large asteroids; at level 2, there are five; at level 3, there are six; and so on.
*At level 2, a medium size alien ship appears 5-10 seconds after the level begins. When it is destroyed, you earn 200 points. About 5-10 seconds later, it appears again.
*At level 3 and above, the medium alien ship stops appearing and the small alien ship appears instead. It's destruction earns you 1000 points.
