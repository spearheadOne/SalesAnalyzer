import {useHistoricDataStore} from "../store/historicDataStore.ts";
import DashboardControls from "./DashboardControls.tsx";

export default function TopCategoriesChart() {

    // const categoryResponse = useHistoricDataStore((state) => state.categoryResponse);
     const fetchCategoryRevenue = useHistoricDataStore((state) => state.fetchCategoryRevenue);

    return (
         <>
             <DashboardControls
                 fetchData={(p, l) => fetchCategoryRevenue(p, l ?? 10)}
                 limitEnabled
             />
         </>
    )

}