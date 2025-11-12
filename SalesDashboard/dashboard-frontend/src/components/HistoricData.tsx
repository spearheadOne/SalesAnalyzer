import TopCategoriesChart from "./TopCategoriesChart.tsx";
import TopProductsChart from "./TopProductsChart.tsx";
import TimeSeriesChart from "./TimeSeriesChart.tsx";

export default function HistoricData() {
    return (
        <>
            <TimeSeriesChart/>
            <TopCategoriesChart/>
            <TopProductsChart/>
        </>
    )
}