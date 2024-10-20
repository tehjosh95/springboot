package com.demo.model;

import java.math.BigDecimal;
import java.util.Date;

public class TotalSales {
    private BigDecimal totalNumberOfGamesSold;
    private BigDecimal totalSalesGenerated;

    public BigDecimal getTotalNumberOfGamesSold() {
        return totalNumberOfGamesSold;
    }

    public void setTotalNumberOfGamesSold(BigDecimal totalNumberOfGamesSold) {
        this.totalNumberOfGamesSold = totalNumberOfGamesSold;
    }

    public BigDecimal getTotalSalesGenerated() {
        return totalSalesGenerated;
    }

    public void setTotalSalesGenerated(BigDecimal totalSalesGenerated) {
        this.totalSalesGenerated = totalSalesGenerated;
    }
}
