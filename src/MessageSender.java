import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lawrencew on 12/3/2015.
 */
public class MessageSender extends Thread{
    private List<String> messages = new ArrayList<>();
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
        start();
    }
    public void addMessage(String message)
    {
        messages.add(message);
    }
    public void run()
    {
        while(true) {
            if(messages.size()>0) {
                String message = messages.get(0);
                messages.remove(0);
                if (message.equals("exit")) {
                    toServer.println("19");
                    toServer.close();
                    main.close();
                    return;
                } else {
                    toServer.println(message);
                }
            }
        }
    }
}
