package PiVideos.Model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    private int port;



    @OneToMany(mappedBy = "network",cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<ClientPi> clientPis;

    public Network() {
    }

    public Network(Integer _id, String name, String rootPath, int port, List<ClientPi> clientPis) {
        this._id = _id;
        this.name = name;
        this.rootPath = rootPath;
        this.port = port;
        this.clientPis = clientPis;
    }

    
}
