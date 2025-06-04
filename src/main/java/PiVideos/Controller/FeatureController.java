package PiVideos.Controller;

import PiVideos.Model.ClientPi;
import PiVideos.Model.Network;
import PiVideos.Model.Video;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

public interface FeatureController {

    // Video Live-Seite mit Video-ID (Pfadparameter)
    String getLiveById(String _id, Model model, HttpSession session);

    // Video Live-Seite ohne ID (optional als RequestParam)
    String getLive(Model model, HttpSession session, String _id);

    // Video löschen über Pfad-ID
    String liveDeleteVideo(String _id, Model model, HttpSession session);

    // Video aktualisieren über Pfad-ID
    String liveUpdateVideo(String _id, Model model, String comment, Boolean favorite, HttpSession session);

    // Datenspeicher mit Filter und Paging
    String getDataStorage(HttpSession session,
                          Model model,
                          int page,
                          int size,
                          Integer clientId,
                          String favorite,
                          LocalDateTime fromDate,
                          LocalDateTime toDate);

    // Video löschen über ID im Datenspeicher
    String dataStorageDeleteVideo(Integer _id, RedirectAttributes model);

    // Video aktualisieren im Datenspeicher
    String dataStorageUpdateVideo(Integer _id, RedirectAttributes model, String comment, Boolean favorite);

    // Mehrere Videos löschen im Datenspeicher
    String massDeleteVideos(String ids, RedirectAttributes model, HttpSession session);

    // Livestream Seite anzeigen
    String getLiveStream(HttpSession session, Model model);

    // Livestream-Status ändern (POST)
    String postLiveStreanForClient(Integer id, HttpSession session, RedirectAttributes model);

    // Livestream-Seite für bestimmten Client
    String getLiveStreamForClient(Integer id, HttpSession session, Model model);

}
