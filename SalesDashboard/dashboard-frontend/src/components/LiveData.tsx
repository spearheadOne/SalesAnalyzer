import {useLiveDataStore} from "../store/liveDataStore.ts";
import {useEffect, useMemo} from "react";
import {CartesianGrid, Legend, Line, LineChart, Tooltip, XAxis, YAxis} from "recharts";

//use for testing without a backend
//@ts-ignore
import {MOCK_AGG_ROWS} from "../util/mockData.ts";
import {LiveTooltip} from "./LiveTooltip.tsx";

export default function LiveData() {

    const liveData = useLiveDataStore((state) => state.liveData);
    const error = useLiveDataStore((state) => state.error);

    const listenStream = useLiveDataStore((state) => state.listenStream);
    useEffect(() => {
        void listenStream();
    }, [listenStream]);

    const data = useMemo(
        () => liveData ?? [],
        [liveData]
    );

    //use for testing without a backend
    //ts-ignore
    // const data = useMemo(
    //     () =>  MOCK_AGG_ROWS ?? [],
    //     [MOCK_AGG_ROWS]
    // );

    return (
        <div>
            {error && <div className="alert alert-danger mb-3">{error}</div>}
            {data.length === 0 ? (
                <div className="alert alert-info">No data available</div>
            ) : (
                <LineChart width={800} height={400} data={data}>
                    <CartesianGrid strokeDasharray="3 3"/>
                    <XAxis
                        dataKey="eventTime"
                        tickFormatter={(t) => new Date(t).toLocaleTimeString()}
                        label={{ value: "Time", position: "insideBottomRight", offset: -5 }}
                    />
                    <YAxis
                        yAxisId="left"
                        label={{
                            value: "Revenue",
                            angle: -90,
                            position: "insideLeft",
                        }}
                    />
                    <YAxis
                        yAxisId="right"
                        orientation="right"
                        label={{
                            value: "Orders",
                            angle: -90,
                            position: "insideRight",
                        }}
                    />
                    <Tooltip content={<LiveTooltip/>} />
                    <Legend/>
                    <Line
                        yAxisId="left"
                        type="monotone"
                        dataKey="revenue"
                        name="Revenue"
                        stroke="#8884d8"
                        dot={false}
                    />
                    <Line
                        yAxisId="right"
                        type="monotone"
                        dataKey="orders"
                        name="Orders"
                        stroke="#ff7300"
                        dot={false}
                    />
                </LineChart>
            )}
        </div>
    )
}