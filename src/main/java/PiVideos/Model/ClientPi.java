package PiVideos.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name ="clientPi")
public class ClientPi {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer _id;

    @Column(unique = true, nullable = false)
    private String name;

    private String location;


    @ManyToOne
    @JoinColumn(name = "network_id", nullable = false)
    private Network network;

    @OneToMany(mappedBy = "clientPi" , cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Video> videos;



    public ClientPi() {
    }

    public ClientPi(Integer _id, String name, Network network, List<Video> videos) {
        this._id = _id;
        this.name = name;
        this.network = network;
        this.videos = videos;
    }
}
