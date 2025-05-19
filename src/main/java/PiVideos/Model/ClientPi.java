package PiVideos.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Table(
        name = "client_pi",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "network_id"})
        }
)
public class ClientPi {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer _id;

    
    private String name;

    private String location;

    private String comment;

    private LocalDateTime date;


    @ManyToOne
    @JoinColumn(name = "network_id", nullable = false)
    private Network network;


    @OneToMany(mappedBy = "clientPi", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Video> videos;




    public ClientPi() {
    }

    public ClientPi(Integer _id, String name, String location, String comment, Network network, List<Video> videos) {
        this._id = _id;
        this.name = name;
        this.location = location;
        this.comment = comment;
        this.network = network;
        this.videos = videos;
    }
}
