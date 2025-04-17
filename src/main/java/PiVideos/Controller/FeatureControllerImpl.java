package PiVideos.Controller;


import PiVideos.Model.Video;
import PiVideos.Service.FeatureService;
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

    @Override
    @GetMapping("/live")
    public String getLive(Model model) {
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
    public String deleteVideo(@PathVariable("_id") Integer id, Model model) {
        featureService.deleteVideoByID(id);
        model.addAttribute("clients", featureService.getAllClientPis());
        model.addAttribute("videos", featureService.getAllVideos());
        return "features/datastorage.html";
    }
    @PostMapping("/dataStorage/update/{_id}")
    public String updateVideo(@PathVariable("_id") Integer id, Model model, @RequestParam String comment, @RequestParam(required = false) Boolean favorite) {
        Video existingVideo = featureService.getVideoBy_id(id);
        existingVideo.setComment(comment);
        existingVideo.setFavorite(favorite != null && favorite);

        featureService.updateVideo(existingVideo);

        model.addAttribute("clients", featureService.getAllClientPis());
        model.addAttribute("videos", featureService.getAllVideos());
        return "features/datastorage.html";
    }

}
