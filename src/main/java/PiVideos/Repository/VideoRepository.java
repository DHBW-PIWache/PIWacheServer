package PiVideos.Repository;

import PiVideos.Model.ClientPi;
import PiVideos.Model.Video;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface VideoRepository extends CrudRepository<Video,Integer> {

    @Query(value = "SELECT * FROM video", nativeQuery = true)
    public List<Video> getAllVideos();

    public Video findByName(String name);


}
