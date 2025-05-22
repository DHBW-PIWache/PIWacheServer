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

    private String relativePath;

    private LocalDateTime date;


    private double mb;

    private double duration;

    private boolean favorite;

    private String comment;



    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private ClientPi clientPi;


    public Video() {
    }

    public Video(Integer _id, String name, String path, String relativePath, LocalDateTime date, double mb, double duration, boolean favorite, String comment, ClientPi clientPi) {
        this._id = _id;
        this.name = name;
        this.path = path;
        this.relativePath = relativePath;
        this.date = date;
        this.mb = mb;
        this.duration = duration;
        this.favorite = favorite;
        this.comment = comment;
        this.clientPi = clientPi;
    }
}
