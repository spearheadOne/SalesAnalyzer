import TopCategoriesChart from "./TopCategoriesChart.tsx";
import TopProductsChart from "./TopProductsChart.tsx";

export default function HistoricData() {
    return (
        <>
            <h1>Historic data</h1>
            <TopCategoriesChart/>
            <TopProductsChart/>
        </>
    )
}