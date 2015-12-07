/**
 * Created by lawrencew on 12/2/2015.
 */
public class Decode{
    private Main main;

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
            c19 fold
            c20 new game
            c21 total players in game
    */
    public void sendMessage(String message) {
        //00 chat
        if (message.substring(0, 2).equals("00")) {
            main.addMessage(message.substring(2));
        }
        //01 add cards to player cards
        if (message.substring(0, 2).equals("01")) {
            main.addCard(message.substring(2));
        }
        //02 add cards to dealer cards
        if (message.substring(0, 2).equals("02")) {
            main.addDealerCard(message.substring(2));
            System.out.println("Recieved dealer card: "+message.substring(2));
        }
        //03 passes the high bet to the player
        if (message.substring(0, 2).equals("03")) {
            main.setHighBet(Integer.parseInt(message.substring(2)));
        }
        //04 request bet
        if (message.substring(0, 2).equals("04")) {
            main.askForBet();
        }
        //05 tells player if blind
        if (message.substring(0, 2).equals("05")) {
            main.setBlind();
        }
        //06 requests anti
        if (message.substring(0, 2).equals("06")) {
            main.askForAnti();
        }
        //07 get total bet for the round
        if (message.substring(0, 2).equals("08")) {
            main.setTotalBet(Integer.parseInt(message.substring(2)));
        }
        //09 get total player money
        if (message.substring(0, 2).equals("09")) {
            main.setTotalMoney(Integer.parseInt(message.substring(2)));
        }
        //10 gets buyInMin
        if (message.substring(0, 2).equals("10")) {
            main.setMin(Integer.parseInt(message.substring(2)));
        }
        //11 gets buyInMax
        if (message.substring(0, 2).equals("11")) {
            main.setMax(Integer.parseInt(message.substring(2)));
        }
        //12 request buy in money
        if (message.substring(0, 2).equals("12")) {
            main.requestBuyInMoney();
        }
        //13 get pot
        if (message.substring(0, 2).equals("13")) {
            main.setPot(Integer.parseInt(message.substring(2)));
        }
        //14 gets winning hand
        if (message.substring(0, 2).equals("14")) {
            boolean winner = false;
            if (message.charAt(2) == 't') {
                winner = true;
            }
            main.theWinningHand(winner, message.substring(3));
        }
        //15 get winnings
        if (message.substring(0, 2).equals("15")) {
            main.setWinnings(message.substring(2));
        }
        //16 request name
        if (message.substring(0, 2).equals("16")) {
            main.requestName();
        }
        if (message.substring(0, 2).equals("18")) {
            main.newGame();
        }
        if(message.substring(0,2).equals("20"))
        {
            main.newGame();
        }
        if(message.substring(0,2).equals("21"))
        {
            main.setPlayers(message.substring(2));
        }
    }
}
