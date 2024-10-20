package com.demo.controller;

import com.demo.model.Game;
import com.demo.model.TotalSales;
import com.demo.service.GameService;
import com.demo.util.CSVUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api")
public class GameController {
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    @Autowired
    GameService gameService;

    @RequestMapping(value="/getGameSales", method = RequestMethod.GET)
    public ResponseEntity<List<Game>> getGameSales(@RequestParam(value = "saleDateFrom", required = false) String saleDatefrom,
                                                   @RequestParam(value = "saleDateTo", required = false) String saleDateTo,
                                                   @RequestParam(value = "salePriceMin", required = false) Double salePriceMin,
                                                   @RequestParam(value = "salePriceMax", required = false) Double salePriceMax,
                                                   @RequestParam(value = "page") Integer page){

        List<Game> games = gameService.getGameSales(saleDatefrom,saleDateTo,salePriceMin, salePriceMax, page);
        if(games.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(games, HttpStatus.OK);
    }

    @RequestMapping(value="/upload", method = RequestMethod.POST)
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        String message;
        if (CSVUtil.hasCsvFormat(file)) {
            try{
                gameService.save(file);
                message = "File uploaded successfully: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(message);
            }catch(Exception e){
                logger.info(e.getMessage());
                message = "File not upload successfully: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
            }
        }
        message = "CSV File only!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    @RequestMapping(value="/getTotalSales", method = RequestMethod.GET)
    public ResponseEntity<List<TotalSales>> getTotalSales(@RequestParam(value = "saleDateFrom", required = false) String saleDatefrom,
                                                          @RequestParam(value = "saleDateTo", required = false) String saleDateTo,
                                                          @RequestParam(value = "gameNo", required = false) Integer gameNo){

        List<TotalSales> totalSales = gameService.getTotalSales(saleDatefrom, saleDateTo, gameNo);
        if(totalSales.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(totalSales, HttpStatus.OK);
    }




}
