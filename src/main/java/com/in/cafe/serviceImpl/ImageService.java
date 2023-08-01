package com.in.cafe.serviceImpl;

import com.in.cafe.POJO.ImageData;
import com.in.cafe.dao.ImageDao;
import com.in.cafe.utils.ImageUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class ImageService {

    @Autowired
    private ImageDao imageDao;

    public String uploadImage(MultipartFile file) throws IOException {


        imageDao.save(ImageData.builder()
                .imageData(ImageUtils.compressImage(file.getBytes())).build());

        return "Image uploaded successfully: " +
                file.getOriginalFilename();

    }

    @Transactional
    public byte[] getImage(Integer id) {
        Optional<ImageData> dbImage = imageDao.findByid(id);
        byte[] image = ImageUtils.decompressImage(dbImage.get().getImageData());
        return image;
    }
}
