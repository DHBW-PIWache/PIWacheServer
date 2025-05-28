package PiVideos.Repository;

import PiVideos.Model.ClientPi;
import PiVideos.Model.Video;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/*******************************************************************************************************
 Autor: Julian Hecht
 Datum letzte Änderung: 23.04.2025
 Änderung: findAllVideos angepasst
 *******************************************************************************************************/
public interface VideoRepository extends CrudRepository<Video,Integer> {

    @Query(value = "SELECT * FROM video", nativeQuery = true)
    public List<Video> getAllVideos();

    @Query(value = """
    SELECT v.*
    FROM video v
    JOIN client_pi c ON v.client_id = c._id
    JOIN network n ON c.network_id = n._id
    WHERE n._id = :network_id
    ORDER BY v.date DESC
    """, nativeQuery = true)
    List<Video> findAllVideosByNetzwerkId(@Param("network_id") Integer network_id);

    @Query(value = """
    SELECT v.*
    FROM video v
    JOIN client_pi c ON v.client_id = c._id
    JOIN network n ON c.network_id = n._id
    WHERE n._id = :network_id
    ORDER BY v.date DESC
    LIMIT 1
    """, nativeQuery = true)
    public Video findNewestVideo(@Param("network_id") Integer network_id);

    @Query(value = """
    SELECT COUNT(*)
    FROM video v
    JOIN client_pi c ON v.client_id = c._id
    JOIN network n ON c.network_id = n._id
    WHERE n._id = :network_id
    ORDER BY v.date DESC
    """, nativeQuery = true)
    public int countAllVids(@Param("network_id") Integer network_id);

    public Video findByName(String name);


}
