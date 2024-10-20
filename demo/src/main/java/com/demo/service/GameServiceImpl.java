package com.demo.service;

import com.demo.dao.GameRepository;
import com.demo.model.Game;
import com.demo.model.TotalSales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private GameRepository gameRepository;

    @Override
    public void save(MultipartFile file) {
        try {
            //Load data infile strategy
            gameRepository.bulkLoadFromInputStream("Load data local infile 'file.csv' into table game_sales columns terminated by ','", file.getInputStream());//

//            //Batch Insert Strategy
//            List<Game> games = CSVUtil.csvToList(file.getInputStream());
//            gameRepository.batchInsert(games);
        }catch (IOException ex){
            throw new RuntimeException("Data is not store successfully: " + ex.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Game> getGameSales(String saleDateFrom, String saleDateTo, Double salePriceMin, Double salePriceMax, Integer page) {
        return gameRepository.getGameSales(saleDateFrom, saleDateTo, salePriceMin, salePriceMax, page);
    }

    @Override
    public List<TotalSales> getTotalSales(String saleDateFrom, String saleDateTo, Integer gameNo) {
        return gameRepository.getTotalSales(saleDateFrom, saleDateTo, gameNo);
    }


}
