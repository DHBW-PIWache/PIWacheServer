package PiVideos.Model;

import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name ="video")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer _id;

    private String name;

    private String path;


    //Hier sollten noch Meta daten eingebaut werden
    // datum, zeitstempel, videoLänge, Trigger(ob audio oder video trigger), usw.
//


    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private ClientPi clientPi;



    public Video(String name, String path,ClientPi clientPi) {

        this.name = name;
        this.path = path;
        this.clientPi = clientPi;
    }
}
