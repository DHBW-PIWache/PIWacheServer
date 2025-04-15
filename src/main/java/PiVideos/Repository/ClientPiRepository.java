package PiVideos.Repository;

import PiVideos.Model.ClientPi;
import PiVideos.Model.Network;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClientPiRepository extends CrudRepository<ClientPi,Integer> {

    public ClientPi findByName(String name);

    @Query(value = "SELECT * FROM client_pi", nativeQuery = true)
    public List<ClientPi> getAllClientPis();
}
