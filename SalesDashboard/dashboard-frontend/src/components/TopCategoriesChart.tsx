import {useHistoricDataStore} from "../store/historicDataStore.ts";
import {useMemo} from "react";
import {Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis} from "recharts";
import {DataCard} from "./DataCard.tsx";
import {formatCurrency} from "../util/util.ts";

//use for testing without a backend
//@ts-ignore
import {MOCK_CATEGORY_DATA} from "../util/mockData.ts";

export default function TopCategoriesChart() {
    const categoryResponse = useHistoricDataStore((state) => state.categoryResponse);
    const fetchCategoryRevenue = useHistoricDataStore((state) => state.fetchCategoryRevenue);

    const data = useMemo(() => categoryResponse ?? [], [categoryResponse])

    //use for testing without a backend
    //@ts-ignore
    //const data = useMemo(() => MOCK_CATEGORY_DATA ?? [], [MOCK_CATEGORY_DATA])

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
                              <YAxis type="number"
                                     tickFormatter={(value) => formatCurrency(Number(value))}
                              />
                              <Tooltip formatter={(value) => [`€${formatCurrency(Number(value))}`, 'Revenue']}/>
                              <Bar dataKey="revenue" fill="#8884d8"/>
                          </BarChart>
                      </ResponsiveContainer>
                  }
        />
    )
}