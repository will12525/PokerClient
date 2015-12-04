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

    private List<String> messages = new ArrayList<>();
    private List<Card> cards = new ArrayList<>(),dealerCards = new ArrayList<>();
    private int highBet=0;
    private boolean askForBet = false,blind=false,askForAnti = false;
    private int totalBet=0,currentBet=0,totalMoney=0,buyInMin=0,buyInMax=0;
    private boolean askForBuyIn = false;
    private int pot=0;
    private List<Card> winningHand = new ArrayList<>();
    private int winnings=0;
    private String clientName;
    private boolean winner=false,newGame=false;


    private JFrame frame;
    private Socket socket;
    private BufferedReader clientInput;
    private MessageSender sender;
    private Deck deck;

    public Main() throws IOException {
        clientName=JOptionPane.showInputDialog("Please enter your name");
        if(clientName==null)
        {
            System.exit(0);
        }
        final int port = 5000;
        final String host = "localhost";
        deck = new Deck(1);
        try{
            socket = new Socket(host,port);
        }catch (ConnectException e)
        {
            System.out.println("Server not open");
            System.exit(0);
        }

        clientInput = new BufferedReader(new InputStreamReader(System.in));

        new MessageReciever(this,socket);
        sender = new MessageSender(this,socket);

        createFrame();
        while(true)
        {
            render();
            if(messages.size()>0) {
                System.out.println(messages.get(0));
                messages.remove(0);
            }
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
            g.drawString("An anti of "+buyInMin+" is needed",400,140);
            g.drawString("Please enter your anti or begin betting",400,160);
            if(blind&&(highBet<=buyInMin))
            {
                g.drawString("You only need to provide "+buyInMin/2+" as you are the blind",400,180);
            }
        }
        if(askForBet)
        {
            g.drawString("Please make a bet ",400,140);
        }
        if(askForBuyIn)
        {
            g.drawString("Please enter how much you would like to buy in with",400,140);
            g.drawString("The minimum buy in is "+buyInMin,400,160);
        }
        if(winner)
        {
            g.drawString("You won the pot! "+pot,400,140);
        }
        if(newGame)
        {
            g.drawString("A new game will be starting soon",400,140);
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
            clientInput.close();
            socket.close();
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
     /*
            c0 chat
            c1 receive player cards
            c2 get dealer cards
            c3 get high bet
            c4 request bet
            c5 tells player if blind
            c6 requests anti
            c7 get total bet for the round
            c8 get current bet for the betting instance
            c9 get total player money
            c10 gets buyInMin
            c11 gets buyInMax

            c12 request buy in money
            c13 gets pot
            c14 gets winning hand
            c15 get winnings
            c16 request name
            c17 check connection
            c18 new game
    */
    public void addMessage(String message)
     {
         messages.add(message);
     }
    public void addCard(String card)
    {
        cards.add(deck.getCard(card));
    }
    public void addDealerCard(String card)
    {
        dealerCards.add(deck.getCard(card));
    }
    public void setHighBet(int bet)
    {
        highBet=bet;
    }
    public void askForBet()
    {
        askForBet=true;
        int bet;
        int userBet;
        do{
            userBet = getNum(0);
            bet = currentBet+userBet;
        }while(bet<highBet&&userBet>totalMoney);
        sender.addMessage("04"+bet);
        askForBet=false;
    }
    public void setBlind()
    {
        blind = true;
    }
    public void askForAnti()
    {
        askForAnti=true;
        int bet;
        int userBet;
        do{
            userBet = getNum(0);
            bet = currentBet+userBet;
        }while(bet<buyInMin&&bet<highBet&&userBet>totalMoney);
        sender.addMessage("06"+bet);
        askForAnti=false;
    }
    public void setTotalBet(int tBet)
    {
        totalBet=tBet;
    }
    public void setCurrentBet(int cBet)
    {
        currentBet=cBet;
    }
    public void setTotalMoney(int tMon)
    {
        totalMoney=tMon;
    }
    public void setMin(int min)
    {
        buyInMin=min;
    }
    public void setMax(int max)
    {
        buyInMax=max;
    }
    public void requestBuyInMoney()
    {
        askForBuyIn=true;
        int buyIn;
        int toSend;
        do{
            buyIn = getNum(0);
            toSend = currentBet+buyIn;
        }while(toSend<buyInMin);
        sender.addMessage("12"+toSend);
        askForBuyIn=false;
    }
    public void setPot(int pot)
    {
        this.pot=pot;
    }
    public void theWinningHand(boolean winner,String winningHandS)
    {
        this.winner=winner;
        if(!winner)
        {
            String card1 = winningHandS.substring(winningHandS.indexOf(",")-1);
            System.out.println(card1);
            System.out.println(winningHandS.substring(card1.length()+1));

            winningHand.add(deck.getCard(card1));
            winningHand.add(deck.getCard(winningHandS.substring(card1.length()+1)));
        }
    }
    public void setWinnings(int muns)
    {
        winnings=muns;
    }
    public void requestName()
    {
        sender.addMessage("16"+clientName);
    }
    public void sayHello()
    {
        sender.addMessage("17");
    }
    public void newGame()
    {
        newGame=true;
        cards.clear();
        dealerCards.clear();
        winningHand.clear();
        highBet=0;
        askForBet = false;
        blind=false;
        askForAnti = false;
        totalBet=0;
        currentBet=0;
        askForBuyIn=false;
        deck=new Deck(1);
    }

    public static void main(String[] args) throws Exception
    {
        new Main();
    }
}