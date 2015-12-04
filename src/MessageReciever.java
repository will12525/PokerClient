import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by lawrencew on 12/2/2015.
 */
public class MessageReciever extends Thread {
    Main main;
    BufferedReader fromServer;
    Decode decode;

    public MessageReciever(Main main, Socket socket) throws IOException
    {
        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.main=main;
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
                    else {
                        decode.addMessage(message);
                    }
                }
            }catch (IOException e)
            {
              interrupt();
            }
        }
    }
}
