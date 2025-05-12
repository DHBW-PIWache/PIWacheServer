package PiVideos.Service;


import PiVideos.Model.ClientPi;
import PiVideos.Model.Network;
import PiVideos.Model.Video;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FeatureService {

   public void saveNetwork(Network network);
   public List<Network> getAllNetworks();

   public void saveClient(ClientPi clientPi);

   public List<ClientPi> getAllClientPis(Network network);

   public List<Video> getAllVideos();
   public List<Video> getAllVideosForNetwork(Network network);

   public boolean deleteVideoByID(Integer _id);

   public boolean updateVideo(Video video);

   public Optional<Video> getVideoBy_id(Integer _id);

   public Video getNewestVideo(Network network);

   public int countVids(Network network);

   public void deleteClientPiById(Integer id);

   public Optional<ClientPi> getClientBy_id(Integer _id);

   public boolean updateClient(ClientPi clientPi);

   public void deleteNetworkByID(Integer id);

   public int countVidsForClient(Network network, ClientPi clientPi);

   public LocalDateTime getLatestVideo(Network network, ClientPi clientPi);

   public double totalMBForNetwork(Network network);

}
