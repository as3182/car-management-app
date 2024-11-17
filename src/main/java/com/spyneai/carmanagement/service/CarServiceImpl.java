package com.spyneai.carmanagement.service;

import com.spyneai.carmanagement.dto.CarDTO;
import com.spyneai.carmanagement.entity.Car;
import com.spyneai.carmanagement.entity.User;
import com.spyneai.carmanagement.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
@Service
public class CarServiceImpl implements CarService {

    @Autowired
    private CarRepository carRepository;



    @Override
    public void addCarWithImages(CarDTO carDTO, List<MultipartFile> images, User user) {
        Car car = new Car();
        car.setTitle(carDTO.getTitle());
        car.setDescription(carDTO.getDescription());
        car.setTags(carDTO.getTags());

        // Convert MultipartFile to byte[]
        List<byte[]> imageBytes = images.stream().map(image -> {
            try {
                return image.getBytes();
            } catch (IOException e) {
                throw new RuntimeException("Error reading image file", e);
            }
        }).toList();
        car.setImages(imageBytes);

        car.setOwner(user);
        carRepository.save(car);
    }

    @Override
    public List<CarDTO> getCarsByUser(User user) {

        List<Car> carList = carRepository.findAllByOwner(user);

        return carList.stream().map(car->{
            CarDTO carDTO = new CarDTO();
            carDTO.setId(car.getId());
            carDTO.setTitle(car.getTitle());
            carDTO.setDescription(car.getDescription());
            carDTO.setTags(car.getTags());

            List<String> based64Images = car.getImages().stream()
                    .map(image->Base64.getEncoder().encodeToString(image))
                    .toList();
            carDTO.setImages(based64Images);

            return carDTO;
        }).toList();

    }

    @Override
    public Page<CarDTO> getAllCars(Pageable pageable) {
        return carRepository.findAll(pageable).map(car -> {
            CarDTO dto = new CarDTO();
            dto.setId(car.getId());
            dto.setTitle(car.getTitle());
            dto.setDescription(car.getDescription());
            dto.setTags(car.getTags());
            dto.setImages(car.getImages().stream()
                    .limit(1)
                    .map(image -> Base64.getEncoder().encodeToString(image))
                    .toList());
            return dto;
        });
    }



    @Override
    public Car viewCar(Long id) {
        return carRepository.findById(id).orElseThrow(()->new RuntimeException("couldn't find the car"));
    }

    public void updateCar(Long id, String title, String description, String tags, List<MultipartFile> images) throws IOException {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found"));

        if (title != null && !title.isBlank()) {
            car.setTitle(title);
        }
        if (description != null && !description.isBlank()) {
            car.setDescription(description);
        }
        if (tags != null && !tags.isBlank()) {
            // Split the comma-separated string into a list
            List<String> tagList = Arrays.asList(tags.split("\\s*,\\s*")); // Split by commas, ignoring spaces
            car.setTags(tagList);
        }

        if (images != null && !images.isEmpty()) {
            List<byte[]> imageBytes = new ArrayList<>();
            for (MultipartFile image : images) {
                imageBytes.add(image.getBytes());
            }
            car.setImages(imageBytes);
        }

        carRepository.save(car); // Save the updated car
    }

    @Override
    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }

    public List<CarDTO> searchCarsByTag(String tag) {
        List<Car> cars = carRepository.findByTag(tag);
        return cars.stream().map(car -> {
            CarDTO dto = new CarDTO();
            dto.setId(car.getId());
            dto.setTitle(car.getTitle());
            dto.setDescription(car.getDescription());
            dto.setTags(car.getTags());
            dto.setImages(car.getImages().stream()
                    .map(image -> Base64.getEncoder().encodeToString(image))
                    .toList());
            return dto;
        }).toList();
    }

}
