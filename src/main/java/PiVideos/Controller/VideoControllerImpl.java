package PiVideos.Controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import PiVideos.Model.Network;
import jakarta.servlet.http.HttpSession;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.MalformedURLException;

@RestController
@RequestMapping("/videos")
public class VideoControllerImpl {

    // Wird gebraucht, um Videos anzuzeigen
    // Diese Klasse dient dazu, Videos von einem bestimmten Pfad zu servieren.
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveVideo(@PathVariable String filename,
            @RequestParam(required = false) String fullPath, HttpSession session) {
        Network network = (Network) session.getAttribute("network");
        try {
            Path filePath;

            if (fullPath != null && !fullPath.isBlank()) {
                filePath = Paths.get(fullPath);
            } else {

                filePath = Paths.get(network.getRootPath()).resolve(filename);
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}