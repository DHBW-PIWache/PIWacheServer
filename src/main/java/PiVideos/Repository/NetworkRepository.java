package PiVideos.Repository;

import PiVideos.Model.Network;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.repository.CrudRepository;

public interface NetworkRepository extends CrudRepository<Network, Integer> {

}
