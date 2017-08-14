package org.jianyi.yiriyigu.repository;

import java.util.List;

import org.jianyi.yiriyigu.modle.Stock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface StockRepository extends CrudRepository<Stock, Long> {

    List<Stock> findByDayAndCode(String day, String code);

    List<Stock> findByCodeAndDayGreaterThanEqualAndDayLessThan(String code,
            String start, String end);

    @Modifying
    @Transactional
    @Query(value = "delete from Stock s where s.code = ?")
    void deleteByCode(String code);

}
