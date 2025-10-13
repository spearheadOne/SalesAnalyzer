import Period from "./Period.tsx";
import Limit from "./Limit.tsx";
import FetchButton from "./FetchButton.tsx";
import {usePeriodStore} from "../store/periodStore.ts";
import {useLimitStore} from "../store/limitStore.ts";


interface DataControlProps {
    fetchData: (period: string, limit?: number) => void | Promise<void>;
    limitEnabled: boolean;
}


export default function DashboardControls({
                                              fetchData,
                                              limitEnabled = true
                                          }: DataControlProps) {

    const period = usePeriodStore((state) => state.period);
    const limit = useLimitStore((state) => state.limit);

    const fetch = () => fetchData(period, limitEnabled ? limit : undefined);
    return (

        <div className="d-flex align-items-center gap-2 mb-3">
            <Period/>
            {limitEnabled && <Limit/>}
            <FetchButton fetchData={fetch}/>
        </div>
    )
}