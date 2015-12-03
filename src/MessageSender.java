import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lawrencew on 12/3/2015.
 */
public class MessageSender extends Thread{
    private List<String> messages = new ArrayList<>();
    BufferedReader clientInput;
    PrintWriter toServer;
    Main main;
    public MessageSender(Main main,BufferedReader clientInput,PrintWriter toServer)
    {
        this.toServer=toServer;
        this.clientInput=clientInput;
        this.main=main;
    }
    public void addMessage(String message)
    {
        messages.add(message);
    }
    public void run()
    {
        while(true) {
            String message = messages.get(0);
            messages.remove(0);
            if(message.equals("exit"))
            {
                main.close();
                interrupt();
            }
            else
            {
                toServer.println(message);
            }
        }
    }
}
