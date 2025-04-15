package PiVideos.Service;


import PiVideos.Model.ClientPi;
import PiVideos.Model.Network;

import java.util.List;

public interface ConfigService {

   public void saveNetwork(Network network);
   public List<Network> getAllNetworks();

   public void saveClient(ClientPi clientPi);

   public List<ClientPi> getAllClientPis();
}
