package PiVideos.Service;


import PiVideos.Model.ClientPi;
import PiVideos.Model.Network;
import PiVideos.Model.Video;

import java.util.List;

public interface FeatureService {

   public void saveNetwork(Network network);
   public List<Network> getAllNetworks();

   public void saveClient(ClientPi clientPi);

   public List<ClientPi> getAllClientPis();

   public List<Video> getAllVideos();

   public void deleteVideoByID(Integer id);
}
