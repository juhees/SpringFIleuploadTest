package kr.kwangan2.fileupload.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.print.attribute.standard.MediaTray;
import javax.xml.crypto.Data;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import kr.kwangan2.fileupload.domain.AttachFileDTO;
import lombok.extern.log4j.Log4j;
import net.coobird.thumbnailator.Thumbnailator;

@Controller
@Log4j
public class UploadController {

   @GetMapping("/uploadAjax")
   public void uploadAjax() {

   }

   @PostMapping(
         value="/uploadAjaxAction",
         produces=MediaType.APPLICATION_JSON_UTF8_VALUE
         )
   
   @ResponseBody
   public ResponseEntity<List<AttachFileDTO>>
   uploadAjaxPost(MultipartFile[] uploadFile) {

      List<AttachFileDTO> list= new ArrayList<AttachFileDTO>();
      
      String uploadFolder = "C:/upload";

      String uploadFolderPath=getFolder();
      
      File uploadPath = new File(uploadFolder, uploadFolderPath);

      if (uploadPath.exists() == false) {
         uploadPath.mkdirs();
      }

      for (MultipartFile multipartFile : uploadFile) {
         
         AttachFileDTO attachDTO= new AttachFileDTO();
         

         log.info("-----------------------------------");

         String originalFileName = multipartFile.getOriginalFilename();
         attachDTO.setFileName(originalFileName);
         log.info("upload file name:" + originalFileName);
         log.info("upload file name:" + multipartFile.getSize());
         log.info("upload file name:" + multipartFile.isEmpty());
         log.info("upload file name:" + multipartFile.getName());
         try {
            log.info("upload file name:" + multipartFile.getBytes());

            log.info("upload file name:" + multipartFile.getInputStream());
         } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
         }
         log.info("-----------------------------------");

         originalFileName.substring(originalFileName.lastIndexOf("\\") + 1);

         UUID uuid = UUID.randomUUID();

         originalFileName=uuid.toString() + "_" + originalFileName;
         

         try {
            File saveFile = new File(uploadPath,originalFileName); // 유니크한 이름
            // File saveFile = new File(uploadPath, originalFileName); //원래 파일이름 ㅅㄱ ㅋ
            multipartFile.transferTo(saveFile);
            
            attachDTO.setUuid(uuid.toString());
            attachDTO.setUploadPath(uploadFolderPath);
            
            
            if(checkImageType(saveFile)) {
               
               attachDTO.setImage(true);
               
               FileOutputStream thumbnail
                  =new FileOutputStream(
                           new File(uploadPath,"thumb_"+originalFileName)
                        );
               Thumbnailator.
               createThumbnail(multipartFile.getInputStream(),thumbnail,100,100);
               
               thumbnail.close();
               
            }
            
            list.add(attachDTO);
            
         } catch (Exception e) {
            e.printStackTrace();
         }

         // String uploadFileName=multipartFile.getOriginalFilename();

      }//for
      
      return new ResponseEntity(list,HttpStatus.OK   );
      
   }

   private String getFolder() {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      Date date = new Date();
      String str = sdf.format(date);
      return str.replace("-", File.separator);

   }

   private boolean checkImageType(File file) {
      try {
         String contentType = Files.probeContentType(file.toPath());
         return contentType.startsWith("image");
      } catch (IOException e) {
         e.printStackTrace();
      }
      return false;
   }

}