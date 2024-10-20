package com.demo.dao;

import com.demo.model.Game;
import com.demo.model.TotalSales;
import com.mysql.cj.jdbc.StatementImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Repository
public class GameRepository {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Logger logger = LoggerFactory.getLogger(GameRepository.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GameRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Game> getGameSales(String saleDateFrom, String saleDateTo, Double salePriceMin, Double salePriceMax, Integer page){
        NamedParameterJdbcTemplate paramJdbcTemplate = new NamedParameterJdbcTemplate(Objects.requireNonNull(jdbcTemplate.getDataSource()));
        List<Game> games = null;
        StringJoiner where = new StringJoiner(" AND ", " WHERE ", "").setEmptyValue("");
        if(saleDateFrom != null)
            where.add("date_of_sale >= :saleDateFrom");
        if(saleDateTo != null)
            where.add("date_of_sale <= :saleDateTo");
        if(salePriceMin != null)
            where.add("sale_price >= :salePriceMin");
        if(salePriceMax != null)
            where.add("sale_price <= :salePriceMax");

        String sql = "SELECT * FROM game_sales" + where + " order by id limit 100";

        if(page != null){
            sql = sql + " offset " + (page - 1) * 100;
        }

        Map<String, Object> params = new HashMap<>();
        if(saleDateFrom != null){
            LocalDateTime dateTime = LocalDateTime.parse(saleDateFrom, formatter);
            params.put("saleDateFrom", dateTime);
        }else {
            params.put("saleDateFrom", null);
        }
        if(saleDateTo != null){
            LocalDateTime dateTime = LocalDateTime.parse(saleDateTo, formatter);
            params.put("saleDateTo", dateTime);
        }else {
            params.put("saleDateTo", null);
        }
        params.put("salePriceMin", salePriceMin);
        params.put("salePriceMax", salePriceMax);

        logger.info("SQL : " + sql);

        try {
            games = paramJdbcTemplate.query(sql, params, new GameMapper());
        }catch(Exception e){
            logger.error(e.getMessage(), e);
        }
        return games;
    }

    public void bulkLoadFromInputStream(String loadDataSql, InputStream dataStream) throws SQLException {
        if (dataStream== null ){
            logger.info("InputStream is null ,No data is imported" );
        }
        Connection conn = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
        PreparedStatement statement = conn.prepareStatement(loadDataSql);

        if (statement.isWrapperFor(StatementImpl.class )) {
            StatementImpl mysqlStatement = statement.unwrap(StatementImpl.class);
            mysqlStatement.setLocalInfileInputStream(dataStream);
            mysqlStatement.execute(loadDataSql);
        }
    }

    public List<TotalSales> getTotalSales(String saleDateFrom, String saleDateTo, Integer gameNo){
        List<TotalSales> totalSales = null;
        NamedParameterJdbcTemplate paramJdbcTemplate = new NamedParameterJdbcTemplate(Objects.requireNonNull(jdbcTemplate.getDataSource()));

        StringJoiner where = new StringJoiner(" AND ", " WHERE ", "").setEmptyValue("");
        if(saleDateFrom != null)
            where.add("date_of_sale >= :saleDateFrom");
        if(saleDateTo != null)
            where.add("date_of_sale <= :saleDateTo");
        if(gameNo != null)
            where.add("game_no = :gameNo");

        String sql = "SELECT sum(sale_price) as sale_price, count(*) as cost_price FROM game_sales" + where + " ";

        Map<String, Object> params = new HashMap<>();
        if(saleDateFrom != null){
            LocalDateTime dateTime = LocalDateTime.parse(saleDateFrom, formatter);
            params.put("saleDateFrom", dateTime);
        }else {
            params.put("saleDateFrom", null);
        }
        if(saleDateTo != null){
            LocalDateTime dateTime = LocalDateTime.parse(saleDateTo, formatter);
            params.put("saleDateTo", dateTime);
        }else {
            params.put("saleDateTo", null);
        }
        params.put("gameNo", gameNo);

        logger.info("SQL : " + sql);

        try {
            totalSales = paramJdbcTemplate.query(sql, params, new TotalSalesMapper());
        }catch(Exception e){
            logger.error(e.getMessage(), e);
        }
        return totalSales;
    }

    public void batchInsert(List<Game> games){
        jdbcTemplate.batchUpdate("INSERT INTO game_sales(id, game_no, game_name, game_code, type, cost_price, tax, sale_price, date_of_sale) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, games.get(i).getId());
                ps.setInt(2, games.get(i).getGame_no());
                ps.setString(3, games.get(i).getGame_name());
                ps.setString(4, games.get(i).getGame_code());
                ps.setInt(5, games.get(i).getType());
                ps.setDouble(6, games.get(i).getCost_price());
                ps.setDouble(7, games.get(i).getTax());
                ps.setDouble(8, games.get(i).getSale_price());
                ps.setTimestamp(9, Timestamp.valueOf(games.get(i).getDate_of_sale().toString()));
            }

            @Override
            public int getBatchSize() {
                return games.size();
            }
        });
    }
    private static final class GameMapper implements RowMapper<Game> {

        @Override
        public Game mapRow(ResultSet rs, int rowNum) throws SQLException {
            Game game = new Game();
            game.setId(rs.getInt("id"));
            game.setGame_no(rs.getInt("game_no"));
            game.setGame_name(rs.getString("game_name"));
            game.setGame_code(rs.getString("game_code"));
            game.setType(rs.getInt("type"));
            game.setCost_price(rs.getDouble("cost_price"));
            game.setTax(rs.getDouble("tax"));
            game.setSale_price(rs.getDouble("sale_price"));
            game.setDate_of_sale(rs.getTimestamp("date_of_sale"));
            return game;
        }
    }

    private static final class TotalSalesMapper implements RowMapper<TotalSales> {
        @Override
        public TotalSales mapRow(ResultSet rs, int rowNum) throws SQLException {
            TotalSales totalSales = new TotalSales();
            totalSales.setTotalNumberOfGamesSold(rs.getBigDecimal("cost_price"));
            totalSales.setTotalSalesGenerated(rs.getBigDecimal("sale_price"));
            return totalSales;
        }
    }
}
