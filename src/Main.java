import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lawrencew on 11/27/2015.
 */
public class Main extends Canvas {

    private Socket socket;
    private BufferedReader fromServer;
    private PrintWriter toServer;
    private BufferedReader clientInput;
    private boolean running = true;
    private boolean blind = false;
    private int totalMoney=0;
    private int currentBet=0;
    private int totalBet = 0;
    private int highBet=0;
    private int pot;
    private List<Card> cards = new ArrayList<>();
    private List<Card> dealerCards = new ArrayList<>();
    private List<String> messages = new ArrayList<>();
    private Deck deck;
    private String clientName;

    private boolean askForAnti = false;
    private boolean askForBet = false;

    private int buyInMin,buyInMax;

    private JFrame frame;

    public Main() throws IOException {

        final int port = 5000;
        final String host = "localhost";
        deck = new Deck(1);
        try{
            socket = new Socket(host,port);
        }catch (ConnectException e)
        {
        }

        toServer = new PrintWriter(socket.getOutputStream(),true);
        clientInput = new BufferedReader(new InputStreamReader(System.in));

       /* System.out.print("please enter a name: ");
        clientName=clientInput.readLine();

        toServer.println(clientName);*/
        new MessageReciever(this,socket,clientInput,toServer);
        MessageReciever.MessageSender sender = new MessageReciever.MessageSender(clientInput,toServer);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*do {
            System.out.println("Please enter how much money you would like to buy in with\nMinimum: "+buyInMin+", Maximum: "+buyInMax);
            totalMoney=getNum(0);
            System.out.println(totalMoney + ", " + buyInMin);
        }while(totalMoney<buyInMin);

        toServer.println("11"+totalMoney);
*/
        createFrame();
        while(true)
        {
            render();
        }

    }
    public void render()
    {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, frame.getWidth(), frame.getHeight());

        g.setColor(Color.WHITE);
        if(blind)
        {
            g.drawString("You are the blind",450,20);
        }
        g.drawString("Player: "+ clientName,450,40);
        g.drawString("Money: "+ totalMoney,450,60);
        g.drawString("Your bet: "+totalBet,450,80);
        g.drawString("The pot: "+pot,450,100);

        if(askForAnti)
        {
            g.drawString("An anti of "+buyInMin+" is needed",450,140);
            g.drawString("Please enter your anti or begin betting",450,160);
            if(blind&&(highBet<=buyInMin))
            {
                g.drawString("You only need to provide "+buyInMin/2+" as you are the blind",450,180);
            }
        }
        if(askForBet)
        {
            g.drawString("Please make a bet ",450,140);
        }

        for(int x=0;x<cards.size();x++)
        {
            Card card = cards.get(x);
            BufferedImage img = card.getCard();
            g.drawImage(img,150*(x+1),200,img.getWidth(),img.getHeight(),null);
        }
        for(int x = 0;x<dealerCards.size();x++)
        {
            Card card = dealerCards.get(x);
            BufferedImage img = card.getCard();
            g.drawImage(img,100*(x+1),20,img.getWidth(),img.getHeight(),null);
        }

        g.dispose();
        bs.show();
    }
    public void close()
    {
        try {
            toServer.println("exit");
            toServer.close();
            clientInput.close();
            socket.close();
            running = false;

        }catch (IOException e)
        {

        }
        System.exit(0);
    }
    public void createFrame()
    {
        int WIDTH=600;
        int HEIGHT=400;
        frame = new JFrame("Texas holdem");
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(this,BorderLayout.CENTER);
        frame.setResizable(false);
        setIgnoreRepaint(true);
        frame.setVisible(true);

    }
    public int getNum(int increments)
    {
        if(increments>30)
        {
            return 0;
        }
        int theNumber;
        try {
            theNumber = Integer.parseInt(clientInput.readLine());
        } catch (Exception e) {
            System.out.println("Please enter a valid number");
            return getNum(increments);
        }
        return theNumber;
    }

    public void addCard(String card)
    {
        cards.add(deck.getCard(card));
    }
    public List<Card> getCards()
    {
        return cards;
    }
    public void addDealerCard(String card)
    {
        dealerCards.add(deck.getCard(card));
    }
    public List<Card> getDealerCards()
    {
        return dealerCards;
    }
    public void setHighBet(int bet)
    {
        highBet=bet;
    }
    public int getHighBet()
    {
        return highBet;
    }
    public void setBlind()
    {
        blind = true;
    }
    public boolean checkBlind()
    {
        return blind;
    }
    public void sendToServer(String message)
    {
        System.out.println("sending");
        toServer.println(message);
    }
    public void setTotalBet(int tBet)
    {
        totalBet=tBet;
    }
    public int getTotalBet()
    {
        return totalBet;
    }
    public void setCurrentBet(int cBet)
    {
        currentBet=cBet;
    }
    public int getCurrentBet()
    {
        return currentBet;
    }
    public void setTotalMoney(int tMon)
    {
        totalMoney=tMon;
    }
    public int getTotalMoney()
    {
        return totalMoney;
    }

    public void setMin(int min)
    {
        buyInMin=min;
    }
    public int getMin()
    {
        return buyInMin;
    }
    public void setMax(int max)
    {
        buyInMax=max;
    }
    public int getMax()
    {
        return buyInMax;
    }
    public void setPot(int pot)
    {
        this.pot=pot;
    }
    public int getPot()
    {
        return pot;
    }
    public void askForBet()
    {
        askForBet=true;
        int bet = 0;
        do{
            bet = currentBet+getNum(0);
        }while(bet<=buyInMin);
        sendToServer("08" + bet);
        askForBet=false;
    }
    public void askForAnti()
    {
        askForAnti=true;
        int bet = 0;
        do{
            bet = currentBet+getNum(0);
        }while(bet<=buyInMin);
        sendToServer("08" + bet);
        askForAnti=false;
    }
    public void addMessage(String message)
    {
        messages.add(message);
    }
    public static void main(String[] args) throws Exception
    {
        new Main();
    }
}