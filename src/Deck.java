import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lawrencew on 11/27/2015.
 */
public class Deck {
    private List<Card> deck = new ArrayList<>();
    private int amount=0;
    private Card holder;

    public Deck(int amount) {
        this.amount=amount;
        loadCards();
        shuffle();
    }

    public int getSize() {
        return deck.size();
    }

    public Card removeCard(int position) {
        Card toReturn = deck.get(position);
        deck.remove(position);
        return toReturn;
    }
    public Card getCard(String name)
    {
        for(Card card : deck)
        {
            if(card.compare(name))
            {
                return card;
            }
        }
        return null;
    }
    public Card getCard(int position) {
        return deck.get(position);
    }
    public void add(Card card)
    {
        deck.add(card);
    }

    public Card getHolder() {
        return holder;
    }
    public void shuffle()
    {
        for(int x=0;x<deck.size()*5;x++) {
            int spot1=(int) (Math.random()*deck.size());
            int spot2=(int) (Math.random()*deck.size());
            Card holder = deck.get(spot1);
            Card holder2 = deck.get(spot2);
            deck.set(spot1, holder2);
            deck.set(spot2, holder);
        }
    }
    private void loadCards() {
        String path = "cards/";
        for(int j=0;j<amount;j++) {
            for (int k = 1; k < 14; k++) {
                String cardPath = path + k + "Spade.jpg";
                deck.add(createCard("spade", k, cardPath));
                cardPath = path + k + "Club.jpg";
                deck.add(createCard("club", k, cardPath));
                cardPath = path + k + "Heart.jpg";
                deck.add(createCard("heart", k, cardPath));
                cardPath = path + k + "Diamond.jpg";
                deck.add(createCard("diamond", k, cardPath));
            }
        }
        holder=createCard("",0,path+"blankCard.png");

    }
    private Card createCard(String suite,int value,String cardPath) {
        BufferedImage img=null;
        try {
            img = ImageIO.read(this.getClass().getResourceAsStream("/" + cardPath));
        } catch (Exception e) {

            System.out.println(e.getMessage());
        }
        if(img==null) {
            System.out.println("there was an error loading images, good bye");
            System.exit(0);
        }
        return new Card(suite,value,resize(img));
    }
    private BufferedImage resize(BufferedImage img) {
        int scale = 3;

        Image tmp = img.getScaledInstance(img.getWidth()/scale, img.getHeight()/scale, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(img.getWidth()/scale, img.getHeight()/scale, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }
}