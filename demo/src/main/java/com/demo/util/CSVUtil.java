package com.demo.util;

import com.demo.model.Game;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CSVUtil {
    public static String TYPE = "text/csv";
    public static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static boolean hasCsvFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public static List<Game> csvToList(InputStream is) {
        try (BufferedReader bReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(bReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim())) {
            List<Game> gameList = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            System.out.println(csvParser.getRecords().size());
            for (CSVRecord csvRecord : csvRecords) {
                Game game = new Game();
                game.setId(Integer.parseInt(csvRecord.get("ID")));
                game.setGame_no(Integer.parseInt(csvRecord.get("Game No.")));
                game.setGame_name(csvRecord.get("Game Name"));
                game.setGame_code(csvRecord.get("Game Code"));
                game.setType(Integer.parseInt(csvRecord.get("Type")));
                game.setCost_price(Double.parseDouble(csvRecord.get("Cost Price")));
                game.setTax(Double.parseDouble(csvRecord.get("Tax")));
                game.setSale_price(Double.parseDouble(csvRecord.get("Sale Price")));
                game.setDate_of_sale(df.parse(csvRecord.get("Date Of Sale")));
                gameList.add(game);
            }
            return gameList;
        } catch (IOException | ParseException e) {
            throw new RuntimeException("CSV data is failed to parse: " + e.getMessage());
        }
    }
}
