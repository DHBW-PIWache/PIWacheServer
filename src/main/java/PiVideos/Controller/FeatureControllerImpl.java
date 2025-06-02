package PiVideos.Controller;


import PiVideos.Model.ClientPi;
import PiVideos.Model.Network;
import PiVideos.Model.Video;
import PiVideos.Service.FeatureService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/features")
public class FeatureControllerImpl implements FeatureController {


    FeatureService featureService;

    public FeatureControllerImpl(FeatureService featureService) {
        this.featureService = featureService;
    }





    @GetMapping("/live/{_id}")
    public String getLiveById(@PathVariable("_id") String _id, Model model, HttpSession session) {

        Network network = (Network) session.getAttribute("network");
        try {
            Integer id = Integer.parseInt(_id);
            Optional<Video> existingVideo = featureService.getVideoBy_id(id);
    
            if (existingVideo.isPresent()) {
                Video video = existingVideo.get();
                model.addAttribute("video", video);
                model.addAttribute("videopath", "/videos/" + video.getRelativePath());
            } else {
                model.addAttribute("error", "Video mit der ID " + id + " wurde nicht gefunden.");
            }
        } catch (NumberFormatException e) {
            model.addAttribute("error", "Ungültige Video-ID im Pfad: " + _id);
        }
    
        model.addAttribute("allVideos", featureService.getAllVideosForNetwork(network));
        return "features/live.html";
    }
    

    
    @GetMapping("/live")
    public String getLive(Model model, HttpSession session,@RequestParam(value = "_id", required = false) String _id) {

        Network network = (Network) session.getAttribute("network");

        Video video = featureService.getNewestVideo(network);
        if(video != null){
            model.addAttribute("video", video);
            model.addAttribute("videopath", "/videos/" + video.getRelativePath());
        } else{
            model.addAttribute("error", "Noch kein Video vorhanden.");
        }
        
        if (_id != null && !_id.isBlank()) {
            try {
                Integer id = Integer.parseInt(_id);
                Optional<Video> existingVideo = featureService.getVideoBy_id(id);
    
                if (existingVideo.isPresent()) {
                    video = existingVideo.get();    
                    model.addAttribute("video", video);
                    model.addAttribute("videopath", "/videos/" + video.getRelativePath());
                    
                } else {
                    model.addAttribute("error", "Video mit der ID " + id + " wurde nicht gefunden.");
                }
            } catch (NumberFormatException e) {
                model.addAttribute("error", "Ungültige Eingabe: Bitte gib eine gültige VideoID ein.");
            }
        }

    
        model.addAttribute("allVideos", featureService.getAllVideosForNetwork(network));
        return "features/live.html";
    }
    

   @PostMapping("/live/delete/{_id}")
    public String liveDeleteVideo(@PathVariable("_id") String _id, Model model,HttpSession session) {
       Network network = (Network) session.getAttribute("network");

    try {
        Integer id = Integer.parseInt(_id);
        boolean deleted = featureService.deleteVideoByID(id);

        if (!deleted) {
            model.addAttribute("error", "Video mit der ID " + id + " konnte nicht gelöscht werden (nicht gefunden?).");
        } else{
            model.addAttribute("message", "Video mit der ID " + id + " wurde gelöscht.");
        }


    } catch (NumberFormatException e) {
        model.addAttribute("error", "Ungültige Video-ID im Pfad: " + _id);
    }

    model.addAttribute("allVideos", featureService.getAllVideosForNetwork(network));
    return "features/live.html";
}


     @PostMapping("/live/update/{_id}")
    public String liveUpdateVideo(@PathVariable("_id") String _id, Model model, @RequestParam String comment, @RequestParam(required = false) Boolean favorite, HttpSession session) {
         Network network = (Network) session.getAttribute("network");

        try {
        Integer id = Integer.parseInt(_id);
        Optional<Video> existingVideo = featureService.getVideoBy_id(id);

        if (existingVideo.isPresent()) {
            Video video = existingVideo.get();
            video.setComment(comment);
            video.setFavorite(favorite != null && favorite);

            featureService.updateVideo(video);

            model.addAttribute("video", video);
            model.addAttribute("videopath", "/videos/" + video.getRelativePath());
            model.addAttribute("message", "Video mit der ID " + _id + " wurde geändert.");
        } else {
            model.addAttribute("error", "Video mit der ID " + id + " wurde nicht gefunden.");
        }

    } catch (NumberFormatException e) {
        model.addAttribute("error", "Ungültige Video-ID im Pfad: " + _id);
    }

    model.addAttribute("allVideos", featureService.getAllVideosForNetwork(network));
    return "features/live.html";
    }






   @GetMapping("/dataStorage")
    public String getDataStorage(
        HttpSession session,
        Model model,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) Integer clientId,
        @RequestParam(required = false) String favorite,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate
) {
    Network network = (Network) session.getAttribute("network");
    if (network == null) {
        return "redirect:/login";
    }

    List<ClientPi> clients = featureService.getAllClientPis(network);
    List<Video> filteredVideos = featureService.getAllVideosForNetwork(network);

    if (clientId != null) {
        filteredVideos = filteredVideos.stream()
            .filter(video -> video.getClientPi() != null && clientId.equals(video.getClientPi().get_id()))
            .toList();
    }

    if (favorite != null && !favorite.isBlank()) {
        boolean favBool = Boolean.parseBoolean(favorite);
        filteredVideos = filteredVideos.stream()
            .filter(video -> video.isFavorite() == favBool)
            .toList();
    }

    // ✅ Datumsfilter
    if (fromDate != null) {
        filteredVideos = filteredVideos.stream()
            .filter(video -> video.getDate() != null && !video.getDate().isBefore(fromDate))
            .toList();
    }

    if (toDate != null) {
        filteredVideos = filteredVideos.stream()
            .filter(video -> video.getDate() != null && !video.getDate().isAfter(toDate))
            .toList();
    }

    int totalVideos = filteredVideos.size();
    int totalPages = (int) Math.ceil((double) totalVideos / size);
    page = Math.max(0, Math.min(page, totalPages - 1));

    int fromIndex = page * size;
    int toIndex = Math.min(fromIndex + size, totalVideos);
    List<Video> pagedVideos = filteredVideos.subList(fromIndex, toIndex);

    model.addAttribute("clients", clients);
    model.addAttribute("videos", pagedVideos);
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", totalPages);
    model.addAttribute("selectedClientId", clientId);
    model.addAttribute("favorite", favorite);
    model.addAttribute("fromDate", fromDate);
    model.addAttribute("toDate", toDate);

    return "features/datastorage.html";
}
    
    


    @PostMapping("/dataStorage/delete/{_id}")
    public String dataStorageDeleteVideo(@PathVariable("_id") Integer _id, RedirectAttributes model, HttpSession session) {
        Network network = (Network) session.getAttribute("network");

        if(featureService.deleteVideoByID(_id)){
            model.addFlashAttribute("message", "Video mit der ID " + _id + " wurde gelöscht.");
        } else{
            model.addFlashAttribute("error", "Video mit der ID " + _id + " konnte nicht gelöscht werden (nicht gefunden?).");
        }
        return "redirect:/features/dataStorage";
    }

    @PostMapping("/dataStorage/update/{_id}")
    public String dataStorageUpdateVideo(@PathVariable("_id") Integer _id, HttpSession session,RedirectAttributes model, @RequestParam String comment, @RequestParam(required = false) Boolean favorite) {
        Network network = (Network) session.getAttribute("network");
        Optional<Video> existingVideo = featureService.getVideoBy_id(_id);
        
        if(existingVideo.isPresent()){
        Video video = existingVideo.get();    
        video.setComment(comment);
        video.setFavorite(favorite != null && favorite);

        if(featureService.updateVideo(video)){
            model.addFlashAttribute("message", "Video mit der ID " + _id + " wurde geändert.");
        } else{
            model.addFlashAttribute("error", "Video mit der ID " + _id + " konnte nicht geändert werden (nicht gefunden?).");
        }

        }
        return "redirect:/features/dataStorage";
    }

    @PostMapping("/dataStorage/deleteMultiple")
    public String massDeleteVideos(@RequestParam("ids") String ids, RedirectAttributes model, HttpSession session) {

    String[] idArray = ids.split(",");
    int deletedCount = 0;

    for(String idStr : idArray) {
        try {
            Integer id = Integer.parseInt(idStr);
            if (featureService.deleteVideoByID(id)) {
                deletedCount++;
            }
        } catch (NumberFormatException ignored) {}
    }

    model.addFlashAttribute("message", deletedCount + " Videos wurden gelöscht.");

    return "redirect:/features/dataStorage";
    }


    @GetMapping("/livestream")
    public String getLiveStream(HttpSession session,Model model)    {
        Network network = (Network) session.getAttribute("network");
        List<ClientPi> clients = featureService.getAllClientPis(network);

        model.addAttribute("clients",clients);
        return "features/livestream.html";
    }

    @PostMapping("/livestream/{id}")
    public String postLiveStreanForClient(@PathVariable("id") Integer id, HttpSession session,RedirectAttributes model){
        ClientPi liveClient = featureService.getClientBy_id(id).get();
        RestTemplate restTemplate = new RestTemplate();
        boolean isActive = false;
        boolean error = false;

        try {
            String hostname = liveClient.getName();  // z.B. raspberrypi.local
            String url = "http://" + hostname + ":5000/status";
            Map<String, String> response = restTemplate.getForObject(url, Map.class);
            if (response != null && "running".equalsIgnoreCase(response.get("detection"))) {
                isActive = true;
            }
        } catch (Exception e) {
            model.addFlashAttribute("error","Client nicht erreichbar!");
            error =true;
            isActive = false;
        }

        if(isActive){
            model.addFlashAttribute("message","Bewegungserkennung für Client '" + liveClient.getName() +"' abgeschalten! Kann länger dauern!");
        }
        if(!isActive && !error){
            model.addFlashAttribute("message","Bewegungserkennung für Client " + liveClient.getName() +" war nicht an!");
        }

        return "redirect:/features/livestream/" + id;
    }

    @GetMapping("/livestream/{id}")
    public String getLiveStreamForClient(@PathVariable("id") Integer id, HttpSession session,Model model)    {


        Network network = (Network) session.getAttribute("network");
        List<ClientPi> clients = featureService.getAllClientPis(network);
        ClientPi liveClient = featureService.getClientBy_id(id).get();
        model.addAttribute("clients",clients);
        model.addAttribute("liveclient",liveClient);


        return "features/livestream.html";
    }
















}
