package com.demo.service;

import com.demo.model.Game;
import com.demo.model.TotalSales;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GameService {
    void save(MultipartFile file);
    List<Game> getGameSales(String saleDateFrom, String saleDateTo, Double salePriceMin, Double salePriceMax, Integer page);

    List<TotalSales> getTotalSales(String saleDateFrom, String saleDateTo, Integer gameNo);

}
