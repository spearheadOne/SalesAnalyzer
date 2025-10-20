import {useHistoricDataStore} from "../store/historicDataStore.ts";
import {useMemo, useRef} from "react";
import {DataCard} from "./DataCard.tsx";
import {Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis} from "recharts";
import {formatCurrency} from "../util/util.ts";
import type {StoreApi, UseBoundStore} from "zustand";
import {type LimitState, makeLimitStore} from "../store/limitState.ts";
import {makePeriodStore, type PeriodState} from "../store/periodState.ts";

export default function TopProductsChart() {
    const productsResponse = useHistoricDataStore((state) => state.productsResponse);
    const fetchProductsRevenue = useHistoricDataStore((state) => state.fetchProductsRevenue);

    const data = useMemo(() => productsResponse ?? [], [productsResponse])

    const periodStoreRef = useRef<UseBoundStore<StoreApi<PeriodState>>>(makePeriodStore())
    const limitStoreRef  = useRef<UseBoundStore<StoreApi<LimitState>>>(makeLimitStore())
    const periodStore = periodStoreRef.current
    const limitStore  = limitStoreRef.current


    return (
        <DataCard title={"Top products for "}
                  dataCount={data.length}
                  limitEnabled={true}
                  fetchData={(p, l) => fetchProductsRevenue(p, l ?? 10)}
                  periodStore={periodStore}
                  limitStore={limitStore}
                  children={
                      <ResponsiveContainer width="100%" height="100%">
                          <BarChart data={data}
                                    layout="horizontal"
                                    margin={{top: 8, right: 16, left: 8, bottom: 100}}
                          >
                              <CartesianGrid strokeDasharray="3 3"/>
                              <XAxis dataKey="productName" textAnchor="end" angle={-30} height={30}/>
                              <YAxis tickFormatter={(value) => formatCurrency(value as number)}/>
                              <Tooltip formatter={(value) => [`€${formatCurrency(value as number)}`, 'Revenue']}/>
                              <Bar dataKey="revenue" fill="#8884d8"/>
                          </BarChart>
                      </ResponsiveContainer>
                  }
        />
    )

}