package PiVideos.Service;
import PiVideos.Model.ClientPi;
import PiVideos.Model.Network;
import PiVideos.Repository.ClientPiRepository;
import PiVideos.Repository.NetworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    NetworkRepository networkRepository;

    @Autowired
    ClientPiRepository clientPiRepository;

    public ConfigServiceImpl(NetworkRepository networkRepository, ClientPiRepository clientPiRepository) {

        this.networkRepository = networkRepository;
        this.clientPiRepository = clientPiRepository;
    }

    @Override
    public Network saveNetwork(Network network) {
        return networkRepository.save(network);
    }


    public List<Network> getAllNetworks(){
        return networkRepository.getAllNetworks();
    }

    @Override
    public ClientPi saveClient(ClientPi clientPi) {

        return clientPiRepository.save(clientPi);
    }
}
