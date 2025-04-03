package PiVideos.Repository;

import PiVideos.Model.Video;
import org.springframework.data.repository.CrudRepository;

public interface VideoRepository extends CrudRepository<Video,Integer> {

}
