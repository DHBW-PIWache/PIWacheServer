package PiVideos.Service;


import PiVideos.Model.ClientPi;
import PiVideos.Model.Network;
import PiVideos.Model.Video;

import java.util.List;
import java.util.Optional;

public interface FeatureService {

   public void saveNetwork(Network network);
   public List<Network> getAllNetworks();

   public void saveClient(ClientPi clientPi);

   public List<ClientPi> getAllClientPis();

   public List<Video> getAllVideos();

   public boolean deleteVideoByID(Integer _id);

   public boolean updateVideo(Video video);

   public Optional<Video> getVideoBy_id(Integer _id);
}
