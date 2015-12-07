import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by lawrencew on 12/3/2015.
 */
public class MessageSender{
    PrintWriter toServer;
    Main main;
    public MessageSender(Main main,Socket socket)
    {
        try {
            toServer = new PrintWriter(socket.getOutputStream(), true);
        }catch(IOException e)
        {
            main.close();
        }
        this.main=main;
    }
    public void addMessage(String message)
    {
        System.out.println("sending "+message);
        toServer.println(message);
    }
}
