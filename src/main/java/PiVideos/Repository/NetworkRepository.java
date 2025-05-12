package PiVideos.Repository;

import PiVideos.Model.ClientPi;
import PiVideos.Model.Network;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/*******************************************************************************************************
 Autor: Julian Hecht
 Datum letzte Änderung: 22.04.2025
 Änderung: Kommentare hinzugefügt
 *******************************************************************************************************/
public interface NetworkRepository extends CrudRepository<Network, Integer> {

    @Query(value = "SELECT * FROM network", nativeQuery = true)
    public List<Network> getAllNetworks();

    @Query(value = """
    SELECT SUM(v.mb)
    FROM video v
    JOIN client_pi c ON v.client_id = c._id
    JOIN network n ON c.network_id = n._id
    WHERE n._id = :networkId
    """, nativeQuery = true)
    public Double totalMBForNetwork(@Param("networkId") Integer networkId);



}
