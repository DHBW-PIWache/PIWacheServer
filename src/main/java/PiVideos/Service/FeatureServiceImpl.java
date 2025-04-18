package PiVideos.Service;
import PiVideos.Model.ClientPi;
import PiVideos.Model.Network;
import PiVideos.Model.Video;
import PiVideos.Repository.ClientPiRepository;
import PiVideos.Repository.NetworkRepository;
import PiVideos.Repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;


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

    @Override
    public void saveNetwork(Network network) {
        networkRepository.save(network);
    }



    public List<Network> getAllNetworks(){
        return networkRepository.getAllNetworks();
    }

    public List<ClientPi> getAllClientPis(){return clientPiRepository.getAllClientPis();}

    public List<Video> getAllVideos(){
        return videoRepository.getAllVideos();
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
}
