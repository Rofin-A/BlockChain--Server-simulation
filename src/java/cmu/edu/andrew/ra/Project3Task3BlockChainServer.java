/**
 * @classname: Project3Task3BlockChainServer.Java
 * @author: Rofin A
 */
/*-----------------------------------------------------------------------------* 
*  Purpose: This class recieves the http request and call the appropriate methods
*  and then return result to the client as an http response
*-------------------------------------------------------------------------------*/

package cmu.edu.andrew.ra;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(name = "Project3Task3BlockChainServer", urlPatterns = {"/blockchain/*"})
public class Project3Task3BlockChainServer extends HttpServlet {

    BlockChain bc = new BlockChain();
    int i = 1;

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Extract the path from the url, to determine the method to be invoked
        //---------------------------------------------------------------------
        System.out.println(request.getPathInfo());
        String name = (request.getPathInfo()).substring(1);
        System.out.println("Console: doGET visited");// audit log

        // if the path is "isValid", then invoke the isChainValid method from the blockchain class
        //----------------------------------------------------------------------------------------
        if (name.equals("isValid")) {
            response.setStatus(200);
            // set the response header
            response.setContentType("text/plain;charset=UTF-8");
            // return the value from a GET request
            String result = bc.isChainValid();
            PrintWriter out = response.getWriter();
            out.println(result);
        }

        // if the path is blank, then invoke the toString method from the blockchain class 
        // to return the block chain
        //----------------------------------------------------------------------------------------
        if (name.equals("")) {
            // set the response header
            response.setStatus(200);
            response.setContentType("text/plain;charset=UTF-8");
            // return the value from a GET request
            String result = bc.toString();
            PrintWriter out = response.getWriter();
            out.println(result);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("Console: doPost visited");
        // Read the request to extract he parameters
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String data = br.readLine();
        // extract the parameters from request data (Parameters are separated by a comma)
        String[] parameter = data.split(",");
        //-- Parameter 0 - difficulty and parameter 1 - signed transaction data (Signature separated by #)
        String[] trans = parameter[1].split("#");
        //--- public key and exponent for signature verification
        BigInteger e = new BigInteger("65537");
        BigInteger n = new BigInteger("2688520255179015026237478731436571621031218154515572968727588377065598663770912513333018006654248650656250913110874836607777966867106290192618336660849980956399732967369976281500270286450313199586861977623503348237855579434471251977653662553");
        BigInteger cipher = null;
        try {
            //--- get the byte value of the transaction
            byte[] bytesOfTransaction = trans[0].getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            //Compute hash
            byte[] bigDigest = md.digest(bytesOfTransaction);
            // Add a zero byte - To make it symmetric with the approach folowed by client
            byte[] sign = new byte[bigDigest.length + 1];
            sign[0] = 0;
            System.arraycopy(bigDigest, 0, sign, 1, sign.length - 1);
            // From the digest, create a BigInteger
            BigInteger msg = new BigInteger(sign);

            //---Decrypt the hash from incoming message and compare it 
            BigInteger val = new BigInteger(trans[1]).modPow(e, n);
            if (!val.toString().equals(msg.toString())) {
                response.setStatus(401);
                return;
            }
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Error in signing :" + ex.getMessage());
        } catch (UnsupportedEncodingException ex) {
            System.out.println("Byte conversion of transaction :" + ex.getMessage());
        }

        //The signature validation was successful, add the block to the chain
        //-------------------------------------------------------------------
        Block b = new Block(i++, new Timestamp(System.currentTimeMillis()), parameter[1], Integer.parseInt(parameter[0]));
        bc.addBlock(b);
        response.setStatus(200);
        return;
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
