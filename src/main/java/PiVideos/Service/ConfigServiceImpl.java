package PiVideos.Service;
import PiVideos.Model.Network;
import PiVideos.Repository.NetworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    NetworkRepository networkRepository;

    public ConfigServiceImpl(NetworkRepository networkRepository) {
        this.networkRepository = networkRepository;
    }


    @Override
    public Network saveNetwork(Network network) {
        return networkRepository.save(network);
    }
}
