package acm35.com.autoblind;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable{

    private String host = "antondubek2.hopto.org";
    private int port = 12345;

    private String command;

    private String currentPosition = "1";


    public Client(String command){
        this.command = command;
    }


    @Override
    public void run() {
        try {
            //Connect to socket
            Socket socket = new Socket(host, port);
            System.out.println("Client connected");

            //Create reader and writer
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //Send the command to the server
            String toSend = "PUT /" + command;
            pw.println(toSend);
            pw.flush();

            //Get the response and save it
            currentPosition = br.readLine();
            System.out.println("Current position = " + currentPosition);

            //Close everything
            br.close();
            pw.close();
            socket.close();
        } catch (Exception e){ // exit cleanly for any Exception (including IOException, DisconnectedException)
            System.out.println("Ooops on connection: " + e.getMessage());
        }
    }

    public int getCurrentPosition(){
        return Integer.parseInt(currentPosition);
    }
}
