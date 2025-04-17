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
    public void deleteVideoByID(Integer _id) {
        File file = new File(videoRepository.findById(_id).get().getPath());
        if(file.exists()){
            file.delete();
        } else{
            System.out.println("No video found");
        }

        videoRepository.deleteById(_id);
    }

    @Override
    public void updateVideo(Video video) {
        videoRepository.save(video);
    }

    @Override
    public void saveClient(ClientPi clientPi) {

        clientPiRepository.save(clientPi);
    }

    @Override
    public Video getVideoBy_id(Integer _id) {
        return videoRepository.findById(_id).get();
    }
}
