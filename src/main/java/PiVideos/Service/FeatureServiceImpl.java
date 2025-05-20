package PiVideos.Service;
import PiVideos.Model.ClientPi;
import PiVideos.Model.Network;
import PiVideos.Model.Video;
import PiVideos.Repository.ClientPiRepository;
import PiVideos.Repository.NetworkRepository;
import PiVideos.Repository.VideoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/*******************************************************************************************************
 Autor: Julian Hecht
 Datum letzte Änderung: 22.04.2025
 Änderung: Kommentare hinzugefügt
 *******************************************************************************************************/

@Service
public class FeatureServiceImpl implements FeatureService {

    @Autowired
    NetworkRepository networkRepository;

    @Autowired
    ClientPiRepository clientPiRepository;

    @Autowired
    VideoRepository videoRepository;

    public FeatureServiceImpl(NetworkRepository networkRepository, ClientPiRepository clientPiRepository, VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
        this.networkRepository = networkRepository;
        this.clientPiRepository = clientPiRepository;
    }

    // Netzwerk anlegen
    @Override
    public void saveNetwork(Network network) {
        networkRepository.save(network);
    }

    public void deleteNetworkByID(Integer id){
        Network network = networkRepository.findById(id).orElseThrow();
        for(ClientPi c : network.getClientPis()){
            deleteClientPiById(c.get_id());
        }
        network.getClientPis().clear();
        networkRepository.deleteById(id);

    }

    public List<Network> getAllNetworks(){
        return networkRepository.getAllNetworks();
    }

    public List<ClientPi> getAllClientPis(Network network){
        return clientPiRepository.findAllByNetworkId(network.get_id());
    }

    public List<Video> getAllVideos(){
        return videoRepository.getAllVideos();
    }
    public List<Video> getAllVideosForNetwork(Network network){
        return videoRepository.findAllVideosByNetzwerkId(network.get_id());
    }

    @Override
    public boolean deleteVideoByID(Integer _id) {
        File file = new File(videoRepository.findById(_id).get().getPath());
        if(file.exists()){
        videoRepository.deleteById(_id);
        return file.delete();
        } else{
            return false;
        }
    }

    @Override
    public boolean updateVideo(Video video) {
        return videoRepository.save(video).equals(video);
    }

    @Override
    public void saveClient(ClientPi clientPi) {

        clientPiRepository.save(clientPi);
    }

    @Override
    public Optional<Video> getVideoBy_id(Integer _id) {
        return videoRepository.findById(_id);
    }

    public Video getNewestVideo(Network network){
        return videoRepository.findNewestVideo(network.get_id());
    }

    public int countVids(Network network){
        return videoRepository.countAllVids(network.get_id());
    }

    public int countVidsForClient(Network network, ClientPi clientPi){
        return clientPiRepository.countAllVideosForClient(network.get_id(),clientPi.get_id());
    }

    public LocalDateTime getLatestVideo(Network network, ClientPi clientPi){
        return clientPiRepository.getLatestVideo(network.get_id(),clientPi.get_id());
    }

    public double totalMBForNetwork(Network network){
        return networkRepository.totalMBForNetwork(network.get_id());
    }

    //Noch vielleicht als Boolean umschreiben
    @Override
    public void deleteClientPiById(Integer id) {
        ClientPi client = clientPiRepository.findById(id).orElseThrow();
        for(Video v : client.getVideos()){
            deleteVideoByID(v.get_id());
        }
        client.getVideos().clear();
        clientPiRepository.delete(client);
    }

    public Optional<ClientPi> getClientBy_id(Integer _id){
        return clientPiRepository.findById(_id);
    }


    public boolean updateClient(ClientPi clientPi){
        return clientPiRepository.save(clientPi).equals(clientPi);
    }

 
    
}
