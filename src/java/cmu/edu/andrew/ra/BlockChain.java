/**
 * @classname: BlockChain.Java
 * @author: Rofin A
 */
/*-----------------------------------------------------------------------------* 
*  Purpose: This class simulates the block chain. It verifies the incoming block
*  and accumulates in an array list data structure. Moreover it provides operation
*  that allows user to validate the transaction and view the transactions
*-------------------------------------------------------------------------------*/

package cmu.edu.andrew.ra;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.json.JSONObject;

/**
 *
 * @author rofin
 */
public class BlockChain {

    List <Block> blocks = new ArrayList<Block>();
    String chain_hash;
    //Constructor - definition

    public BlockChain() {
        Block b = new Block(0, new Timestamp(System.currentTimeMillis()),"Genesis", 2);
        blocks.add(b);
        
        chain_hash = b.proofOfWork();
    }

    /*---------------------------------------------------------------------------
 Mehtod- getTime
 --- Signature:
 --- Return: 
     Timestamp timestamp
 --- Purpose :
 This method returns the current system time as timestamp
  ----------------------------------------------------------------------------*/
    public java.sql.Timestamp getTime() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp;
    }

    /*---------------------------------------------------------------------------
 Mehtod- getLatestBlock
 --- Signature:
 --- Return: 
     Block block 
 --- Purpose :
 This method returns a reference last block of the chain
  ----------------------------------------------------------------------------*/
    public Block getLatestBlock() {
        return blocks.get(blocks.size() - 1);
    }

    /*---------------------------------------------------------------------------
 Mehtod- getChainSize
 --- Signature:
 --- Return: 
     int size
 --- Purpose :
 This method returns the number of blocks in the chain
  ----------------------------------------------------------------------------*/
    public int getChainSize() {
        return blocks.size();
    }

    /*---------------------------------------------------------------------------
 Mehtod- hashesPerSecond
 --- Signature:
 --- Return: 
     int ctr
 --- Purpose :
 This method returns the number hashes computed in a second
  ----------------------------------------------------------------------------*/
    public int hashesPerSecond() {
        //-- Variable declaration
        //------------------------
        int ctr = 0;
        MessageDigest algorithm;
        long end = System.currentTimeMillis() + 1000; //one second delay
        //-- track the number of hashes calculated in a second
        while (System.currentTimeMillis() <= end) {
            String str = "00000000";
            // Hash the concatenated string and return the result 
            try {
                algorithm = MessageDigest.getInstance("SHA-256");
                algorithm.update(str.getBytes());
                byte[] hashed = algorithm.digest();
            } catch (NoSuchAlgorithmException ex) {
                System.out.println("Digest error");
            }
            //increment the counter everytime an hash is calculated
            ctr++;
        }
        return ctr;
    }

    /*---------------------------------------------------------------------------
 Mehtod- addBlock
 --- Signature:
     Block newBlock (instance of Block)
 --- Return: 
 --- Purpose :
 This method adds a block to the chain and maintains the hashpointer accordingly
 ----------------------------------------------------------------------------*/
    public void addBlock(Block newBlock) {
        //Set the previous hash of the new block
        newBlock.setPreviousHash(chain_hash);
        String hash = newBlock.proofOfWork();
        blocks.add(newBlock);
        chain_hash = hash;
    }

 /*---------------------------------------------------------------------------
 Method- toString
 --- Signature:
 --- Return: 
     String chain (list of Blocks in the chain) 
 --- Purpose :
 This method overrides the toString method to display the Blocks alvailable in the chain
  ----------------------------------------------------------------------------*/
    @Override
    public String toString() {
        String chain = null;
        JSONObject b_data = new JSONObject();
        for (int i = 0; i < blocks.size(); i++) {
                chain = blocks.get(i).toString();
                JSONObject block = new JSONObject(chain);
                b_data.append("ds_chain",block);
        }
        b_data.put("chainHash", chain_hash);
        return b_data.toString();
    }

/*---------------------------------------------------------------------------
 Mehtod- isChainValid
 --- Signature:
 --- Return:
     String containing the status message
 --- Purpose :
This method validates if hash computation of each block yields good hash and the 
good hash matches the pointer in the next block of the chain
 ----------------------------------------------------------------------------*/
    public String isChainValid() {
        StringBuilder msg = new StringBuilder();
        msg.append("Verifying entire chain\n");
        for (int i = 0; i < blocks.size(); i++) {
            Block b = blocks.get(i);
            String next_hash = null;
            //-- get the hash pointer of the next node 
            //-- if current block is the last block of the chain (or only block
            //   in the chain then the chain_hash will be the hash pointer.
            //   otherwise check the pointer in the next block of chain
            if (i != blocks.size() - 1) {
                next_hash = blocks.get(i + 1).getPreviousHash();
            } else {
                next_hash = chain_hash;
            }
            String hash = b.calculatHash();
            //-- Check if they have required number zero prefix
            if (hash != null && !hash.substring(0, b.getDifficulty()).matches("[0]+")) {
                // repeat difficulty times 0 in a string to produce output
                String val = new String(new char[b.getDifficulty()]).replace("\0", "0");
                //write the log message
                msg.append("..improper hash on node "+i + " Does not start with "+val+"\n" );
                msg.append("Chain Verification: false");
                return msg.toString();
            }
            //-- check if the pointer is valid
            if (!hash.equals(next_hash)) {
                // repeat difficulty times 0 in a string to produce output
                String val = new String(new char[b.getDifficulty()]).replace("\0", "0");
                msg.append("..improper hash on node "+i + " Does not start with "+val+"\n" );
                msg.append("Chain Verification: false");
                return msg.toString();
            }
        }
        msg.append("Chain Verification: true");
        return msg.toString();
    }

 /*---------------------------------------------------------------------------
 Mehtod- repairChain
 --- Signature:
 --- Return: 
 --- Purpose :
 This methods tracks the corrupted blocks in the chain and performs proof of work
 and transcends the result throughout the chain to achieve consensus  
  ----------------------------------------------------------------------------*/
    public void repairChain() {
        for (int i = 0; i < blocks.size(); i++) {
            Block b = blocks.get(i);
            String next_hash = null;
            if (i != blocks.size() - 1) {
                next_hash = blocks.get(i + 1).getPreviousHash();
            } else {
                next_hash = chain_hash;
            }
            String hash = b.calculatHash();
            //Ensure the hash string has required number of zeros and it matches 
            // the previoushash value maintaine in the next node of the chaing
            // if any of this condition fails repair the chain and bring it to 
            // consistency
            if ((hash != null && !hash.substring(0, b.getDifficulty()).matches("[0]+"))
                    || (!hash.equals(next_hash))) {
                String revised_hash = b.proofOfWork();

                //--- Reset the hash pointer with the appropriate hash calculated above
                if (i != blocks.size() - 1) {
                    blocks.get(i + 1).setPreviousHash(revised_hash);
                } else {
                    chain_hash = null;
                    chain_hash = revised_hash;
                }
                // once the chain is repared continue to next iteration of the loop
                //continue;
            }
        }
    }

    public static void main(String[] args) {
        boolean is_true = true;
        int uChoice;
        Scanner uinp = new Scanner(System.in);
         BlockChain bc = new BlockChain();
         int i = 1;
        while (is_true) {
            System.out.println("Please choose one of the following option");
            System.out.println("0. View basic blockchain status.");
            System.out.println("1. Add a transaction to the blockchain.");
            System.out.println("2. Verify the blockchain.");
            System.out.println("3. View the blockchain.");
            System.out.println("4. Corrupt the chain.");
            System.out.println("5. Hide the corruption by repairing the chain.");
            System.out.println("6. Exit.");
           
            uChoice = uinp.nextInt();
            switch (uChoice) {
                case 0: {
                    System.out.println("Current size of chain: "+bc.getChainSize());
                    System.out.println("Current hashes per second by this machine:  "+bc.hashesPerSecond());
                    break;
                }
                case 1: {
                    System.out.println("Enter difficulty :");
                    int d = uinp.nextInt();
                    System.out.println("Enter transaction :");
                    uinp.nextLine();
                    String transaction = uinp.nextLine();
                    long start = System.currentTimeMillis();
                    Block b = new Block(i++,new Timestamp(System.currentTimeMillis()),transaction,d);
                    bc.addBlock(b);                
                    long stop = System.currentTimeMillis();
                    System.out.println("Total execution time to add this block was "+(stop-start)+" milliseconds");
                    break;
                }
                case 2: {
                    System.out.println("Verifying entire chain");
                    System.out.println("Chain Verification: "+bc.isChainValid());
                    break;
                }
                case 3: {
                    System.out.println(bc.toString());
                    break;
                }
                case 4: {
                    System.out.println("Enter block ID of block to corrupt ");
                    int idx = uinp.nextInt();
                    uinp.nextLine();
                    System.out.println("Enter new data for block "+idx);
                    String transaction = uinp.nextLine();
                    Block b = bc.blocks.get(idx);
                    b.setData(transaction);
                    System.out.println("Block 2 now holds "+b.getData());
                    break;
                }
                case 5: {
                    System.out.println("Repairing the entire chain");
                    long start = System.currentTimeMillis();
                    bc.repairChain();
                    long stop = System.currentTimeMillis();
                    System.out.println("Total execution time required to repair the chain was "+(stop-start)+" milliseconds");
                    break;
                }
                case 6: {
                    is_true = false;
                    break;
                }
            }
        }

    }

}
