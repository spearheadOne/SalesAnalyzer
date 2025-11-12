import {useHistoricDataStore} from "../store/historicDataStore.ts";
import {useMemo} from "react";
import {Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis} from "recharts";
import {DataCard} from "./DataCard.tsx";

//use for testing without a backend
//@ts-ignore
import {MOCK_CATEGORY_DATA} from "../util/mockData.ts";
import {formatRevenue, getCurrency} from "../util/util.ts";

export default function TopCategoriesChart() {
    const categoryResponse = useHistoricDataStore((state) => state.categoryResponse);
    const fetchCategoryRevenue = useHistoricDataStore((state) => state.fetchCategoryRevenue);
    const data = useMemo(
        () => categoryResponse?.items ?? [],
        [categoryResponse]
    );
    const currency = getCurrency(
        categoryResponse?.defaultCurrency ||
        data?.[0]?.currency ||
        'EUR'
    );

    //use for testing without a backend
    //@ts-ignore
    // const data = useMemo(
    //     () => MOCK_CATEGORY_DATA.items ?? [],
    //     [MOCK_CATEGORY_DATA]
    // );
    // const currency = getCurrency(MOCK_CATEGORY_DATA?.defaultCurrency ?? data[0]?.currency)

    return (
        <DataCard title={"Top categories for "}
                  dataCount={data.length}
                  limitEnabled={true}
                  fetchData={(p, l) => fetchCategoryRevenue(p, l ?? 10)}
                  children={
                      <ResponsiveContainer width="100%" height="100%">
                          <BarChart data={data}
                                    layout="horizontal"
                                    margin={{top: 8, right: 16, left: 8, bottom: 100}}
                          >
                              <CartesianGrid strokeDasharray="3 3"/>
                              <XAxis type="category"
                                     dataKey="category"
                                     textAnchor="end"
                                     angle={-30}
                                     height={30}
                              />
                              <YAxis
                                  yAxisId="left"
                                  type="number"
                                  tickFormatter={(v) => formatRevenue(v, currency)}
                              />
                              <Tooltip
                                  formatter={(value: any, _name, info: any) => {
                                      const rowCurrency = info?.payload?.currency ?? currency;
                                      return [formatRevenue(Number(value), rowCurrency), 'Revenue'];
                                  }}
                              />
                              <Bar dataKey="revenue" fill="#8884d8"/>
                          </BarChart>
                      </ResponsiveContainer>
                  }
        />
    )
}