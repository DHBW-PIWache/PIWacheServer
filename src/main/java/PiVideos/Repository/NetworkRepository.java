package PiVideos.Repository;

import PiVideos.Model.Network;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/*******************************************************************************************************
 Autor: Julian Hecht
 Datum letzte Änderung: 22.04.2025
 Änderung: Kommentare hinzugefügt
 *******************************************************************************************************/
public interface NetworkRepository extends CrudRepository<Network, Integer> {

    @Query(value = "SELECT * FROM network", nativeQuery = true)
    public List<Network> getAllNetworks();



}
