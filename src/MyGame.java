import java.awt.Color;
import java.util.Random;

import tester.*;

import javalib.funworld.*;
import javalib.colors.*;
import javalib.worldcanvas.*;
import javalib.worldimages.*;


//creates a mutable position class
class CartPt extends Posn {
    CartPt(int x, int y){
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
        	if (this.topBoundries()){
        		return new Monkey(this.center.up(this.speed), this.radius, this.speed, this.color);
        	}
        	else {
        		//if monkey is at top boundry, move monkey down 1 to allow movement next check
        		return new Monkey(new CartPt(this.center.x, 0 + this.radius), this.radius, this.speed, this.color);
        	}
            
        }
        else {
            if (ke.equals("down")) {
            	if (this.bottomBoundries()){
            		return new Monkey(this.center.down(this.speed), this.radius, this.speed, this.color);
            	}
            	else {
            		//if monkey is at bottom boundry, move monkey up 1 to allow movement next check
            		return new Monkey(new CartPt(this.center.x, 300 - this.radius), this.radius, this.speed, this.color);
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
    	if (this.center.y - this.radius <= 0){
    		return false;
    	}
    	else {
    		return true;
    	}
    }
    /** checks if monkey is at bottom boundry
     * returns true if monkey is within bounds **/
    boolean bottomBoundries() {
    	if (this.center.y + this.radius >= 300){
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
        return new RectangleImage(this.center, this.width, this.height, this.color);
    }
//is the input CartPt that within with block object
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

//
class MtBlock implements ILoBlock {
    MtBlock(){}
//generate a new block in the list with n as y value
    public ILoBlock spawnBlock(int n){
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


class ConsLoBlock implements ILoBlock{
    Block first;
    ILoBlock rest;
  
    ConsLoBlock(Block first, ILoBlock rest){
        this.first = first;
        this.rest = rest;
    }
//generates a new block in the list with n as y value
    public ILoBlock spawnBlock(int n) {
        return new ConsLoBlock(
                new Block(new CartPt(500, n), 150, 50, new Blue()), this.moveBlocks());
    }
//moves all blocks in list left
    public ILoBlock moveBlocks(){
        return new ConsLoBlock(new Block(this.first.center.left(15), this.first.height, this.first.width, this.first.color),
                this.rest.moveBlocks());
    }
//draws all block images
    public WorldImage drawBlocks() {
        return new OverlayImages(this.first.blockImage(), this.rest.drawBlocks());
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
        return new OverlayImages(this.monkey.monkeyImage(), this.blocks.drawBlocks());
    }
    
    public World onKeyEvent(String ke) {
        return new JungleWorld(this.time, this.monkey.moveMonkey(ke), this.blocks);
    }
    
    public World onTick() {
        if (this.time % 15 == 0){
            return new JungleWorld(1 + this.time, this.monkey, this.blocks.spawnBlock(this.randomInt(300)));
        }
        else {
            return new JungleWorld(1 + this.time, this.monkey, this.blocks.moveBlocks());
        }
    }
    /** helper method to generate a random number in the range 0 to n**/
    int randomInt(int n) {
        return  new Random().nextInt(n);
    }
    /** returns true if monkey has touched any blocks in existance **/
    boolean monkeyCollide() {
    	return this.blocks.hasCollided(new CartPt(this.monkey.center.x + this.monkey.radius, this.monkey.center.y)) ||
    			this.blocks.hasCollided(new CartPt(this.monkey.center.x - this.monkey.radius, this.monkey.center.y)) ||
    			this.blocks.hasCollided(new CartPt(this.monkey.center.x, this.monkey.center.y + this.monkey.radius)) ||
    			this.blocks.hasCollided(new CartPt(this.monkey.center.x, this.monkey.center.y - this.monkey.radius));
    }
    /**
     * produce the image of this world by adding the moving blob 
     * to the background image
     */
    public WorldImage lastImage(String s){
      return new OverlayImages(this.makeImage(),
          new TextImage(new Posn(100, 40), s, 
              Color.red));
    }
//when monkey collides with a block the world ends
    public WorldEnd worldEnds() {
    	if (this.monkeyCollide()) {
    		return new WorldEnd(true, this.lastImage("MONKEY DIED!!! WHY YOU DIE MONKEY?!"));
    	}
    	else {
    		return new WorldEnd(false, this.makeImage());
    	}
    }

}

class ExamplesJungleWorld {
    Monkey monkey1 = new Monkey(new CartPt(100, 100), 30, 10, new Red());
    
    Block block1 = new Block(new CartPt(150, 75), 150, 50, new Green());
    Block block2 = new Block(new CartPt(250, 225), 150, 50, new Green());
    Block block3 = new Block(new CartPt(350, 75), 150, 50, new Green());
    
    MtBlock empty = new MtBlock();
    
    ConsLoBlock blocks1 = new ConsLoBlock(this.block1, 
            new ConsLoBlock(this.block2, new ConsLoBlock(this.block3, this.empty)));
    

    JungleWorld world = new JungleWorld(1, this.monkey1, this.empty);
    
    
     
    
    boolean runAnimation = this.world.bigBang(500, 300, .3);
}
