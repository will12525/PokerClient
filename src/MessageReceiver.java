import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by lawrencew on 12/2/2015.
 */
public class MessageReceiver extends Thread {
    Main main;
    BufferedReader fromServer;
    Decode decode;
    MessageSender sender;
    public MessageReceiver(Main main, Socket socket, MessageSender sender) throws IOException
    {
        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.main=main;
        this.sender=sender;
        decode=new Decode(main);
        start();
    }
    public void run()
    {
        String message;
        while (true) {
            try {
                if ((message = fromServer.readLine()) != null) {
                    if(message.equals("stop"))
                    {
                        System.out.println("Server: Server closing");
                        fromServer.close();
                        main.close();
                        return;
                    }
                    else if (message.substring(0, 2).equals("17")) {
                        System.out.println("Must respond");
                        sender.addMessage("17");
                    }
                    else {

                        decode.sendMessage(message);
                    }
                }
            }catch (Exception e)
            {

            }
        }
    }
}
