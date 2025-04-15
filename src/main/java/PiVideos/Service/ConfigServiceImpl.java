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
    public void saveNetwork(Network network) {
        networkRepository.save(network);
    }


    public List<Network> getAllNetworks(){
        return networkRepository.getAllNetworks();
    }

    public List<ClientPi> getAllClientPis(){return clientPiRepository.getAllClientPis();}

    @Override
    public void saveClient(ClientPi clientPi) {

        clientPiRepository.save(clientPi);
    }


}
