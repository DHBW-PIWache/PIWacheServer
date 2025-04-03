package PiVideos.Model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.net.InetAddress;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "network")
public class Network {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer _id;

    private String name;

    private String rootPath;

    //Maybe noch IP adresse direkt im netzwerk, je nach dem ob es möglich ist
//    private InetAddress inetAddress;

    @OneToMany(mappedBy = "network", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<ClientPi> clientPis;



    public Network(Integer _id, String name, String rootPath, List<ClientPi> clientPis) {
        this._id = _id;
        this.name = name;
        this.rootPath = rootPath;
        this.clientPis = clientPis;
    }

    public Network() {
    }
}
