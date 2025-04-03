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

    private String location;

    private String path;

    @ManyToOne
    @JoinColumn(name = "network_id", nullable = false)
    private Network network;

    @OneToMany(mappedBy = "clientPi" , cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Video> videos;



    public ClientPi(String location, String path, Integer _id) {
        this.location = location;
        this.path = path;
        this._id = _id;

    }

    public ClientPi() {
    }
}
