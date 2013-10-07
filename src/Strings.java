// CS 2510 Fall 2013
// Assignment 3
/*
 * assignment 3
 * Robbins Jeffrey
 * Robbinsj
 * Guo jinhua
 * guoj
 */
import tester.*;

// to represent a list of Strings
interface ILoS {
// combine all Strings in this list into one
    String combine();
//returns content of list
    String contents();
//determines whether given list is sorted
    boolean isSorted();
// insert a string to the list in alphabetic order 
    ILoS insert(String that);
//merge given list into this sorted list 
    ILoS merge(ILoS that);
}

// to represent an empty list of Strings
class MtLoS implements ILoS {
    MtLoS() {
        //empty
    }
  
// combine all Strings in this list into one
    public String combine() {
        return "";
    }  
//returns true because empty list is alphabetical
    public boolean isSorted() {
        return true;
    }
//returns empty string because Mt list is empty
    public String contents() {
        return "";
    }
 // insert a string to the list in alphabetic order 
    public ILoS insert(String that) {
        return new ConsLoS(that, this);
    }
//merge two list into one sorted list
    public ILoS merge(ILoS that) {
        return that;
    }
}

// to represent a nonempty list of Strings
class ConsLoS implements ILoS {
    String first;
    ILoS rest;
  
    ConsLoS(String first, ILoS rest) {
        this.first = first;
        this.rest = rest;  
    }

 /*
  TEMPLATE
  FIELDS:
  ... this.first ...         -- String
  ... this.rest ...          -- ILoS
  
  METHODS
  ... this.combine() ...      -- String
  
  METHODS FOR FIELDS
  ... this.first.concat(String) ...       -- String
  ... this.first.compareTo(String) ...    -- int
  ... this.rest.combine() ...             -- String
  
  */
  
// combine all Strings in this list into one
    public String combine() {
        return this.first.concat(this.rest.combine());
    }  
//returns if list is sorted alphabetically 
    public boolean isSorted() {
        if (this.rest.contents() == "") {
            return true;
        }
        else {
            if (this.first.compareTo(this.rest.contents()) > 0) {
                return false;
            }
            else {
                return this.rest.isSorted();
            }
        }
    }
//returns the contents of first
    public String contents() {
        return this.first;
    }
// insert a string to the list in alphabetic order     
    public ILoS insert(String that) {
        if (this.first.compareTo(that) <= 0) {
            return new ConsLoS(this.first, this.rest.insert(that));
        }
        else {
            return new ConsLoS(that, this); 
        }
    }
//merge two list into one sorted list
    public ILoS merge(ILoS that) {
        return this.rest.merge(that.insert(this.first));
    }
}

// to represent examples for lists of strings
class ExamplesStrings {
    ExamplesStrings() {
        //empty
    }
  
    ILoS mary = new ConsLoS("Mary ",
                    new ConsLoS("had ",
                            new ConsLoS("a ",
                                    new ConsLoS("little ",
                                        new ConsLoS("lamb.", new MtLoS())))));
    ILoS alphabet = new ConsLoS("a",
            new ConsLoS("b", 
                    new ConsLoS("d", new MtLoS())));
    ILoS alphabet1 = new ConsLoS("a",
            new ConsLoS("b", 
                    new ConsLoS("c", new MtLoS())));
    ILoS alphabetCom = new ConsLoS("a",
            new ConsLoS("a",
                    new ConsLoS("b", 
                            new ConsLoS("b", 
                                    new ConsLoS("c", 
                                           new ConsLoS("d",
                                                   new MtLoS()
                        ))))));
    boolean testCombine(Tester t) {
        return 
                t.checkExpect(this.mary.combine(), "Mary had a little lamb.");
    }
    
    boolean testIsSorted(Tester t) {
        return
                t.checkExpect(this.mary.isSorted(), false) &&
                t.checkExpect(this.alphabet.isSorted(), true);
    }
    boolean testInsert(Tester t) {
        return
                t.checkExpect(this.alphabet.insert("c"), new ConsLoS("a",
                        new ConsLoS("b", new ConsLoS("c", 
                                new ConsLoS("d", new MtLoS())))));
    }
    boolean testMerge(Tester t) {
        return
                t.checkExpect(this.alphabet.merge(this.alphabet1),
                        this.alphabetCom );
    }
}