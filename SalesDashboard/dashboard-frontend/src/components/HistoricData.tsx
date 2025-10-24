import TopCategoriesChart from "./TopCategoriesChart.tsx";
import TopProductsChart from "./TopProductsChart.tsx";
import TimeSeriesChart from "./TimeSeriesChart.tsx";

export default function HistoricData() {
    //TODO: reformat this to be a grid of cards
    return (
        <>
            <TimeSeriesChart/>
            <TopCategoriesChart/>
            <TopProductsChart/>
        </>
    )
}