package org.abondar.experimental.sales.analyzer.dashboard.data

import org.abondar.experimental.sales.analyzer.data.AggRow
import org.apache.ibatis.annotations.Delete
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper

@Mapper
interface SalesDashboardTestMapper {

    @Insert("""
    INSERT INTO sales_agg (
        event_time,
        product_id,
        product_name,
        category,
        orders,
        units,
        revenue,
        currency
    ) VALUES (
        #{eventTime},
        #{productId},
        #{productName},
        #{category},
        #{orders},
        #{units},
        #{revenue},
        #{currency}
    )
""")
    fun insertAgg(agg: AggRow);

    @Delete("delete from sales_agg")
    fun deleteAll();
}