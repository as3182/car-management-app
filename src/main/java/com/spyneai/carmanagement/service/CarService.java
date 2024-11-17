package com.spyneai.carmanagement.service;

import com.spyneai.carmanagement.dto.CarDTO;
import com.spyneai.carmanagement.dto.CarUpdateDTO;
import com.spyneai.carmanagement.entity.Car;
import com.spyneai.carmanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface CarService {



    public void addCarWithImages(CarDTO carDTO, List<MultipartFile> images, User user);

    public List<CarDTO> getCarsByUser(User user);

    public Page<CarDTO> getAllCars(Pageable pageable);

    public Car viewCar(Long id);

    public void updateCar(Long id,String title, String description, String tags, List<MultipartFile> images) throws IOException;

    public void deleteCar(Long id);

    public List<CarDTO> searchCarsByTag(String tag);
}
