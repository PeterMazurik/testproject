package com.space.service;

import com.space.Exceptions.BadRequestException;
import com.space.Exceptions.ShipNotFoundException;
import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

@Service
public class ShipServiceImpl implements ShipService {
    private final ShipRepository shipRepository;

    @Autowired
    public ShipServiceImpl(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    //Методы для работы с репозиторием
    @Override
    public List<Ship> findAll() {
        return shipRepository.findAll();
    }

    @Override
    public Ship findById(Long id) {
        return shipRepository.findById(id).get();
    }

    @Override
    public boolean existById(Long id) {
        return shipRepository.existsById(id);
    }

    @Override
    public Ship save(Ship ship) {
        return shipRepository.save(ship);
    }

    @Override
    public void deleteById(Long id) {
        shipRepository.deleteById(id);
    }


    //Основные методы
    @Override
    public ResponseEntity<Ship> getExistedShip(Long id) {
        checkExistence(id);
        return new ResponseEntity<>(findById(id), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Ship> deleteExistedShip(Long id) {
        checkExistence(id);
        deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Ship> editExistedShip(Ship editedShip, Long id) {
        checkExistence(id);

        if (editedShip == null)
            return new ResponseEntity<>(findById(id), HttpStatus.OK);

        Ship readyShip = shipEditor(findById(id), editedShip);

        save(readyShip);
        return new ResponseEntity<>(readyShip, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<Ship> addShip(@RequestBody Ship ship) {

        if (!checkParameters(ship)) {
            throw new BadRequestException("Неправильные параметры создания");
        }

        if (ship.isUsed() == null) {
            ship.setUsed(false);
        }

        ship.setSpeed(Math.round(ship.getSpeed() * 100.0) / 100.0);
        ship.setRating(getRating(ship));

        save(ship);
        return new ResponseEntity<>(ship, HttpStatus.OK);
    }


    //Вспомогательные методы

    //Проверка существует ли в базе такой корабль по id
    private void checkExistence(Long id) {
        if (id < 1) {
            throw new BadRequestException("wrong request");
        }
        if (!existById(id)) {
            throw new ShipNotFoundException("not found");
        }
    }

    //Редактирует существующий корабль
    private Ship shipEditor(Ship updatedShip, Ship editedShip) {
        if (editedShip.getName() != null) {
            if (editedShip.getName().length() >= 1 && editedShip.getName().length() < 50) {
                updatedShip.setName(editedShip.getName());
            } else {
                throw new BadRequestException("Неправильное имя");
            }
        }

        if (editedShip.getPlanet() != null) {
            if (editedShip.getPlanet().length() >= 1 && editedShip.getPlanet().length() < 50) {
                updatedShip.setPlanet(editedShip.getPlanet());
            } else {
                throw new BadRequestException("Неправильная планета");
            }
        }

        if (editedShip.getShipType() != null) {
            updatedShip.setShipType(editedShip.getShipType());
        }

        if (editedShip.getProdDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(editedShip.getProdDate());

            if (calendar.get(Calendar.YEAR) >= 2800 && calendar.get(Calendar.YEAR) <= 3019) {
                updatedShip.setProdDate(editedShip.getProdDate());
            } else {
                throw new BadRequestException("Неправильная дата");
            }
        }

        if (editedShip.isUsed() != null) {
            updatedShip.setUsed(editedShip.isUsed());
        }

        if (editedShip.getSpeed() != null) {
            if (editedShip.getSpeed() >= 0.1d && editedShip.getSpeed() <= 0.99d) {
                updatedShip.setSpeed(Math.round(editedShip.getSpeed() * 100.0) / 100.0);
            } else {
                throw new BadRequestException("Неправильная скорость");
            }
        }

        if (editedShip.getCrewSize() != null) {
            if (editedShip.getCrewSize() >= 1 && editedShip.getCrewSize() <= 9999) {
                updatedShip.setCrewSize(editedShip.getCrewSize());
            } else {
                throw new BadRequestException("Неправильное количество команды");
            }
        }

        updatedShip.setRating(getRating(updatedShip));

        return updatedShip;
    }

    //Рейтинг
    private double getRating(Ship ship) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ship.getProdDate());
        int year = calendar.get(Calendar.YEAR);

        double rating = (80 * ship.getSpeed() * (ship.isUsed() ? 0.5 : 1)) / (3019 - year + 1);

        return Math.round(rating * 100.0) / 100.0;
    }

    //Проверка параметров для создания
    private boolean checkParameters(final Ship ship) {
        if (ship.getProdDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(ship.getProdDate());
            int year = calendar.get(Calendar.YEAR);

            return ship.getName() != null && ship.getPlanet() != null && ship.getShipType() != null &&
                    ship.getProdDate() != null && ship.getSpeed() != null && ship.getCrewSize() != null &&
                    ship.getName().length() >= 1 && ship.getName().length() <= 50 &&
                    ship.getPlanet().length() >= 1 && ship.getPlanet().length() <= 50 &&
                    year >= 2800 && year <= 3019 &&
                    !(ship.getSpeed() < 0.01d) && !(ship.getSpeed() > 0.99d) &&
                    ship.getCrewSize() >= 1 && ship.getCrewSize() <= 9999;
        } else {
            return false;
        }
    }

    //Получение списка кораблей
    @Override
    public List<Ship> getShips(String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed, Double minSpeed,
                               Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating) {
        List<Ship> ships = findAll();
        List<Ship> filtered = new ArrayList<>(ships);

        if (planet != null) {
            for (Ship ship : ships) {
                if (!ship.getPlanet().contains(planet)) {
                    filtered.remove(ship);
                }
            }
            ships = new ArrayList<>(filtered);
        }

        if (shipType != null) {
            for (Ship ship : ships) {
                if (ship.getShipType() != shipType) {
                    filtered.remove(ship);
                }
            }
            ships = new ArrayList<>(filtered);
        }

        if (name != null) {
            for (Ship ship : ships) {
                if (!ship.getName().contains(name)) {
                    filtered.remove(ship);
                }
            }
            ships = new ArrayList<>(filtered);
        }

        if (isUsed != null) {
            for (Ship ship : ships) {
                if (ship.isUsed() != isUsed) {
                    filtered.remove(ship);
                }
            }
            ships = new ArrayList<>(filtered);
        }

        List<Ship> result = new ArrayList<>();

        for (Ship ship : ships) {
            if (ship.getProdDate().getTime() >= after && ship.getProdDate().getTime() <= before &&
                    ship.getSpeed() >= minSpeed && ship.getSpeed() <= maxSpeed &&
                    ship.getCrewSize() >= minCrewSize && ship.getCrewSize() <= maxCrewSize &&
                    ship.getRating() > minRating && ship.getRating() < maxRating
            ) {
                result.add(ship);
            }
        }

        return result;
    }

    //Фильтр по параметрам
    @Override
    public List<Ship> filterShips(final List<Ship> ships, ShipOrder shipOrder, Integer pageNumber, Integer pageSize) {
        Comparator<Ship> comparator = new Comparator<Ship>() {
            @Override
            public int compare(Ship o1, Ship o2) {
                return (int) (o1.getId() - o2.getId());
            }
        };

        if (shipOrder == ShipOrder.ID) {
            comparator = new Comparator<Ship>() {
                @Override
                public int compare(Ship o1, Ship o2) {
                    return (int) (o1.getId() - o2.getId());
                }
            };
        }

        if (shipOrder == ShipOrder.SPEED) {
            comparator = new Comparator<Ship>() {
                @Override
                public int compare(Ship o1, Ship o2) {
                    if (o1.getSpeed() < o2.getSpeed()) return -1;
                    if (o1.getSpeed() > o2.getSpeed()) return 1;
                    return 0;
                }
            };
        }

        if (shipOrder == ShipOrder.DATE) {
            comparator = new Comparator<Ship>() {
                @Override
                public int compare(Ship o1, Ship o2) {
                    if (o1.getProdDate().getTime() < o2.getProdDate().getTime()) return -1;
                    if (o1.getProdDate().getTime() > o2.getProdDate().getTime()) return 1;
                    return 0;
                }
            };
        }

        if (shipOrder == ShipOrder.RATING) {
            comparator = new Comparator<Ship>() {
                @Override
                public int compare(Ship o1, Ship o2) {
                    if (o1.getRating() < o2.getRating()) return -1;
                    if (o1.getRating() > o2.getRating()) return 1;
                    return 0;
                }
            };
        }

        ships.sort(comparator);

        List<Ship> result = new ArrayList<>(ships.subList(pageNumber * pageSize, ships.size()));

        return result.size() <= pageSize ? result.subList(0, result.size()) : result.subList(0, pageSize);
    }
}
