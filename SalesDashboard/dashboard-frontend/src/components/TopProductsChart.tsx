import {useHistoricDataStore} from "../store/historicDataStore.ts";
import {useMemo} from "react";
import {DataCard} from "./DataCard.tsx";
import {
    Area,
    Bar,
    CartesianGrid,
    ComposedChart,
    Line,
    ResponsiveContainer,
    Tooltip,
    XAxis,
    YAxis
} from "recharts";
import {formatCurrency} from "../util/util.ts";

export default function TopProductsChart() {
    const productsResponse = useHistoricDataStore((state) => state.productsResponse);
    const fetchProductsRevenue = useHistoricDataStore((state) => state.fetchProductsRevenue);

    const data = useMemo(() => productsResponse ?? [], [productsResponse])

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
                              <YAxis yAxisId="left"
                                     tickFormatter={(value) => formatCurrency(value as number)}
                                     width={70}
                              />
                              <Tooltip formatter={(value, name) => {
                                  const v = Number(value);
                                  if (name === 'Revenue') return [`€${formatCurrency(value as number)}`, name];
                                  return [v.toLocaleString(), name as string];
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