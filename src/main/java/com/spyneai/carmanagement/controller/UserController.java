package com.spyneai.carmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spyneai.carmanagement.dto.*;
import com.spyneai.carmanagement.entity.Car;
import com.spyneai.carmanagement.entity.User;
import com.spyneai.carmanagement.repository.CarRepository;
import com.spyneai.carmanagement.service.AuthService;
import com.spyneai.carmanagement.service.CarService;
import jakarta.validation.Valid;
import jakarta.xml.bind.SchemaOutputResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private CarService carService;

    @Autowired
    private AuthService authService;

    @Autowired
    private CarRepository carRepository;

    @PostMapping(value = "/addcar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addCar(
            @RequestPart("carDetails") @Valid String carDetailsJson,
            @RequestPart("images") List<MultipartFile> images,
            @AuthenticationPrincipal User user) {
        try {
            // Parse JSON string to CarDTO
            ObjectMapper objectMapper = new ObjectMapper();
            CarDTO carDTO = objectMapper.readValue(carDetailsJson, CarDTO.class);

            // Process the request
            carService.addCarWithImages(carDTO, images, user);

            return ResponseEntity.status(HttpStatus.CREATED).body("Car added successfully!");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
        }
    }

    @GetMapping("/cars")
    public ResponseEntity<?> getUserCars(@AuthenticationPrincipal User user)
    {
        try
        {
            List<CarDTO> userCars = carService.getCarsByUser(user);
            return ResponseEntity.ok(userCars);
        }
        catch(Exception ex)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occured: "+ex.getMessage());
        }
    }

    @GetMapping("/allcars")
    public ResponseEntity<?> getAllCars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<CarDTO> allCars = carService.getAllCars(pageable);
            return ResponseEntity.ok(allCars);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
        }
    }


    @GetMapping("/viewcar")
    public ResponseEntity<?> viewSpecificCar(@RequestParam Long id) {
        try {
            // Fetch the car details using the id from the query parameter
            Car car = carService.viewCar(id);

            // Convert Car to CarDTO
            CarDTO carDTO = new CarDTO();
            carDTO.setTitle(car.getTitle());
            carDTO.setDescription(car.getDescription());
            carDTO.setTags(car.getTags());

            // Convert images to Base64 strings
            List<String> base64Images = car.getImages().stream()
                    .map(image -> Base64.getEncoder().encodeToString(image))
                    .collect(Collectors.toList());
            carDTO.setImages(base64Images);

            return ResponseEntity.ok(carDTO);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + ex.getMessage());
        }
    }




    @PutMapping(value = "updatecar", consumes = "multipart/form-data")
    public ResponseEntity<?> updateCar(
            @RequestParam Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String tags,
            @RequestPart(required = false) List<MultipartFile> images,
            @AuthenticationPrincipal User user) {
        try {
            Car car = carRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Car not found"));

            if (!car.getOwner().getUserID().equals(user.getUserID())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this car.");
            }

            carService.updateCar(id, title, description, tags, images);

            return ResponseEntity.ok("Car updated successfully!");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
        }
    }


    @DeleteMapping("/cars/delete")
    public ResponseEntity<?> deleteCar(@RequestParam Long carId, @AuthenticationPrincipal User user)
    {
        try {
            Car car = carRepository.findById(carId)
                    .orElseThrow(() -> new RuntimeException("Car not found"));

            if (!car.getOwner().getUserID().equals(user.getUserID())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this car.");
            }

            carService.deleteCar(carId);
            return ResponseEntity.ok("Car Deleted Successfully");
        }
        catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
        }
    }

    @GetMapping("/details")
    public ResponseEntity<?> userDetails(@AuthenticationPrincipal User user)
    {
        Long userId = user.getUserID();
        UserDTO userDTO = authService.userDetails(userId);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("hi")
    public String hi()
    {
        return "hi";
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchCars(@RequestParam String tag) {
        try {
            List<CarDTO> cars = carService.searchCarsByTag(tag);
            return ResponseEntity.ok(cars);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while searching: " + ex.getMessage());
        }
    }




}
