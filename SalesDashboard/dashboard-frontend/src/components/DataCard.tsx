import DashboardControls from "./DashboardControls.tsx";
import {type PeriodState} from "../store/periodState.ts";
import {useHistoricDataStore} from "../store/historicDataStore.ts";
import type {StoreApi, UseBoundStore} from "zustand";
import type {LimitState} from "../store/limitState.ts";

type DataCardProps = {
    title: string;
    children: React.ReactNode;
    dataCount: number
    limitEnabled: boolean
    fetchData: (period: string, limit?: number) => void | Promise<void>;
    periodStore: UseBoundStore<StoreApi<PeriodState>>;
    limitStore: UseBoundStore<StoreApi<LimitState>>;

}

export function DataCard({
                             periodStore,
                             limitStore,
                             title,
                             children,
                             dataCount,
                             limitEnabled,
                             fetchData
                         }: DataCardProps) {
    const displayPeriod = periodStore.getState().getDisplayPeriod();
    const error = useHistoricDataStore((state) => state.error);
    const isEmpty = dataCount === 0;


    return (
        <>
            <div className="card">
                <div className="card-header d-flex justify-content-between align-items-center">
                    <span>{title} {displayPeriod}</span>
                </div>

                <div className="card-body" style={{height: '360px'}}>
                    {error && <div className="alert alert-danger mb-3">{error}</div>}

                    {isEmpty ? (
                        <div className="alert alert-info">No data available</div>
                    ) : (
                        children
                    )}
                </div>
            </div>

            <DashboardControls
                periodStore={periodStore}
                limitStore={limitStore}
                fetchData={fetchData}
                limitEnabled={limitEnabled}
            />
        </>
    )
}