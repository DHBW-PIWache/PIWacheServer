package PiVideos.Repository;

import PiVideos.Model.ClientPi;
import PiVideos.Model.Network;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ClientPiRepository extends CrudRepository<ClientPi, Integer> {

    public Optional<ClientPi> findByNameAndNetwork(String name, Network network);

    @Query(value = "SELECT * FROM client_pi WHERE network_id = :networkId", nativeQuery = true)
    List<ClientPi> findAllByNetworkId(@Param("networkId") Integer networkId);

    @Query(value = """
            SELECT COUNT(*)
            FROM video v
            JOIN client_pi c ON v.client_id = c._id
            JOIN network n ON c.network_id = n._id
            WHERE n._id = :networkId AND c._id = :clientId
            """, nativeQuery = true)
    Integer countAllVideosForClient(@Param("networkId") Integer networkId, @Param("clientId") Integer clientId);

    @Query(value = """
            SELECT v.date
            FROM video v
            JOIN client_pi c ON v.client_id = c._id
            JOIN network n ON c.network_id = n._id
            WHERE n._id = :networkId AND c._id = :clientId
            ORDER BY v.date DESC
            LIMIT 1
            """, nativeQuery = true)
    LocalDateTime getLatestVideo(@Param("networkId") Integer networkId, @Param("clientId") Integer clientId);
}
