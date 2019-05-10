/*
 * Copyright (c) 1995, 2013, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

import java.net.*;
import java.io.*;

public class IMMultiServerThread extends Thread {
    private Socket socket = null;

    public IMMultiServerThread(Socket socket) {
        super("IMMultiServerThread");
        this.socket = socket;
    }

    public void run() {
        try (
        //PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        //BufferedReader in = new BufferedReader(
        //  new InputStreamReader(
        //      socket.getInputStream()))
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream()); 
        ) {
            BufferedReader stdIn =
                new BufferedReader(new InputStreamReader(System.in));
            //Message inMessage = new Message();
            String clientInput;
            String serverInput;
            String board = "";
            String client = "Client";
            String coordinates;
            int response = 0;

            // Initiate conversation with client
            BattleshipProtocol IMp = new BattleshipProtocol();
            // Processes the input (initial)
            board = IMp.processInput(null);

            // Sends the initial input from server to client
            out.writeObject(board);

            // While the object sent from client is not null
            while ((clientInput = (String) in.readObject()) != null) {
                // Print message from client
                System.out.println(client + ": " + clientInput);

                // Client breaks connection
                if (clientInput.equals("congrats"))
                    break;

                // Read response from server (Step 10)
                coordinates = stdIn.readLine();

                // If board is not null, processes coordinates taken as input
                if(board != null)
                {
                    serverInput = IMp.processInput(coordinates);
                    /*Can comment this out so doesnt just shut them out*/
                    if(serverInput.equals("GAME OVER"))
                        break;
                    System.out.println("Server: " + serverInput);
                    out.writeObject(board);
                }
                
                
                //if (board.equals("Bye"))
                    //break;
            }
            socket.close(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
