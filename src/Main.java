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
    private boolean blind=false;
    private int totalBet=0,totalMoney=0,buyInMin=0,buyInMax=0;
    private boolean askForBuyIn = false;
    private int pot=0;
    private String winningHand;
    private String winnings="";
    private String clientName;
    private boolean winner=false,newGame=false;
    private boolean fold = false;
    private boolean gameOver = false;
    private String winningPlayer="";
    private JFrame frame;
    private Socket socket;
    private BufferedReader clientInput;
    private MessageSender sender;
    private Deck deck;
    private String totalPlayers="";

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


        sender = new MessageSender(this,socket);
        new MessageReciever(this,socket,sender);

        createFrame();
        while(true)
        {
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
        g.drawString("High bet: "+highBet,450,120);

        g.drawString("There is "+totalPlayers+" people playing",200,20);
        if(gameOver)
        {
           g.drawString(winningPlayer+" won with a: ",450,160);
            g.drawString(winningHand,450,180);
            g.drawString(winnings,450,200);
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
            g.drawImage(img,70*(x+1),20,img.getWidth(),img.getHeight(),null);
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
    public int getNum(int increments,String text)
    {
        if(increments>30)
        {
            return 0;
        }
        int theNumber;
        try {
            String sNum = JOptionPane.showInputDialog(text);
            if(sNum==null)
            {
                folded();
                return -1;
            }
            theNumber = Integer.parseInt(sNum);
        } catch (Exception e) {
            System.out.println("Please enter a valid number");
            return getNum(increments++,text);
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

        int bet;
        int userBet;
        do{
            userBet = getNum(0,"Please enter your bet\nTo fold press cancel, enter 0 to check");
            if(userBet==-1)
            {
                return;
            }
            bet = totalBet+userBet;
            System.out.println(bet+", "+totalBet+", "+highBet);
        }while(bet<highBet||userBet>totalMoney);
        sender.addMessage("04"+userBet);
    }
    public void setBlind()
    {
        blind = true;
    }
    public void askForAnti()
    {
        newGame=false;
        int bet;
        int userBet;
        int filler = buyInMin;
        if(highBet>buyInMin)
        {
            filler=highBet;
        }
        do{
            userBet = getNum(0,"An anti of "+(filler-totalBet)+" is needed, please\n"+
                    "enter your anti or begin betting.\n"+
                    "To fold press cancel, enter 0 to check");
            if(userBet==-1)
            {
                return;
            }
            bet = totalBet+userBet;

        }while(bet<buyInMin||bet<highBet||userBet>totalMoney);
        sender.addMessage("06"+userBet);
    }
    public void setTotalBet(int tBet)
    {
        totalBet=tBet;
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
        int buyIn;
        int toSend;
        do{

            buyIn = getNum(0,"Please enter how much you would like\nto buy in with.\nThe minimum buy in is "+buyInMin);
            if(buyIn==-1)
            {
                System.exit(0);
            }
            toSend = totalBet+buyIn;
        }while(toSend<buyInMin);
        sender.addMessage("12"+toSend);
    }
    public void setPot(int pot)
    {
        this.pot=pot;
    }
    public void theWinningHand(boolean winner,String winningHandS)
    {
        gameOver=true;
        //players name, the hand
        this.winner=winner;

        if(!winner)
        {
            winningPlayer=winningHandS.substring(0,winningHandS.indexOf(","));
            winningHand=winningHandS.substring(winningHandS.indexOf(",")+1);
            winnings="They won "+winnings;
        }
        else
        {
            winningPlayer="You";
            winningHand=winningHandS.substring(winningHandS.indexOf(",")+1);
            winnings="You won "+winnings;
        }
    }
    public void setWinnings(String muns)
    {
        winnings=muns;
    }
    public void requestName()
    {
        sender.addMessage("16"+clientName);
    }
    public void folded()
    {
        fold = true;
        sender.addMessage("19");
    }
    public void newGame()
    {
        gameOver=false;
        newGame=true;
        cards.clear();
        dealerCards.clear();
        winningHand="";
        winnings="";
        winner=false;
        highBet=0;
        blind=false;
        totalBet=0;
        askForBuyIn=false;
        deck=new Deck(1);
    }
    public void setPlayers(String amount)
    {
        totalPlayers = amount;
    }

    public static void main(String[] args) throws Exception
    {
        new Main();
    }
}