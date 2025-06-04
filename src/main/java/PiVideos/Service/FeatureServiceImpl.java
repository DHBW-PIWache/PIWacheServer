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
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class FeatureServiceImpl implements FeatureService {

    @Autowired
    NetworkRepository networkRepository;

    @Autowired
    ClientPiRepository clientPiRepository;

    @Autowired
    VideoRepository videoRepository;

    // -------> Netzwerk Methoden <-------
    // Neues Netzwerk anlegen und in der Datenbank speichern
    @Override
    public boolean saveNetwork(Network network) {
        Network savedNetwork = networkRepository.save(network);
        return savedNetwork != null;
    }

    // Netzwerk anhand der ID holen
    @Override
    public Network getNetworkById(Integer id) {
        return networkRepository.findById(id).orElseThrow();
    }

    // Netzwerk anhand der ID löschen
    @Override
    public boolean deleteNetworkByID(Integer id) {
        try {
            Network network = networkRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Network mit ID " + id + " nicht gefunden"));

            for (ClientPi c : network.getClientPis()) {
                deleteClientPiById(c.get_id());
            }
            network.getClientPis().clear();
            networkRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Alle Netzwerke aus der Datenbank holen
    public List<Network> getAllNetworks() {
        return networkRepository.getAllNetworks();
    }

    // Gesamtgröße in MB für ein Netzwerk berechnen
    public double totalMBForNetwork(Network network) {
        return networkRepository.totalMBForNetwork(network.get_id());
    }

    // -- -------> Client Methoden <-------
    // Alles Clients für ein Netzwerk holen
    public List<ClientPi> getAllClientPis(Network network) {
        return clientPiRepository.findAllByNetworkId(network.get_id());
    }

    // ClientPi speichern
    @Override
    public boolean saveClient(ClientPi clientPi) {
        ClientPi savedClient = clientPiRepository.save(clientPi);
        return savedClient != null;
    }

    // Löscht einen ClientPi anhand der ID
    @Override
    public boolean deleteClientPiById(Integer id) {
        try {
            ClientPi client = clientPiRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("ClientPi mit ID " + id + " nicht gefunden"));

            for (Video v : client.getVideos()) {
                deleteVideoByID(v.get_id());
            }
            client.getVideos().clear();
            clientPiRepository.delete(client);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Holt einen ClientPi anhand der ID
    public Optional<ClientPi> getClientBy_id(Integer _id) {
        return clientPiRepository.findById(_id);
    }

    // Aktualisiert einen ClientPi
    public boolean updateClient(ClientPi clientPi) {
        return clientPiRepository.save(clientPi).equals(clientPi);
    }

    // --------> Video Methoden <-------
    // Alle Videos für ein Netzwerk holen
    public List<Video> getAllVideosForNetwork(Network network) {
        return videoRepository.findAllVideosByNetzwerkId(network.get_id());
    }

    // Video löschen anhand der ID
    @Override
    public boolean deleteVideoByID(Integer _id) {
        File file = new File(videoRepository.findById(_id).get().getPath());
        if (file.exists()) {
            videoRepository.deleteById(_id);
            return file.delete();
        } else {
            return false;
        }
    }

    // Video aktualisieren
    @Override
    public boolean updateVideo(Video video) {
        return videoRepository.save(video).equals(video);
    }

    // Video anhand der ID holen
    @Override
    public Optional<Video> getVideoBy_id(Integer _id) {
        return videoRepository.findById(_id);
    }

    // Neueste Video für ein Netzwerk holen
    public Video getNewestVideo(Network network) {
        return videoRepository.findNewestVideo(network.get_id());
    }

    // Anzahl der Videos für ein Netzwerk zählen
    public int countVids(Network network) {
        return videoRepository.countAllVids(network.get_id());
    }

    // Anzahl der Videos für einen ClientPi zählen
    public int countVidsForClient(Network network, ClientPi clientPi) {
        return clientPiRepository.countAllVideosForClient(network.get_id(), clientPi.get_id());
    }

    // Neueste Video für einen ClientPi holen
    public LocalDateTime getLatestVideo(Network network, ClientPi clientPi) {
        return clientPiRepository.getLatestVideo(network.get_id(), clientPi.get_id());
    }
    
}
