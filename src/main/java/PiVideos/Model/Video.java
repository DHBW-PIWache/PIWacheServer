package PiVideos.Model;

import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

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

    private LocalDateTime date;

    private Timestamp timestamp;

    private long bytes;

    private boolean favorite;



    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private ClientPi clientPi;


    public Video() {
    }

    public Video(Integer _id, String name, String path, LocalDateTime date, Timestamp timestamp, long bytes, boolean favorite, ClientPi clientPi) {
        this._id = _id;
        this.name = name;
        this.path = path;
        this.date = date;
        this.timestamp = timestamp;
        this.bytes = bytes;
        this.favorite = favorite;
        this.clientPi = clientPi;
    }
}
