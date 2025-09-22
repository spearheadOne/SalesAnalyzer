package org.abondar.experimental.sales.analyzer.job.data

import org.abondar.experimental.sales.analyzer.data.AggRow
import org.apache.ibatis.annotations.Delete
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface AggTestMapper {

    @Select(" select\n" +
            "            event_time as eventTime,\n" +
            "            product_id as productId,\n" +
            "            product_name as productName,\n" +
            "            category,\n" +
            "            orders,\n" +
            "            units,\n" +
            "            revenue\n" +
            "        from sales_agg")
    fun getAggregates(): List<AggRow>

    @Delete("delete from sales_agg")
    fun deleteAll()

}