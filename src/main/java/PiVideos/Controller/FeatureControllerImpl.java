package PiVideos.Controller;


import PiVideos.Model.Video;
import PiVideos.Service.FeatureService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/features")
public class FeatureControllerImpl implements FeatureController {

    @Autowired
    FeatureService featureService;

    public FeatureControllerImpl(FeatureService featureService) {
        this.featureService = featureService;
    }

    @Override
    @GetMapping("/config")
    public String getConfig(Model model) {

        return  "features/configuration.html";
    }



    @GetMapping("/live/{_id}")
    public String getLiveById(@PathVariable("_id") String _id, Model model) {
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
    
        model.addAttribute("allVideos", featureService.getAllVideos());
        return "features/live.html";
    }
    

    
    @GetMapping("/live")
    public String getLive(Model model, @RequestParam(value = "_id", required = false) String _id) {
    
        if (_id != null && !_id.isBlank()) {
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
                model.addAttribute("error", "Ungültige Eingabe: Bitte gib eine gültige VideoID ein.");
            }
        }
    
        model.addAttribute("allVideos", featureService.getAllVideos());
        return "features/live.html";
    }
    

   @PostMapping("/live/delete/{_id}")
    public String liveDeleteVideo(@PathVariable("_id") String _id, Model model) {

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

    model.addAttribute("allVideos", featureService.getAllVideos());
    return "features/live.html";
}


     @PostMapping("/live/update/{_id}")
    public String liveUpdateVideo(@PathVariable("_id") String _id, Model model, @RequestParam String comment, @RequestParam(required = false) Boolean favorite) {
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

    model.addAttribute("allVideos", featureService.getAllVideos());
    return "features/live.html";
    }






    @Override
    @GetMapping("/dataStorage")
    public String getDataStorage(Model model) {
        model.addAttribute("clients", featureService.getAllClientPis());
        model.addAttribute("videos", featureService.getAllVideos());
        return "features/datastorage.html";
    }

    @PostMapping("/dataStorage/delete/{_id}")
    public String dataStorageDeleteVideo(@PathVariable("_id") Integer _id, Model model) {

        if(featureService.deleteVideoByID(_id)){
            model.addAttribute("message", "Video mit der ID " + _id + " wurde gelöscht.");
        } else{
            model.addAttribute("error", "Video mit der ID " + _id + " konnte nicht gelöscht werden (nicht gefunden?).");
        }

        model.addAttribute("clients", featureService.getAllClientPis());
        model.addAttribute("videos", featureService.getAllVideos());
        return "features/datastorage.html";
    }

    @PostMapping("/dataStorage/update/{_id}")
    public String dataStorageUpdateVideo(@PathVariable("_id") Integer _id, Model model, @RequestParam String comment, @RequestParam(required = false) Boolean favorite) {
        Optional<Video> existingVideo = featureService.getVideoBy_id(_id);
        
        if(existingVideo.isPresent()){
        Video video = existingVideo.get();    
        video.setComment(comment);
        video.setFavorite(favorite != null && favorite);

        if(featureService.updateVideo(video)){
            model.addAttribute("message", "Video mit der ID " + _id + " wurde geändert.");
        } else{
            model.addAttribute("error", "Video mit der ID " + _id + " konnte nicht geändert werden (nicht gefunden?).");
        }

        }
        model.addAttribute("clients", featureService.getAllClientPis());
        model.addAttribute("videos", featureService.getAllVideos());
        return "features/datastorage.html";
    }

    @Override
    public String getLive(Integer id, Model model) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLive'");
    }

}
