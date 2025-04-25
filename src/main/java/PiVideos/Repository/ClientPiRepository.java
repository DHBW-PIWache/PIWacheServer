package PiVideos.Repository;

import PiVideos.Model.ClientPi;
import PiVideos.Model.Network;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
/*******************************************************************************************************
 Autor: Julian Hecht
 Datum letzte Änderung: 22.04.2025
 Änderung: Kommentare hinzugefügt
 *******************************************************************************************************/
public interface ClientPiRepository extends CrudRepository<ClientPi,Integer> {

    public Optional<ClientPi> findByNameAndNetwork(String name,Network network);

    @Query(value = "SELECT * FROM client_pi WHERE network_id = :networkId", nativeQuery = true)
    List<ClientPi> findAllByNetworkId(@Param("networkId") Integer networkId);

}
