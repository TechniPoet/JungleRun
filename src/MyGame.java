/*
 * assignment 3
 * Robbins Jeffrey
 * Robbinsj
 * Guo jinhua
 * guoj
 */

import java.awt.Color;
import java.util.Random;

import tester.*;

import javalib.funworld.*;
import javalib.colors.*;
import javalib.worldcanvas.*;
import javalib.worldimages.*;


//creates a mutable position class
class CartPt extends Posn {
    CartPt(int x, int y) {
        super(x, y);
    }
    //moves cartPt up subtracting to y
    CartPt up(int y) {
        return new CartPt(this.x, this.y - y);
    }
    
  //moves cartPt down adding to y
    CartPt down(int y) {
        return new CartPt(this.x, this.y + y);
    }
    
    //moves cartPt left subtracting x
    CartPt left(int x) {
        return new CartPt(this.x - x, this.y);
    }
    
  //moves cartPt right adding to x
    CartPt right(int x) {
        return new CartPt(this.x + x, this.y);
    }
}

//represents monkey aka player in game
class Monkey {
    CartPt center;
    int radius;
    int speed;
    IColor color;
    Monkey(CartPt center, int radius, int speed, IColor color) {
        this.center = center;
        this.radius = radius;
        this.speed = speed;
        this.color = color;
    }
    
    /** produces image of monkey **/
    WorldImage monkeyImage() {
        return new DiskImage(this.center, this.radius, this.color);
    }
    
    /** moves monkey using key events**/
    Monkey moveMonkey(String ke) {
        if (ke.equals("up")) {
        	if (this.topBoundries()) {
        		return new Monkey(this.center.up(this.speed), this.radius,
        				this.speed, this.color);
        	}
        	else {
        		//if monkey is at top boundry, move monkey down 1 to allow 
        		//movement next check
        		return new Monkey(new CartPt(this.center.x, 0 + this.radius),
        				this.radius, this.speed, this.color);
        	}
            
        }
        else {
            if (ke.equals("down")) {
            	if (this.bottomBoundries()) {
            		return new Monkey(this.center.down(this.speed),
            				this.radius, this.speed, this.color);
            	}
            	else {
            		//if monkey is at bottom boundry, move monkey up 1 to
            		//allow movement next check
            		return new Monkey(
            				new CartPt(this.center.x, 300 - this.radius),
            				this.radius, this.speed, this.color);
            	}
            }
            else {
                return this;
            }
        }
    }
    
    /** checks if monkey is at top boundry 
     * returns true if monkey is within bounds**/
    boolean topBoundries() {
    	if (this.center.y - this.radius <= 0) {
    		return false;
    	}
    	else {
    		return true;
    	}
    }
    /** checks if monkey is at bottom boundry
     * returns true if monkey is within bounds **/
    boolean bottomBoundries() {
    	if (this.center.y + this.radius >= 300) {
    		return false;
    	}
    	else {
    		return true;
    	}
    }
    
}


//represents block in the game

class Block {
    CartPt center; 
    int height;
    int width;
    IColor color;
  
    Block(CartPt center, int height, int width, IColor color) {
        this.center = center;
        this.height = height;
        this.width = width;
        this.color = color;
    }
  
 /** produces image of block **/
    WorldImage blockImage() {
        return new RectangleImage(this.center, this.width, this.height,
        		this.color);
    }
/** is the input CartPt that within with block object **/
    boolean isTouching(CartPt that) {
    	if ( (this.center.x - (this.width / 2) <= that.x) &&
    	        (this.center.x + (this.width / 2) >= that.x) &&
    	        (this.center.y - (this.height / 2) <= that.y)  &&
    	        (this.center.y + (this.height / 2) >= that.y) ) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }
/** returns whether or not this block is off the screen **/
    boolean offScreen() {
    	//if right side of block is off screen
    	if (this.center.x + (this.width / 2) <= 0) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }
}

//represents the blocks in the game 
interface ILoBlock {
// generate a new block in the list with n as yvalue     
    ILoBlock spawnBlock(int n);
//moves all blocks in list left
    ILoBlock moveBlocks();
//draws all blocks
    WorldImage drawBlocks();
//any blocks touching the given point?
    boolean hasCollided(CartPt that) ;
}

//represents empty list of blocks
class MtBlock implements ILoBlock {
    MtBlock() {
    	//MtBlock contains no fields
    }
//generate a new block in the list with n as y value
    public ILoBlock spawnBlock(int n) {
        return new ConsLoBlock(
                new Block( new CartPt( 500, n), 150, 50, new Blue()), this);
    }
//nothing happens
    public ILoBlock moveBlocks() {
        return this;
    }
//draws circle out of plain of sight so world doesnt blow up
    public WorldImage drawBlocks() {
        return new DiskImage(new CartPt(1000, 1000), 1, new Red());
    }
//because mt does not have any blocks, return false
    public boolean hasCollided(CartPt that) {
    	return false;
    }
}


class ConsLoBlock implements ILoBlock {
    Block first;
    ILoBlock rest;
    int speed;
  
    ConsLoBlock(Block first, ILoBlock rest, int speed) {
        this.first = first;
        this.rest = rest;
        this.speed = speed;
    }
    //constructor which will be used when speed isnt changed
    ConsLoBlock(Block first, ILoBlock rest) {
    	//default speed is 15
    	this(first, rest, 15);
    }
//generates a new block in the list with n as y value
    public ILoBlock spawnBlock(int n) {
        return new ConsLoBlock(
                new Block(new CartPt(500, n),
                		150, 50, new Blue()), this.moveBlocks());
    }
//moves all blocks in list left
    public ILoBlock moveBlocks() {
    	if (this.first.offScreen()) {
    		//if the first is off screen then exclude it from returned list
    		return this.rest.moveBlocks();
    	}
    	else {
    		//if its not off screen than move it left
    		return new ConsLoBlock(new Block(this.first.center.left(this.speed)
    				, this.first.height, this.first.width, this.first.color),
                    this.rest.moveBlocks());
    	}
        
    }
//draws all block images
    public WorldImage drawBlocks() {
        return new OverlayImages(this.first.blockImage(),
        		this.rest.drawBlocks());
    }
//returns true if CartPt that is touching any of the blocks in list
    public boolean hasCollided(CartPt that) {
    	return this.first.isTouching(that) || this.rest.hasCollided(that);
    }
}



//represents world of jungle
class JungleWorld extends World {
    int time;
    int height = 500;
    int width = 300;
    Monkey monkey;
    ILoBlock blocks;
    JungleWorld(int time, Monkey monkey, ILoBlock blocks) {
        super();
        this.monkey = monkey;
        this.time = time;
        this.blocks = blocks;
    }
    
    /** produces image of world **/
    public WorldImage makeImage() {
        return new OverlayImages(this.monkey.monkeyImage(),
        		this.blocks.drawBlocks());
    }
    /** updates key event for moving monkey **/
    public World onKeyEvent(String ke) {
    	if (ke.equals("x")) {
    		return this.endOfWorld("Game Ended");
    	}
        return new JungleWorld(this.time, this.monkey.moveMonkey(ke),
        		this.blocks);
    }
    /** runs every time the clock moves up
     * moving blocks and spawning new blocks
     */
    public World onTick() {
        if (this.time % 15 == 0) {
            return new JungleWorld(1 + this.time, this.monkey,
            		this.blocks.spawnBlock(this.randomInt(300)));
        }
        else {
            return new JungleWorld(1 + this.time, this.monkey,
            		this.blocks.moveBlocks());
        }
    }
    /** helper method to generate a random number in the range 0 to n**/
    int randomInt(int n) {
        return  new Random().nextInt(n);
    }
    /** returns true if monkey has touched any blocks in existance **/
    boolean monkeyCollide() {
    	return this.blocks.hasCollided(
    			new CartPt(this.monkey.center.x + this.monkey.radius,
    					this.monkey.center.y)) ||
    			this.blocks.hasCollided(
    					new CartPt(this.monkey.center.x - this.monkey.radius,
    							this.monkey.center.y)) ||
    			this.blocks.hasCollided(
    					new CartPt(this.monkey.center.x,
    							this.monkey.center.y + this.monkey.radius)) ||
    			this.blocks.hasCollided(
    					new CartPt(this.monkey.center.x,
    							this.monkey.center.y - this.monkey.radius));
    }
    /**
     * produce the image of this world by adding the moving blob 
     * to the background image
     */
    public WorldImage lastImage(String s) {
    	return new OverlayImages(this.makeImage(),
          new TextImage(new Posn(this.height / 2, this.width / 2), s, 
              Color.red));
    }
//when monkey collides with a block the world ends
    public WorldEnd worldEnds() {
    	if (this.monkeyCollide()) {
    		return new WorldEnd(true, 
    				this.lastImage("MONKEY DIED!!! WHY YOU DIE MONKEY?!"));
    	}
    	else {
    		return new WorldEnd(false, this.makeImage());
    	}
    }

}

class ExamplesJungleWorld {
    Monkey monkey = new Monkey(new CartPt(100, 100), 30, 10, new Red());
    Monkey mTopOut = new Monkey(new CartPt(100, -100), 30, 10, new Red());
    Monkey mBotOut = new Monkey(new CartPt(100, 1000), 30, 10, new Red());
    Monkey monkey1 = new Monkey(new CartPt(100, 90), 30, 10, new Red());
    Monkey monkey2 = new Monkey(new CartPt(100, 110), 30, 10, new Red());
    Monkey mTopRim = new Monkey(new CartPt(100, 30), 30, 10, new Red());
    Monkey mBotRim = new Monkey(new CartPt(100, 270), 30, 10, new Red());
    
    
    
    
    Block block1 = new Block(new CartPt(150, 75), 150, 50, new Green());
    Block block2 = new Block(new CartPt(250, 225), 150, 50, new Green());
    Block block3 = new Block(new CartPt(350, 75), 150, 50, new Green());
    Block block4 = new Block(new CartPt(135, 75), 150, 50, new Green());
    Block block5 = new Block(new CartPt(235, 225), 150, 50, new Green());
    Block block6 = new Block(new CartPt(335, 75), 150, 50, new Green());
    Block block7 = new Block(new CartPt(220, 225), 150, 50, new Green());
    Block bLeftOut = new Block(new CartPt(-100, 75), 150, 50, new Green());
    
    
    
    MtBlock empty = new MtBlock();
    
    ConsLoBlock blocks1 = new ConsLoBlock(this.block1, 
            new ConsLoBlock(this.block2, 
            		new ConsLoBlock(this.block3, this.empty)));
    ConsLoBlock blocks2 = new ConsLoBlock(this.block3, this.empty);
    ConsLoBlock blocks3 = new ConsLoBlock(this.block4, 
            new ConsLoBlock(this.block5, 
            		new ConsLoBlock(this.block6, this.empty)));
    ConsLoBlock blocks4 = new ConsLoBlock(this.block5, 
    		new ConsLoBlock(this.bLeftOut, this.empty));
   

    
    JungleWorld world = new JungleWorld(1, this.monkey, this.empty);
    JungleWorld world1 = new JungleWorld(1, this.monkey, this.blocks1);
    JungleWorld world2 = new JungleWorld(2, this.monkey, this.blocks3);
    JungleWorld world3 = new JungleWorld(15, this.monkey, this.empty);
    JungleWorld world4 = new JungleWorld(16, this.monkey, this.blocks2);
    
    
    /** tests creation of monkey image */
    boolean testMonkeyImage(Tester t) {
        return
                t.checkExpect(this.monkey.monkeyImage(),
                		new DiskImage(new CartPt(100, 100),
                				30, new Red()) );
    }
    /** tests topBoundries function of monkey class */
    boolean testTopBoundries(Tester t) {
    	return 
    			t.checkExpect(this.monkey.topBoundries(), true) &&
    			t.checkExpect(this.mTopOut.topBoundries(), false);
    			
    }
    /** tests bottomBoundries function of monkey class */
    boolean testBottomBoundries(Tester t) {
    	return 
    			t.checkExpect(this.monkey.bottomBoundries(), true) &&
    			t.checkExpect(this.mBotOut.bottomBoundries(), false);
    			
    }
    
    /** test moveMonkey in monkey class **/
    boolean testMoveMonkey(Tester t) {
        return
                t.checkExpect(this.monkey.moveMonkey("up"), this.monkey1) &&
                t.checkExpect(this.monkey.moveMonkey("down"), this.monkey2) &&
                t.checkExpect(this.mTopRim.moveMonkey("up"), this.mTopRim) &&
                t.checkExpect(this.mBotRim.moveMonkey("down"), this.mBotRim);
    }
    
    /** tests block image of block class */
    boolean testBlockImage(Tester t) {
        return
                t.checkExpect(this.block1.blockImage(),
                		new RectangleImage(new CartPt(150, 75),
                				50, 150, new Green()) );
    }
    
    /** tests isTouching function of block */
    boolean testIsTouching(Tester t) {
        return
                t.checkExpect(this.block1.isTouching(new CartPt(160, 85)),
                		true) &&
                t.checkExpect(this.block1.isTouching(new CartPt(250, 300)),
                		false);
                		
    }
    
    /** tests topBoundries function of IloBlock */
    boolean testHasCollided(Tester t) {
        return
                t.checkExpect(this.blocks1.hasCollided(new CartPt(260, 235)), true) &&
                t.checkExpect(this.blocks1.hasCollided(new CartPt(290, 300)), false);
            		
    }
    
    /** tests spawnBlock() function of ILoBlock */
    boolean testSpawnBlock(Tester t) {
        return
                t.checkExpect(this.blocks2.spawnBlock(30),
                		new ConsLoBlock(
                				new Block( new CartPt( 500, 30), 150, 50, new Blue()), 
                				new ConsLoBlock( 
                						new Block(new CartPt(335, 75),
                								150, 50, new Green()),this.empty)));
    }
    
    /** tests moveBlock function of ILoBlock */
    boolean testMoveBlock(Tester t) {
        return
                t.checkExpect(this.blocks1.moveBlocks(), this.blocks3);
    }
    
    /** tests drawBlocks() function of ILoBlock */
    boolean testDrawBlock(Tester t) {
        return
                t.checkExpect(this.blocks4.drawBlocks(),
                		new OverlayImages(new RectangleImage(new CartPt(235, 225), 
                				50, 150, new Green()), 
                				new OverlayImages(new RectangleImage(new CartPt(-100, 75), 
                						50, 150, new Green()), 
                						new DiskImage(new CartPt(1000, 1000), 1,
                								new Red()))));
    }
    
    /** tests if blocks are destroyed in moveBlocks() function of ILoBlock */
	boolean testRemove(Tester t) {
	        return
	                t.checkExpect(this.blocks4.moveBlocks(), 
	                		new ConsLoBlock(this.block7, this.empty));
	}



	/** test onKeyEvent in world class **/  
	boolean testOnKeyEvent(Tester t) {
        return
                t.checkExpect(this.world.onKeyEvent("up"), 
                		new JungleWorld(this.world.time, this.monkey1, this.world.blocks)) &&
                t.checkExpect(this.world.onKeyEvent("down"), 
                        new JungleWorld(this.world.time, this.monkey2, this.world.blocks)) &&
                t.checkExpect(this.world.onKeyEvent("left"),
                		new JungleWorld(this.world.time, this.monkey, this.world.blocks)) &&
                t.checkExpect(this.world.onKeyEvent("right"),
                		new JungleWorld(this.world.time, this.monkey, this.world.blocks));
                
	}
	
	/** tests randomInt function of JungleWorld */
	boolean testRandom(Tester t) {
		return
				t.checkRange(this.world1.randomInt(15), 0, 14);
	}
	
	/** tests onTick() function of JungleWorld */
	boolean testOnTick(Tester t) {
        return
                t.checkExpect(this.world1.onTick(), this.world2);
	}
	
	/** tests monkeyCollide function of JungleWorld */
	boolean testMonkeyCollide(Tester t) {
        return
                t.checkExpect(this.world4.monkeyCollide(), false) &&
                t.checkExpect(this.world2.monkeyCollide(), true);
          
	}

	/** tests worldEnd() function JungleWorld */
	boolean testWorldEnd(Tester t) {
        return
                t.checkExpect(this.world4.worldEnds(), 
                		new WorldEnd(false, this.world4.makeImage())) &&
                t.checkExpect(this.world2.worldEnds(), 
                		new WorldEnd (true, 
                				this.world2.lastImage(
                						"MONKEY DIED!!! WHY YOU DIE MONKEY?!"))
                ) && t.checkExpect(this.world2.onKeyEvent("x"),
                		this.world2.endOfWorld("Game Ended"));
	}
	
	//boolean runAnimation = this.world.bigBang(500, 300, .1);

    
}