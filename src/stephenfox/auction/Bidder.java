package stephenfox.auction;

import stephenfox.tcp.ClientHandler;
import stephenfox.tcp.Server;

/**
 * Created by stephenfox on 31/10/2016.
 */
public class Bidder implements Registrable {

  private ClientHandler clientHandler;


  /**
   * Constructs a new instance with reference to a ClientHandler object.
   * The ClientHandler object will be messaged, when new information about the current auction is announced
   * from the auctioneer.
   * @param clientHandler The client handler object.
   * */
  public Bidder(ClientHandler clientHandler) {
    this.clientHandler = clientHandler;
  }

  /**
   * Parses incoming messages from clients and performs appropriate action.
   * @param message The message that was send from the client.
   * */
  public void handleClientMessage(String message) {
    System.out.println("New message received from client: " + message);
    Auctioneer auctioneer = Auctioneer.sharedInstance();

    if (message.contains(Server.ServerCommandMessages.CLIENT_JOIN_AUCTION_COMMAND)) {
      // Register with auctioneer.
      auctioneer.registerBidder(this);
    }
    else if (message.contains(Server.ServerCommandMessages.CLIENT_BID_COMMAND)) {
      String[] commandSplit = message.split(" ");
      String bidAmountString;
      double bidAmount;

      if (commandSplit.length > 1) {
        bidAmountString = commandSplit[1];
        try {
          bidAmount = Double.parseDouble(bidAmountString);
        } catch (NumberFormatException e) {
          auctionInfoMessage(Server.ServerCommandMessages.INVALID_BID_FORMAT_COMMAND);
          return;
        }
      } else {
        auctionInfoMessage(Server.ServerCommandMessages.INVALID_BID_FORMAT_COMMAND);
        return;
      }

      AuctionItem item = auctioneer.getCurrentAuctionItem();
      try {
        item.increaseAuctionPrice(bidAmount);
      } catch (AuctionPriceException e) {
        auctionInfoMessage(e.getMessage());
        return;
      }
      auctioneer.newBid(item);
    }
    else {
      auctionInfoMessage(Server.ServerCommandMessages.UNKNOWN_COMMAND);
    }
  }


  /// Registrable interface.
  @Override
  public void auctionInfoMessage(String message) {
    // Message the client of new info about the auction.
    this.clientHandler.messageClient(message);
  }
}
