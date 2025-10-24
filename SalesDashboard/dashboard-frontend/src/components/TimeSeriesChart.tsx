import {useHistoricDataStore} from "../store/historicDataStore.ts";
import {useMemo} from "react";
import {DataCard} from "./DataCard.tsx";
import {CartesianGrid, ResponsiveContainer, Scatter, ScatterChart, Tooltip, XAxis, YAxis} from "recharts";
import {formatCurrency, formatTime} from "../util/util.ts";

//use for testing without a backend
//@ts-ignore
import {MOCK_TIME_SERIES} from "../util/mockData.ts";
import ScatterTooltip from "./ScatterTooltip.tsx";


export default function TimeSeriesChart() {

    const timeSeriesResponse = useHistoricDataStore((state) => state.timeSeriesResponse);
    const fetchTimeSeries = useHistoricDataStore((state) => state.fetchTimeSeries);

    const data = useMemo(() => timeSeriesResponse ?? [], [timeSeriesResponse])

    //use for testing without a backend
    //ts-ignore
    //const data = useMemo(() => MOCK_TIME_SERIES, [])

    return (
        <DataCard title={"Product revenue over a period of"}
                  dataCount={data.length}
                  limitEnabled={false}
                  fetchData={(p) => fetchTimeSeries(p)}
                  children={
                      <ResponsiveContainer width="100%" height="100%">
                          <ScatterChart data={data} margin={{top: 8, right: 16, left: 8, bottom: 8}}>
                              <CartesianGrid strokeDasharray="3 3"/>
                              <XAxis dataKey="eventTime"
                                     type="number"
                                     domain={['dataMin', 'dataMax']}
                                     scale="time"
                                     tickFormatter={(value) => formatTime(new Date(value).toISOString())}
                              />
                              <YAxis
                                  type="number"
                                  dataKey="revenue"
                                  name="revenue"
                                  tickFormatter={(value) => formatCurrency(Number(value))}/>
                              <Tooltip cursor={{strokeDasharray: '3 3'}}
                                       content={({active, payload}) => {
                                           if (!active || !payload?.length) return null;
                                           return (
                                               <ScatterTooltip point={payload[0].payload}/>
                                           );
                                       }}
                              />
                              <Scatter name="Revenue" data={data} fill="#8884d8"/>
                          </ScatterChart>
                      </ResponsiveContainer>
                  }

        />
    )
}