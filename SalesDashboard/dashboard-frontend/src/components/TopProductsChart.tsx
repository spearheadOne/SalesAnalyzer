import {useHistoricDataStore} from "../store/historicDataStore.ts";
import {useMemo} from "react";
import {DataCard} from "./DataCard.tsx";
import {Area, Bar, CartesianGrid, ComposedChart, Line, ResponsiveContainer, Tooltip, XAxis, YAxis} from "recharts";

//use for testing without backend
//@ts-ignore
import {MOCK_PRODUCT_DATA} from "../util/mockData.ts";
import {formatRevenue, getCurrency} from "../util/util.ts";

export default function TopProductsChart() {
    const productsResponse = useHistoricDataStore((state) => state.productsResponse);
    const fetchProductsRevenue = useHistoricDataStore((state) => state.fetchProductsRevenue);


    const data = useMemo(() =>
            (productsResponse?.items ?? []).map(d => ({
                ...d,
                revenue: Number(d.revenue), // must be numeric
            })),
        [productsResponse]
    );
    const currency = getCurrency(
        productsResponse?.defaultCurrency ||
        data?.[0]?.currency ||
        'EUR'
    );


    //use for testing without backend
    //@ts-ignore
    // const data = useMemo(() =>
    //         (MOCK_PRODUCT_DATA.items ?? []).map(d => ({
    //             ...d,
    //             revenue: Number(d.revenue), // must be numeric
    //         })),
    //     []
    // );
    // const currency = getCurrency(MOCK_PRODUCT_DATA?.defaultCurrency ?? data[0]?.currency);

    return (
        <DataCard title={"Top products for "}
                  dataCount={data.length}
                  limitEnabled={true}
                  fetchData={(p, l) => fetchProductsRevenue(p, l ?? 10)}
                  children={
                      <ResponsiveContainer width="100%" height="100%">
                          <ComposedChart data={data}
                                         margin={{top: 8, right: 16, left: 8, bottom: 100}}
                          >
                              <CartesianGrid strokeDasharray="3 3"/>
                              <XAxis dataKey="productName"
                                     textAnchor="end"
                                     interval={0}
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
                              <Area
                                  yAxisId="right"
                                  type="monotone"
                                  dataKey="units"
                                  name="Units"
                                  strokeWidth={2}
                                  fillOpacity={0.2}
                              />
                              <Bar
                                  yAxisId="left"
                                  dataKey="revenue"
                                  name="Revenue"
                                  barSize={22}
                              />
                              <Line
                                  yAxisId="right"
                                  type="monotone"
                                  dataKey="orders"
                                  name="Orders"
                                  dot={false}
                                  strokeWidth={2}
                              />
                          </ComposedChart>
                      </ResponsiveContainer>
                  }
        />

    )

}