package works.weave.socks.cart.controllers;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import works.weave.socks.cart.entities.Item;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author yansir
 * @date 2024/7/8
 * @apiNote
 */
@RestController
@RequestMapping("/common")
public class CommonController {

    private final Logger LOG = getLogger(getClass());

    /**
     * 下载指定路径的文件
     * @param filePath 文件路径
     * @return file
     */
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(String filePath) throws IOException {
        Resource resource = new UrlResource(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    @PostMapping("/upload")
    public @ResponseBody String fileUpload(@RequestParam("file") MultipartFile file) throws IOException {
            // 指定文件保存路径
            Path uploadPath = Paths.get("uploads");
            // 将文件保存到指定路径
            Files.copy(file.getInputStream(), uploadPath.resolve(file.getOriginalFilename()));
            return "上传成功！";
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/{id}")
    public Item getItemById(@PathVariable String id) {
        String sql = "SELECT * FROM items WHERE id = " + id;
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Item.class));
    }

}
