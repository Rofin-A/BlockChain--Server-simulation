/**
 * @classname: Block.Java
 * @author: Rofin A
 */
/*-----------------------------------------------------------------------------* 
*  Purpose: This class constitutes the block of the block chain and offers all *
*           possible operations that could be carried out on the block         *
*-------------------------------------------------------------------------------*/

package cmu.edu.andrew.ra;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import org.json.JSONObject;

public class Block {
    //Member variable declaration ; Scope - Global (throughout the class)
    private int index; 
    private Timestamp timestamp; 
    private String data;
    private int difficulty;   
    private String previousHash;
    private BigInteger nonce;
    
//Constructor declaration
 Block(int index, Timestamp timestamp, String data, int difficulty){
    this.index = index;
    this.timestamp = timestamp;
    this.data = data;
    this.difficulty = difficulty;
    previousHash = "";
    nonce = BigInteger.ZERO;
 }
 
 /*---------------------------------------------------------------------------
 Mehtod- Calculate hash
 --- Signature:
 --- Purpose :
 This method computes a hash of the concatenation of the index, timestamp, 
 data, previousHash, nonce, and difficulty.
 ----------------------------------------------------------------------------*/
 public String calculatHash(){
   // Variable declaration
   String hash_string = null;
   //--- Concatenate the memeber variable to be hashed
   String tobehashed = Integer.toString(index)+ timestamp + data + previousHash + nonce.toString() + difficulty;
   
   // Hash the concatenated string and return the result 
   MessageDigest algorithm;
        try {
            algorithm = MessageDigest.getInstance("SHA-256");
            algorithm.update(tobehashed.getBytes());
            byte[] hashed = algorithm.digest();
//            Convert the hash to hexadecimal notations
            hash_string = javax.xml.bind.DatatypeConverter.printHexBinary(hashed);
            } catch (NoSuchAlgorithmException ex) {
            System.out.println("Digest error");
        }
     // return hashed string   
     return hash_string;
 }
    
 /*---------------------------------------------------------------------------
 Mehtod- proofOfWork
 --- Signature:
 --- Return:
     hash_str - Good hash
 --- Purpose :
 The proof of work methods finds a good hash. It increments the nonce until it 
 produces a good hash.
 --- Good Hash:
 Hash that has difficulty number of zero as prefix
  ----------------------------------------------------------------------------*/
  public String proofOfWork() {
 // -- Variable declaration
      boolean is_true = true;
      String hash_str = null;
      //--compute hash until we end up with good hash
      while(is_true){
          hash_str = calculatHash();
          //- check if the hash string has required number of zero as prefix
          if(hash_str !=null && hash_str.substring(0,difficulty).matches("[0]+")){
              is_true = false;
          }
          // Increment the nonce and continue
          else{nonce = nonce.add(BigInteger.ONE);}
      }
      return hash_str;
  } 
    
  /*---------------------------------------------------------------------------
 Mehtod- getDifficulty
 --- Signature:
 --- Return: 
     difficulty 
 --- Purpose :
 This is a getter method for difficulty
  ----------------------------------------------------------------------------*/
 public int getDifficulty(){
     return difficulty;
 }   

 /*---------------------------------------------------------------------------
 Mehtod- setDifficulty
 --- Signature:
     int Difficulty
 --- Return: 
     difficulty 
 --- Purpose :
 This is a setter method for difficulty and resets the difficulty with the parameter
 supplied in method signature
  ----------------------------------------------------------------------------*/
 public void setDifficulty(int difficulty){
     this.difficulty = difficulty;
 } 
 
 /*---------------------------------------------------------------------------
 Mehtod- toString
 --- Signature:
 --- Return: 
     json_rep - String representation of JSON notation of blocks data 
 --- Purpose :
 This methods overrides toString method to return the JSON representation of the 
 of the blocks data
  ----------------------------------------------------------------------------*/
 
 @Override
 public java.lang.String toString(){
        // Populate the block data into the json
        JSONObject chain = new JSONObject();
        chain.put("index",index );
        chain.put("time stamp",timestamp );
        chain.put("Tx",data );
        chain.put("PrevHash",previousHash );
        chain.put("nonce",nonce );
        chain.put("difficulty",difficulty );
        return chain.toString();
 }
 
 
 /*---------------------------------------------------------------------------
 Mehtod- setPreviousHash
 --- Signature:
     String previousHash
 --- Return: 
     
 --- Purpose :
 This is a setter method for previousHash and resets the previousHash with the 
 parameter supplied in method signature
  ----------------------------------------------------------------------------*/
 public void setPreviousHash(String previousHash){
     this.previousHash = previousHash;
 }
/*---------------------------------------------------------------------------
 Mehtod- getDifficulty
 --- Signature:
 --- Return: 
     difficulty 
 --- Purpose :
 This is a getter method for previousHash.
  ----------------------------------------------------------------------------*/ 
 public String getPreviousHash(){
     return previousHash;
 }

/*---------------------------------------------------------------------------
 Mehtod- getIndex
 --- Signature:
 --- Return: 
     index
 --- Purpose :
 This is a getter method for index.
  ----------------------------------------------------------------------------*/  
public int getIndex(){
    return index;
} 
/*---------------------------------------------------------------------------
 Mehtod- setIndex
 --- Signature:
     int index
 --- Return: 
     
 --- Purpose :
 This is a setter method for index and resets the index with the 
 parameter supplied in method signature
  ----------------------------------------------------------------------------*/
public void setIndex(int index){
    this.index = index;
}

/*---------------------------------------------------------------------------
 Mehtod- setTimestamp
 --- Signature:
     TimeStamp timestamp
 --- Return: 
     
 --- Purpose :
 This is a setter method for timestamp and resets the timestamp with the 
 parameter supplied in method signature
  ----------------------------------------------------------------------------*/

public void setTimestamp(java.sql.Timestamp timestamp){
    this.timestamp = timestamp;
}

/*---------------------------------------------------------------------------
 Mehtod- getTimestamp
 --- Signature:
 --- Return: 
     Timestamp
 --- Purpose :
 This is a getter method for timestamp.
  ----------------------------------------------------------------------------*/  

public Timestamp getTimestamp(){
    return timestamp;
}

/*---------------------------------------------------------------------------
 Mehtod- getData
 --- Signature:
 --- Return: 
     Timestamp
 --- Purpose :
 This is a getter method for data.(transaction detail)
  ----------------------------------------------------------------------------*/  

public String getData(){
    return data;
}

/*---------------------------------------------------------------------------
 Mehtod- setData
 --- Signature:
     String Data
 --- Return: 
     
 --- Purpose :
 This is a setter method for Data and resets the data with the 
 parameter supplied in method signature -(Transaction detail)
  ----------------------------------------------------------------------------*/

public void setData(java.lang.String data){
 this.data = data;   
}


    /**
     * @param args the command line arguments
     */
    
    
    public static void main(String[] args) {
      
    }
    
}
