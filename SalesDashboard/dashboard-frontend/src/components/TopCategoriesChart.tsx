import Period from "./Period.tsx";
import Limit from "./Limit.tsx";
import FetchButton from "./FetchButton.tsx";
import {useHistoricDataStore} from "../store/historicDataStore.ts";

export default function TopCategoriesChart() {

    // const categoryResponse = useHistoricDataStore((state) => state.categoryResponse);
     const fetchCategoryRevenue = useHistoricDataStore((state) => state.fetchCategoryRevenue);

    return (
        <>
            <Period/>
            <Limit/>
            <FetchButton fetchData={()=>fetchCategoryRevenue("1h",10)}/>
        </>
    )

}