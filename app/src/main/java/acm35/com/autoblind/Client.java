package acm35.com.autoblind;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable{

    //private String host = "antondubek2.hopto.org";
    //private String host = "192.168.1.78";
    private String host = "192.168.0.32";
    private int port = 12345;

    private String command;
    private String openTime, closeTime;
    private String toSend;
    private Boolean enabled;

    private String[] responseData;

    private String currentPosition;


    public Client(String command){
        this.command = command;
        //this.currentPosition = "3";
    }

    public Client(String command, boolean enabled, String openTime, String closeTime){
        this.command = command;
        this.enabled = enabled;
        this.openTime = openTime;
        this.closeTime = closeTime;
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

            if(command.equals("PUT /time")){
                toSend = "PUT /time " + enabled + " " + openTime + " " + closeTime;
            } else {
                toSend = command;
            }
            //Send the command to the server
            pw.println(toSend);
            pw.flush();

            //Get the response and save it
            String serverResponse = br.readLine();

            if (command.equals("GET /Time")){
                responseData = serverResponse.split(",");
            } else {
                currentPosition = serverResponse;
            }
            System.out.println("Current position = " + currentPosition);

            //Close everything
            br.close();
            pw.close();
            socket.close();
            return;
        } catch (Exception e){ // exit cleanly for any Exception (including IOException, DisconnectedException)
            System.out.println("Ooops on connection: " + e.getMessage());
        }
    }

    public int getCurrentPosition(){
        return Integer.parseInt(currentPosition);
    }

    public String[] getCurrentTime(){
        return responseData;
    }
}
