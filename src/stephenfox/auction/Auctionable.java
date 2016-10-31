package stephenfox.auction;

/**
 * Created by stephenfox on 31/10/2016.
 */
public interface Auctionable {
  void setName(String name);
  String getName();
  double getBasePrice();
  double getAuctionPrice();
  void increaseAuctionPrice(double amount);
  boolean hasExpired();
  void auction(AuctionExpiration expirationCallback);
}
