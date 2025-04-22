package PiVideos.Repository;

import PiVideos.Model.ClientPi;
import PiVideos.Model.Network;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
/*******************************************************************************************************
 Autor: Julian Hecht
 Datum letzte Änderung: 22.04.2025
 Änderung: Kommentare hinzugefügt
 *******************************************************************************************************/
public interface ClientPiRepository extends CrudRepository<ClientPi,Integer> {

    public ClientPi findByName(String name);

    @Query(value = "SELECT * FROM client_pi WHERE network_id = :networkId", nativeQuery = true)
    List<ClientPi> findAllByNetworkId(@Param("networkId") Integer networkId);

}
