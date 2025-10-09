import Period from "./Period.tsx";
import Limit from "./Limit.tsx";

export default function TopCategoriesChart() {

    // const categoryResponse = useHistoricDataStore((state) => state.categoryResponse);
    // const fetchCategoryRevenue = useHistoricDataStore((state) => state.fetchCategoryRevenue);

    return (
        <>
            <Period/>
            <Limit/>
        </>
    )

}