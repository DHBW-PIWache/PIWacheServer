package PiVideos.Repository;

import PiVideos.Model.Network;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
