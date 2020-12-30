package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ShipController {
    private final ShipServiceImpl shipServiceImpl;

    @Autowired
    public ShipController(ShipServiceImpl shipServiceImpl) {
        this.shipServiceImpl = shipServiceImpl;
    }

    //GET by ID
    @GetMapping(value = "/rest/ships/{id}")
    public ResponseEntity<Ship> getShip(@PathVariable(value = "id") Long id) {
        return shipServiceImpl.getExistedShip(id);
    }

    //GET Ships count
    @GetMapping(value = "/rest/ships/count")
    public ResponseEntity<Integer> shipsCount(@RequestParam(value = "name", required = false) String name,
                                              @RequestParam(value = "planet", required = false) String planet,
                                              @RequestParam(value = "shipType", required = false) ShipType shipType,
                                              @RequestParam(value = "after", defaultValue = "0", required = false) Long after,
                                              @RequestParam(value = "before", defaultValue = "9223372036854775807", required = false) Long before,
                                              @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                              @RequestParam(value = "minSpeed", defaultValue = "0.01", required = false) Double minSpeed,
                                              @RequestParam(value = "maxSpeed", defaultValue = "0.99", required = false) Double maxSpeed,
                                              @RequestParam(value = "minCrewSize", defaultValue = "1", required = false) Integer minCrewSize,
                                              @RequestParam(value = "maxCrewSize", defaultValue = "9999", required = false) Integer maxCrewSize,
                                              @RequestParam(value = "minRating", defaultValue = "0.0", required = false) Double minRating,
                                              @RequestParam(value = "maxRating", defaultValue = "100.0", required = false) Double maxRating
    ) {
        List<Ship> ships = shipServiceImpl.getShips(name, planet, shipType, after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
        return new ResponseEntity<>(ships.size(), HttpStatus.OK);
    }

    //GET all
    @GetMapping("/rest/ships")
    public ResponseEntity<List<Ship>> getAllShips(@RequestParam(value = "name", required = false) String name,
                                                  @RequestParam(value = "planet", required = false) String planet,
                                                  @RequestParam(value = "shipType", required = false) ShipType shipType,
                                                  @RequestParam(value = "after", defaultValue = "0", required = false) Long after,
                                                  @RequestParam(value = "before", defaultValue = "9223372036854775807", required = false) Long before,
                                                  @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                                  @RequestParam(value = "minSpeed", defaultValue = "0.01", required = false) Double minSpeed,
                                                  @RequestParam(value = "maxSpeed", defaultValue = "0.99", required = false) Double maxSpeed,
                                                  @RequestParam(value = "minCrewSize", defaultValue = "1", required = false) Integer minCrewSize,
                                                  @RequestParam(value = "maxCrewSize", defaultValue = "9999", required = false) Integer maxCrewSize,
                                                  @RequestParam(value = "minRating", defaultValue = "0.0", required = false) Double minRating,
                                                  @RequestParam(value = "maxRating", defaultValue = "100.0", required = false) Double maxRating,
                                                  @RequestParam(value = "order", required = false) ShipOrder shipOrder,
                                                  @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                                                  @RequestParam(value = "pageSize", defaultValue = "3", required = false) Integer pageSize
    ) {
        List<Ship> ships = shipServiceImpl.getShips(name, planet, shipType, after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);

        return new ResponseEntity<>(shipServiceImpl.filterShips(ships, shipOrder, pageNumber, pageSize), HttpStatus.OK);
    }

    //ADD ship
    @PostMapping(value = "/rest/ships")
    public ResponseEntity<Ship> addShip(@RequestBody Ship ship) {
        return shipServiceImpl.addShip(ship);

    }

    //EDIT ship
    @PostMapping(value = "/rest/ships/{id}")
    public ResponseEntity<Ship> editShip(@RequestBody Ship shipValuesToEdit, @PathVariable(value = "id") Long id) {
        return shipServiceImpl.editExistedShip(shipValuesToEdit, id);
    }

    //DELETE ship
    @DeleteMapping("/rest/ships/{id}")
    public ResponseEntity<Ship> deleteShip(@PathVariable(value = "id") Long id) {
        return shipServiceImpl.deleteExistedShip(id);
    }
}
