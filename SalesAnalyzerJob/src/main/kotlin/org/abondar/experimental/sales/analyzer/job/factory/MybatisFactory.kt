package org.abondar.experimental.sales.analyzer.job.factory

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.ibatis.transaction.TransactionFactory
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory
import javax.sql.DataSource

@Factory
class MybatisFactory(
    private val dataSource: DataSource,
    @param:Value("\${mybatis.environment:default}") private val mybatisEnv: String
) {

    @Singleton
    fun SqlSessionFactory(): SqlSessionFactory {

        val transactionFactory: TransactionFactory = JdbcTransactionFactory()
        val mybatisEnvironment = Environment(mybatisEnv, transactionFactory, dataSource)
        val config = Configuration(mybatisEnvironment)
        config.addMappers("org.abondar.experimental.sales.analyzer.job.mapper")

        return SqlSessionFactoryBuilder().build(config)
    }

}