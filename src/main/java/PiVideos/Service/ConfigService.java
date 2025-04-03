package PiVideos.Service;


import PiVideos.Model.ClientPi;
import PiVideos.Model.Network;

import java.util.List;

public interface ConfigService {

   public Network saveNetwork(Network network);
   public List<Network> getAllNetworks();

   public ClientPi saveClient(ClientPi clientPi);
}
