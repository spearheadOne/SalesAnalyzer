import DashboardControls from "./DashboardControls.tsx";
import {createPeriodStore, PeriodStoreContext} from "../store/periodStore.ts";
import {createLimitStore, LimitStoreContext} from "../store/limitStore.ts";
import {useRef, useState} from "react";
import {useStoreSelector} from "../util/util.ts";

type DataCardProps = {
    title: string;
    children: React.ReactNode;
    dataCount: number
    limitEnabled: boolean
    fetchData: (period: string, limit?: number) => void | Promise<void>;
}

export function DataCard({
                             title,
                             children,
                             dataCount,
                             limitEnabled,
                             fetchData
                         }: DataCardProps) {


    const periodStoreRef = useRef<ReturnType<typeof createPeriodStore>>(null);
    if (!periodStoreRef.current) periodStoreRef.current = createPeriodStore();

    const limitStoreRef = useRef<ReturnType<typeof createLimitStore>>(null);
    if (!limitStoreRef.current) limitStoreRef.current = createLimitStore();


    const displayPeriod = useStoreSelector(periodStoreRef.current, s => s.getDisplayPeriod());
    const [error, setError] = useState<string | undefined>();
    const isEmpty = dataCount === 0;

    const handleFetchData = async (period: string, limit?: number) => {
        setError(undefined);
        try {
            await fetchData(period, limit);
        } catch (err: any) {
            setError(err?.message || 'Request failed');
        }
    };


    return (
        <>
            <PeriodStoreContext.Provider value={periodStoreRef.current}>
                <LimitStoreContext.Provider value={limitStoreRef.current}>
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

                    <div className="mt-3">
                        <DashboardControls
                            fetchData={handleFetchData}
                            limitEnabled={limitEnabled}
                        />
                    </div>
                </LimitStoreContext.Provider>
            </PeriodStoreContext.Provider>
        </>
    )
}