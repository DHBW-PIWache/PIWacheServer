package PiVideos.Service;

import PiVideos.Model.ClientPi;
import PiVideos.Model.Network;
import PiVideos.Model.Video;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FeatureService {

    // Netzwerk Methoden
    boolean saveNetwork(Network network);

    Network getNetworkById(Integer id);

    boolean deleteNetworkByID(Integer id);

    List<Network> getAllNetworks();

    double totalMBForNetwork(Network network);

    // Client Methoden
    List<ClientPi> getAllClientPis(Network network);

    boolean saveClient(ClientPi clientPi);

    boolean deleteClientPiById(Integer id);

    Optional<ClientPi> getClientBy_id(Integer _id);

    boolean updateClient(ClientPi clientPi);

    // Video Methoden
    List<Video> getAllVideosForNetwork(Network network);

    boolean deleteVideoByID(Integer _id);

    boolean updateVideo(Video video);

    Optional<Video> getVideoBy_id(Integer _id);

    Video getNewestVideo(Network network);

    int countVids(Network network);

    int countVidsForClient(Network network, ClientPi clientPi);

    LocalDateTime getLatestVideo(Network network, ClientPi clientPi);

}
