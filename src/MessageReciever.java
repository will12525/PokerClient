import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lawrencew on 12/2/2015.
 */
public class MessageReciever extends Thread {
    Main main;
    BufferedReader fromServer;
    Decode decode;

    public MessageReciever(Main main, Socket socket, BufferedReader clientInput, PrintWriter toServer) throws IOException
    {
        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.main=main;
        decode=new Decode(main);
        start();
        new MessageSender(clientInput,toServer).start();

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
                        main.close();
                        fromServer.close();
                        interrupt();
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
    class MessageSender extends Thread{
        private List<String> messages = new ArrayList<>();
        BufferedReader clientInput;
        PrintWriter toServer;
        public MessageSender(BufferedReader clientInput,PrintWriter toServer)
        {
            this.toServer=toServer;
            this.clientInput=clientInput;
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
}
