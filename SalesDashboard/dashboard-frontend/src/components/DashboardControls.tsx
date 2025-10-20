import Period from "./Period.tsx";
import Limit from "./Limit.tsx";
import FetchButton from "./FetchButton.tsx";
import {type PeriodState} from "../store/periodState.ts";
import {type LimitState} from "../store/limitState.ts";
import type {StoreApi, UseBoundStore} from "zustand";


interface DataControlProps {
    periodStore: UseBoundStore<StoreApi<PeriodState>>;
    limitStore: UseBoundStore<StoreApi<LimitState>>;
    fetchData: (period: string, limit?: number) => void | Promise<void>;
    limitEnabled: boolean;
}


export default function DashboardControls({
                                              periodStore,
                                              limitStore,
                                              fetchData,
                                              limitEnabled = true
                                          }: DataControlProps) {

    const period = periodStore((state) => state.period);
    const limit = limitStore((state) => state.limit);

    const fetch = () => fetchData(period, limitEnabled ? limit : undefined);
    return (

        <div className="d-flex align-items-center gap-2 mb-3">
            <Period periodStore={periodStore}/>
            {limitEnabled && <Limit limitStore={limitStore}/>}
            <FetchButton fetchData={fetch}/>
        </div>
    )
}