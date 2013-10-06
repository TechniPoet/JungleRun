/*
 * assignment 3
 * Robbins Jeffrey
 * Robbinsj
 * Guo jinhua
 * guoj
 */
import tester.*;

//to represent the abstract file

abstract class AFile{
	String name;
	String owner;
	
	AFile(String name, String owner){
		this.name = name;
		this.owner = owner;
	}

	
// compute the size of this file
	abstract int size();
	
//get the owner of the IFile
    String owner(){
	    return this.owner;
	  }

// is the owner of this file the same 
// as the owner of the given file?
    boolean sameOwner(AFile that){
  	  return this.owner == that.owner();
    }
      
// compute the time (in seconds) to download this file
// at the given download rate
    int downloadTime(int rate){
        return this.size() / rate;
   }
}

//to represent class ImageFile

class ImageFile extends AFile{
	int height;
	int width;
	
	ImageFile(String name, String owner, int height, int width){
		super(name, owner);
		this.height = height;
		this.width = width;
		
	}
// compute the size of this file
    int size(){
        return this.width * this.height;
	  }  
}

//to represent class TextFile

class TextFile extends AFile{
	int length;
	
	TextFile(String name, String owner, int length){
		super(name, owner);
		this.length = length;
	}
// compute the size of this file
	  int size(){
		  return this.length;
	  }  
}



//to represent class Aduiofile

class AudioFile extends AFile {
	int speed;
	int length;
	
	AudioFile(String name, String owner, int length, int speed){
		super(name, owner);
		this.speed = speed;
		this.length = length;
	}
// compute the size of this file
    public int size(){
        return this.speed * this.length;
	  }  
}

class ExamplesFiles {
	 
	  AFile text1 = new TextFile("English paper", "Maria", 1234);
	  AFile picture = new ImageFile("Beach", "Maria", 400, 200);
	  AFile song = new AudioFile("Help", "Pat", 200, 120);
	  AFile text2 = new TextFile("American dream", "Bob", 989);
	  AFile picture2 = new ImageFile("Ocean", "Pat", 300, 500);
	  AFile song2 = new AudioFile("Money", "Maria", 100, 150);
	  
	  // test the method size for the classes that represent files
	  boolean testSize(Tester t){
	    return
	    t.checkExpect(this.text1.size(), 1234) &&
	    t.checkExpect(this.picture.size(), 80000) &&
	    t.checkExpect(this.song.size(), 24000)&&
	    t.checkExpect(this.text2.size(), 989) &&
	    t.checkExpect(this.picture2.size(), 150000) &&
	    t.checkExpect(this.song2.size(), 15000);
	  }
	  boolean testDownloadTime(Tester t){
		    return
		    t.checkExpect(this.text1.downloadTime(1234), 1) &&
		    t.checkExpect(this.picture.downloadTime(20), 4000) &&
		    t.checkExpect(this.song.downloadTime(12), 2000);
	  }
	  boolean testSameOwner (Tester t){
		    return
		    t.checkExpect(this.text1.sameOwner(text2), false) &&
		    t.checkExpect(this.picture.sameOwner(song2), true) &&
		    t.checkExpect(this.song.sameOwner(picture2), true);
	}
	  
	}