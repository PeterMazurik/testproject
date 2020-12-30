package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ShipService {
    public List<Ship> findAll();

    public Ship findById(Long id);

    public boolean existById(Long id);

    public Ship save(Ship ship);

    public void deleteById(Long id);

    public ResponseEntity<Ship> getExistedShip(Long id);

    public ResponseEntity<Ship> deleteExistedShip(Long id);

    public ResponseEntity<Ship> editExistedShip(Ship shipValuesToEdit, Long id);

    public ResponseEntity<Ship> addShip(@RequestBody Ship ship);

    public List<Ship> getShips(String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed, Double minSpeed,
                               Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating);

    public List<Ship> filterShips(final List<Ship> ships, ShipOrder shipOrder, Integer pageNumber, Integer pageSize);
}
