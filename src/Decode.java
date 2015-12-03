import java.util.List;

/**
 * Created by lawrencew on 12/2/2015.
 */
public class Decode extends Thread{
    private Main main;
    private List<String> messages;
    public Decode(Main main)
    {
        this.main=main;
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
            c13 gets winning hand
            c14 get winnings
            c15 request name
    */
    public void addMessage(String newMessage)
    {
        messages.add(newMessage);
        start();
    }
    public void run()
    {
        String message = messages.get(0);
        messages.remove(0);
        //00 chat
        if(message.substring(0,2).equals("00"))
        {
           main.addMessage(message.substring(2));
        }
        //01 add cards to player cards
        if(message.substring(0,2).equals("01"))
        {
            main.addCard(message.substring(2));
        }
        //02 add cards to dealer cards
        if(message.substring(0,2).equals("02"))
        {
            main.addDealerCard(message.substring(2));
        }
        //03 passes the high bet to the player
        if(message.substring(0,2).equals("03"))
        {
            main.setHighBet(Integer.parseInt(message.substring(2)));
        }
        //04 request bet
        if(message.substring(0,2).equals("04"))
        {
            main.askForBet();
        }
        //05 tells player if blind
        if(message.substring(0,2).equals("5"))
        {
            main.setBlind();
        }
        //06 requests anti
        if(message.substring(0,2).equals("06"))
        {
            main.askForAnti();
        }
        //07 get total bet for the round
        if(message.substring(0,2).equals("07"))
        {
            main.setTotalBet(Integer.parseInt(message.substring(2)));
        }
        //08 get current bet for the betting instance
        if(message.substring(0,2).equals("08"))
        {
           main.setCurrentBet(Integer.parseInt(message.substring(2)));
        }
        //09 get total player money
        if(message.substring(0,2).equals("09"))
        {
            main.setTotalMoney(Integer.parseInt(message.substring(2)));
        }
        //10 gets buyInMin
        if(message.substring(0,2).equals("10"))
        {
            main.setMin(Integer.parseInt(message.substring(2)));
        }
        //11 gets buyInMax
        if(message.substring(0,2).equals("11"))
        {
            main.setMax(Integer.parseInt(message.substring(2)));
        }
        //12 request buy in money
        if(message.substring(0,2).equals("12"))
        {
            main.requestBuyInMoney();
        }
        //13 gets winning hand
        if(message.substring(0,2).equals("13"))
        {


        }
        //14 get winnings
        if(message.substring(0,2).equals("14")) {

        }
        //15 request name
        if(message.substring(0,2).equals("15"))
        {
            main.requestName();
        }
    }
}
